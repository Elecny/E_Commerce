package com.example.e_commerce.service.ProductService;


import com.example.e_commerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductService {
    
    // ========== 基础CRUD操作 ==========
    
    /**
     * 创建商品
     */
    Product createProduct(Product product);
    
    /**
     * 更新商品
     */
    Product updateProduct(Product product);
    
    /**
     * 根据ID查询商品
     */
    Product getProductById(Long id);
    
    /**
     * 根据ID查询上架的商品（前台使用）
     */
    Product getOnSaleProductById(Long id);
    
    /**
     * 删除商品（物理删除）
     */
    void deleteProduct(Long id);
    
    /**
     * 逻辑删除商品
     */
    void logicDeleteProduct(Long id);
    
    /**
     * 批量逻辑删除商品
     */
    void batchLogicDeleteProducts(List<Long> ids);
    
    // ========== 查询操作 ==========
    
    /**
     * 查询所有商品（分页）
     */
    Page<Product> getAllProducts(Pageable pageable);
    
    /**
     * 查询上架商品（分页）
     */
    Page<Product> getOnSaleProducts(Pageable pageable);
    
    /**
     * 根据分类查询商品
     */
    Page<Product> getProductsByCategory(Long categoryId, Pageable pageable);
    
    /**
     * 根据分类查询上架商品
     */
    Page<Product> getOnSaleProductsByCategory(Long categoryId, Pageable pageable);
    
    /**
     * 搜索商品（根据名称）
     */
    Page<Product> searchProducts(String keyword, Pageable pageable);
    
    /**
     * 搜索上架商品（根据名称）
     */
    Page<Product> searchOnSaleProducts(String keyword, Pageable pageable);
    
    /**
     * 多条件查询商品
     */
    Page<Product> searchProductsByConditions(String name, Long categoryId, String status,
                                            BigDecimal minPrice, BigDecimal maxPrice,
                                            Integer minStock, Integer maxStock, Pageable pageable);
    
    /**
     * 根据价格区间查询商品
     */
    Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    // ========== 库存管理 ==========
    
    /**
     * 减少库存
     */
    void decreaseStock(Long productId, Integer quantity);
    
    /**
     * 增加库存
     */
    void increaseStock(Long productId, Integer quantity);
    
    /**
     * 检查库存是否充足
     */
    boolean checkStock(Long productId, Integer quantity);
    
    /**
     * 获取库存不足的商品列表
     */
    List<Product> getLowStockProducts(Integer threshold);
    
    /**
     * 批量更新库存
     */
    void batchUpdateStock(Map<Long, Integer> stockUpdates);
    
    // ========== 销量和浏览管理 ==========
    
    /**
     * 增加销量
     */
    void increaseSalesCount(Long productId, Integer quantity);
    
    /**
     * 增加浏览量
     */
    void increaseViewCount(Long productId);
    
    /**
     * 获取销量排行榜
     */
    List<Product> getTopSalesProducts(Integer limit);
    
    /**
     * 获取浏览排行榜
     */
    List<Product> getTopViewProducts(Integer limit);
    
    // ========== 商品状态管理 ==========
    
    /**
     * 上架商品
     */
    void putOnSale(Long productId);
    
    /**
     * 下架商品
     */
    void putOffSale(Long productId);
    
    /**
     * 批量上架商品
     */
    void batchPutOnSale(List<Long> productIds);
    
    /**
     * 批量下架商品
     */
    void batchPutOffSale(List<Long> productIds);
    
    // ========== 统计查询 ==========
    
    /**
     * 获取商品总数
     */
    long getTotalProductCount();
    
    /**
     * 获取上架商品数量
     */
    long getOnSaleProductCount();
    
    /**
     * 获取分类下的商品数量
     */
    long getProductCountByCategory(Long categoryId);
    
    /**
     * 获取库存为0的商品数量
     */
    long getOutOfStockCount();
    
    /**
     * 获取价格统计信息
     */
    Map<String, Object> getPriceStatistics();
    
    /**
     * 获取各分类商品数量统计
     */
    Map<Long, Long> getProductCountByCategory();
    
    // ========== 验证和检查 ==========
    
    /**
     * 验证商品是否存在
     */
    void validateProductExists(Long productId);
    
    /**
     * 验证商品是否可购买（存在且上架且有库存）
     */
    void validateProductPurchasable(Long productId, Integer quantity);
    
    /**
     * 检查商品名称是否重复
     */
    boolean isProductNameDuplicate(String name, Long excludeId);
}