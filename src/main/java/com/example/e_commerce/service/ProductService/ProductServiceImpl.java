package com.example.e_commerce.service.ProductService;

import com.example.e_commerce.entity.Product;
import com.example.e_commerce.entity.elasticsearch.ProductDocument;
import com.example.e_commerce.exception.BusinessException;
import com.example.e_commerce.exception.ResourceNotFoundException;
import com.example.e_commerce.repository.ProductDocumentRepository;
import com.example.e_commerce.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    
    // 商品状态常量
    public static final String STATUS_ON_SALE = "ON_SALE";
    public static final String STATUS_OFF_SALE = "OFF_SALE";
    public static final String STATUS_DELETED = "DELETED";
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDocumentRepository productDocumentRepository;
    
    // ========== 基础CRUD操作 ==========
    
    @Override
    public Product createProduct(Product product) {
        logger.info("创建商品: {}", product.getName());
        
        // 验证商品信息
        validateProduct(product);
        
        // 设置默认值
        if (product.getStatus() == null) {
            product.setStatus(STATUS_OFF_SALE);
        }
        if (product.getSalesCount() == null) {
            product.setSalesCount(0);
        }
        if (product.getViewCount() == null) {
            product.setViewCount(0);
        }
        if (product.getSortOrder() == null) {
            product.setSortOrder(0);
        }
        
        Product savedProduct = productRepository.save(product);
        logger.info("商品创建成功, ID: {}", savedProduct.getId());
        
        return savedProduct;
    }
    
    @Override
    public Product updateProduct(Product product) {
        logger.info("更新商品, ID: {}", product.getId());
        
        // 检查商品是否存在
        Product existingProduct = getProductById(product.getId());
        
        // 更新字段
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setOriginalPrice(product.getOriginalPrice());
        existingProduct.setStock(product.getStock());
        existingProduct.setCategoryId(product.getCategoryId());
        existingProduct.setMainImage(product.getMainImage());
        existingProduct.setImages(product.getImages());
        existingProduct.setWeight(product.getWeight());
        existingProduct.setUnit(product.getUnit());
        existingProduct.setSortOrder(product.getSortOrder());
        
        // 状态更新需要特殊处理（不允许从DELETED恢复）
        if (product.getStatus() != null && !STATUS_DELETED.equals(existingProduct.getStatus())) {
            existingProduct.setStatus(product.getStatus());
        }
        
        Product updatedProduct = productRepository.save(existingProduct);
        logger.info("商品更新成功, ID: {}", updatedProduct.getId());
        
        return updatedProduct;
    }
    
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("商品不存在, ID: " + id));
    }
    
    @Override
    public Product getOnSaleProductById(Long id) {
        Product product = getProductById(id);
        if (!STATUS_ON_SALE.equals(product.getStatus())) {
            throw new BusinessException("商品未上架，无法查看");
        }
        return product;
    }
    
    @Override
    public void deleteProduct(Long id) {
        logger.info("物理删除商品, ID: {}", id);
        Product product = getProductById(id);
        productRepository.delete(product);
        logger.info("商品删除成功, ID: {}", id);
    }
    
    @Override
    public void logicDeleteProduct(Long id) {
        logger.info("逻辑删除商品, ID: {}", id);
        Product product = getProductById(id);
        product.setStatus(STATUS_DELETED);
        productRepository.save(product);
        logger.info("商品逻辑删除成功, ID: {}", id);
    }
    
    @Override
    public void batchLogicDeleteProducts(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        logger.info("批量逻辑删除商品, IDs: {}", ids);
        int count = productRepository.batchDelete(ids);
        logger.info("批量逻辑删除成功, 数量: {}", count);
    }
    
    // ========== 查询操作 ==========
    
    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    
    @Override
    public Page<Product> getOnSaleProducts(Pageable pageable) {
        return productRepository.findByStatus(STATUS_ON_SALE, pageable);
    }
    
    @Override
    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }
    
    @Override
    public Page<Product> getOnSaleProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndStatus(categoryId, STATUS_ON_SALE, pageable);
    }
    
    @Override
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        if (!StringUtils.hasText(keyword)) {
            return productRepository.findAll(pageable);
        }
        return productRepository.findByNameContaining(keyword, pageable);
    }
    
    @Override
    public Page<Product> searchOnSaleProducts(String keyword, Pageable pageable) {
        if (!StringUtils.hasText(keyword)) {
            return productRepository.findByStatus(STATUS_ON_SALE, pageable);
        }
        return productRepository.findByNameContainingAndStatus(keyword, STATUS_ON_SALE, pageable);
    }
    
    @Override
    public Page<Product> searchProductsByConditions(String name, Long categoryId, String status,
                                                    BigDecimal minPrice, BigDecimal maxPrice,
                                                    Integer minStock, Integer maxStock, Pageable pageable) {
        return productRepository.findByConditions(name, categoryId, status, minPrice, maxPrice, minStock, maxStock, pageable);
    }
    
    @Override
    public Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        if (minPrice == null) minPrice = BigDecimal.ZERO;
        if (maxPrice == null) maxPrice = new BigDecimal("999999999");
        return productRepository.findByPriceBetweenAndStatus(minPrice, maxPrice, STATUS_ON_SALE, pageable);
    }
    
    // ========== 库存管理 ==========
    
    @Override
    @Transactional
    public void decreaseStock(Long productId, Integer quantity) {
        logger.info("减少商品库存, ProductId: {}, Quantity: {}", productId, quantity);
        
        if (quantity <= 0) {
            throw new BusinessException("减少库存数量必须大于0");
        }
        
        int updatedCount = productRepository.decreaseStock(productId, quantity);
        if (updatedCount == 0) {
            Product product = getProductById(productId);
            if (product.getStock() < quantity) {
                throw new BusinessException("库存不足，当前库存: " + product.getStock() + ", 需要: " + quantity);
            }
            throw new BusinessException("减少库存失败");
        }
        
        logger.info("库存减少成功, ProductId: {}, Quantity: {}", productId, quantity);
    }
    
    @Override
    @Transactional
    public void increaseStock(Long productId, Integer quantity) {
        logger.info("增加商品库存, ProductId: {}, Quantity: {}", productId, quantity);
        
        if (quantity <= 0) {
            throw new BusinessException("增加库存数量必须大于0");
        }
        
        int updatedCount = productRepository.increaseStock(productId, quantity);
        if (updatedCount == 0) {
            throw new BusinessException("增加库存失败");
        }
        
        logger.info("库存增加成功, ProductId: {}, Quantity: {}", productId, quantity);
    }
    
    @Override
    public boolean checkStock(Long productId, Integer quantity) {
        Product product = getProductById(productId);
        return product.getStock() >= quantity;
    }
    
    @Override
    public List<Product> getLowStockProducts(Integer threshold) {
        if (threshold == null) threshold = 10;
        return productRepository.findByStockLessThanAndStatus(threshold, STATUS_ON_SALE);
    }
    
    @Override
    @Transactional
    public void batchUpdateStock(Map<Long, Integer> stockUpdates) {
        logger.info("批量更新库存, 数量: {}", stockUpdates.size());
        
        for (Map.Entry<Long, Integer> entry : stockUpdates.entrySet()) {
            Long productId = entry.getKey();
            Integer newStock = entry.getValue();
            
            Product product = getProductById(productId);
            if (newStock < 0) {
                throw new BusinessException("库存不能为负数, ProductId: " + productId);
            }
            
            product.setStock(newStock);
            productRepository.save(product);
        }
        
        logger.info("批量更新库存完成");
    }
    
    // ========== 销量和浏览管理 ==========
    
    @Override
    @Transactional
    public void increaseSalesCount(Long productId, Integer quantity) {
        logger.info("增加商品销量, ProductId: {}, Quantity: {}", productId, quantity);
        
        if (quantity <= 0) {
            throw new BusinessException("增加销量数量必须大于0");
        }
        
        int updatedCount = productRepository.increaseSalesCount(productId, quantity);
        if (updatedCount == 0) {
            throw new BusinessException("增加销量失败");
        }
        
        logger.info("销量增加成功, ProductId: {}, Quantity: {}", productId, quantity);
    }
    
    @Override
    @Transactional
    public void increaseViewCount(Long productId) {
        productRepository.increaseViewCount(productId);
        logger.debug("浏览量增加, ProductId: {}", productId);
    }
    
    @Override
    public List<Product> getTopSalesProducts(Integer limit) {
        if (limit == null) limit = 10;
        return productRepository.findTopSalesProducts(STATUS_ON_SALE, limit);
    }

    @Override
    public List<Product> getTopViewProducts(Integer limit) {
        if (limit == null) limit = 10;
        return productRepository.findTop10ByStatusOrderByViewCountDesc(STATUS_ON_SALE);
    }
    
    // ========== 商品状态管理 ==========
    
    @Override
    public void putOnSale(Long productId) {
        logger.info("上架商品, ID: {}", productId);
        Product product = getProductById(productId);
        
        if (STATUS_DELETED.equals(product.getStatus())) {
            throw new BusinessException("已删除的商品无法上架");
        }
        
        product.setStatus(STATUS_ON_SALE);
        productRepository.save(product);
        logger.info("商品上架成功, ID: {}", productId);
    }
    
    @Override
    public void putOffSale(Long productId) {
        logger.info("下架商品, ID: {}", productId);
        Product product = getProductById(productId);
        product.setStatus(STATUS_OFF_SALE);
        productRepository.save(product);
        logger.info("商品下架成功, ID: {}", productId);
    }
    
    @Override
    public void batchPutOnSale(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return;
        }
        logger.info("批量上架商品, IDs: {}", productIds);
        int count = productRepository.batchUpdateStatus(productIds, STATUS_ON_SALE);
        logger.info("批量上架成功, 数量: {}", count);
    }
    
    @Override
    public void batchPutOffSale(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return;
        }
        logger.info("批量下架商品, IDs: {}", productIds);
        int count = productRepository.batchUpdateStatus(productIds, STATUS_OFF_SALE);
        logger.info("批量下架成功, 数量: {}", count);
    }
    
    // ========== 统计查询 ==========
    
    @Override
    public long getTotalProductCount() {
        return productRepository.count();
    }
    
    @Override
    public long getOnSaleProductCount() {
        return productRepository.countByStatus(STATUS_ON_SALE);
    }
    
    @Override
    public long getProductCountByCategory(Long categoryId) {
        return productRepository.countByCategoryId(categoryId);
    }
    
    @Override
    public long getOutOfStockCount() {
        return productRepository.countByStock(0);
    }
    
    @Override
    public Map<String, Object> getPriceStatistics() {
        Object[] stats = productRepository.getPriceStatistics();
        Map<String, Object> result = new HashMap<>();
        
        if (stats != null && stats.length == 3) {
            result.put("minPrice", stats[0] != null ? stats[0] : BigDecimal.ZERO);
            result.put("maxPrice", stats[1] != null ? stats[1] : BigDecimal.ZERO);
            result.put("avgPrice", stats[2] != null ? stats[2] : BigDecimal.ZERO);
        }
        
        return result;
    }
    
    @Override
    public Map<Long, Long> getProductCountByCategory() {
        List<Object[]> results = productRepository.countProductsByCategory();
        Map<Long, Long> categoryCountMap = new HashMap<>();
        
        for (Object[] result : results) {
            Long categoryId = (Long) result[0];
            Long count = (Long) result[1];
            categoryCountMap.put(categoryId, count);
        }
        
        return categoryCountMap;
    }
    
    // ========== 验证和检查 ==========
    
    @Override
    public void validateProductExists(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("商品不存在, ID: " + productId);
        }
    }
    
    @Override
    public void validateProductPurchasable(Long productId, Integer quantity) {
        Product product = getProductById(productId);
        
        if (!STATUS_ON_SALE.equals(product.getStatus())) {
            throw new BusinessException("商品未上架，无法购买");
        }
        
        if (product.getStock() < quantity) {
            throw new BusinessException("库存不足，当前库存: " + product.getStock() + ", 需要: " + quantity);
        }
    }
    
    @Override
    public boolean isProductNameDuplicate(String name, Long excludeId) {
        if (!StringUtils.hasText(name)) {
            return false;
        }
        return productRepository.existsByNameAndIdNot(name, excludeId);
    }
    
    // ========== 私有辅助方法 ==========
    
    /**
     * 验证商品信息
     */
    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new BusinessException("商品名称不能为空");
        }
        
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("商品价格必须大于0");
        }
        
        if (product.getStock() == null || product.getStock() < 0) {
            throw new BusinessException("商品库存不能为负数");
        }
        
        if (product.getCategoryId() == null) {
            throw new BusinessException("商品分类不能为空");
        }
        
        // 检查名称是否重复
        if (isProductNameDuplicate(product.getName(), product.getId())) {
            throw new BusinessException("商品名称已存在: " + product.getName());
        }
    }
}