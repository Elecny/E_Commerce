package com.example.e_commerce.service.ProductService;

import com.example.e_commerce.entity.Product;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductService {
    
    // ========== 基础CRUD操作 ==========
    
    @CacheEvict(value = {"productList", "categoryProducts", "searchResults", "hotProducts", "productStats"}, 
                allEntries = true)
    Product createProduct(Product product);
    
    @Caching(evict = {
        @CacheEvict(value = "productDetail", key = "#product.id"),
        @CacheEvict(value = {"productList", "categoryProducts", "searchResults", "hotProducts", "productStats"}, 
                   allEntries = true)
    })
    @CachePut(value = "productDetail", key = "#result.id")
    Product updateProduct(Product product);
    
    @Cacheable(value = "productDetail", key = "#id", unless = "#result == null")
    Product getProductById(Long id);
    
    @Cacheable(value = "productDetail", key = "'onsale_' + #id", unless = "#result == null")
    Product getOnSaleProductById(Long id);
    
    @Caching(evict = {
        @CacheEvict(value = "productDetail", key = "#id"),
        @CacheEvict(value = {"productList", "categoryProducts", "searchResults", "hotProducts", "productStats"}, 
                   allEntries = true)
    })
    void deleteProduct(Long id);
    
    @Caching(evict = {
        @CacheEvict(value = "productDetail", key = "#id"),
        @CacheEvict(value = {"productList", "categoryProducts", "searchResults", "hotProducts", "productStats"}, 
                   allEntries = true)
    })
    void logicDeleteProduct(Long id);
    
    @CacheEvict(value = {"productList", "categoryProducts", "searchResults", "hotProducts", "productStats"}, 
                allEntries = true)
    void batchLogicDeleteProducts(List<Long> ids);
    
    // ========== 查询操作 ==========
    
    @Cacheable(value = "productList", key = "#pageable.pageNumber + '_' + #pageable.pageSize", 
               unless = "#result == null || #result.isEmpty()")
    Page<Product> getAllProducts(Pageable pageable);
    
    @Cacheable(value = "productList", key = "'onsale_' + #pageable.pageNumber + '_' + #pageable.pageSize", 
               unless = "#result == null || #result.isEmpty()")
    Page<Product> getOnSaleProducts(Pageable pageable);
    
    @Cacheable(value = "categoryProducts", key = "#categoryId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize", 
               unless = "#result == null || #result.isEmpty()")
    Page<Product> getProductsByCategory(Long categoryId, Pageable pageable);
    
    @Cacheable(value = "categoryProducts", key = "'onsale_' + #categoryId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize", 
               unless = "#result == null || #result.isEmpty()")
    Page<Product> getOnSaleProductsByCategory(Long categoryId, Pageable pageable);
    
    @Cacheable(value = "searchResults", key = "#keyword + '_' + #pageable.pageNumber + '_' + #pageable.pageSize", 
               unless = "#result == null || #result.isEmpty()")
    Page<Product> searchProducts(String keyword, Pageable pageable);
    
    @Cacheable(value = "searchResults", key = "'onsale_' + #keyword + '_' + #pageable.pageNumber + '_' + #pageable.pageSize", 
               unless = "#result == null || #result.isEmpty()")
    Page<Product> searchOnSaleProducts(String keyword, Pageable pageable);
    
    @Cacheable(value = "searchResults", key = "#name + '_' + #categoryId + '_' + #status + '_' + #minPrice + '_' + #maxPrice + '_' + #minStock + '_' + #maxStock + '_' + #pageable.pageNumber + '_' + #pageable.pageSize", 
               unless = "#result == null || #result.isEmpty()")
    Page<Product> searchProductsByConditions(String name, Long categoryId, String status,
                                            BigDecimal minPrice, BigDecimal maxPrice,
                                            Integer minStock, Integer maxStock, Pageable pageable);
    
    @Cacheable(value = "searchResults", key = "#minPrice + '_' + #maxPrice + '_' + #pageable.pageNumber + '_' + #pageable.pageSize", 
               unless = "#result == null || #result.isEmpty()")
    Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    // ========== 库存管理 ==========
    
    @Caching(evict = {
        @CacheEvict(value = "productDetail", key = "#productId"),
        @CacheEvict(value = {"productList", "categoryProducts", "searchResults", "hotProducts"}, 
                   allEntries = true)
    })
    void decreaseStock(Long productId, Integer quantity);
    
    @Caching(evict = {
        @CacheEvict(value = "productDetail", key = "#productId"),
        @CacheEvict(value = {"productList", "categoryProducts", "searchResults", "hotProducts"}, 
                   allEntries = true)
    })
    void increaseStock(Long productId, Integer quantity);
    
    boolean checkStock(Long productId, Integer quantity);
    
    @Cacheable(value = "productList", key = "'lowStock_' + #threshold", unless = "#result == null || #result.isEmpty()")
    List<Product> getLowStockProducts(Integer threshold);
    
    @Caching(evict = {
        @CacheEvict(value = "productDetail", key = "#stockUpdates.keySet()"),
        @CacheEvict(value = {"productList", "categoryProducts", "searchResults", "hotProducts"}, 
                   allEntries = true)
    })
    void batchUpdateStock(Map<Long, Integer> stockUpdates);
    
    // ========== 销量和浏览管理 ==========
    
    @Caching(evict = {
        @CacheEvict(value = "productDetail", key = "#productId"),
        @CacheEvict(value = {"hotProducts", "productStats"}, allEntries = true)
    })
    void increaseSalesCount(Long productId, Integer quantity);
    
    @CacheEvict(value = "productDetail", key = "#productId")
    void increaseViewCount(Long productId);
    
    @Cacheable(value = "hotProducts", key = "'topSales_' + #limit", unless = "#result == null || #result.isEmpty()")
    List<Product> getTopSalesProducts(Integer limit);
    
    @Cacheable(value = "hotProducts", key = "'topView_' + #limit", unless = "#result == null || #result.isEmpty()")
    List<Product> getTopViewProducts(Integer limit);
    
    // ========== 商品状态管理 ==========
    
    @Caching(evict = {
        @CacheEvict(value = "productDetail", key = "#productId"),
        @CacheEvict(value = {"productList", "categoryProducts", "searchResults", "hotProducts", "productStats"}, 
                   allEntries = true)
    })
    void putOnSale(Long productId);
    
    @Caching(evict = {
        @CacheEvict(value = "productDetail", key = "#productId"),
        @CacheEvict(value = {"productList", "categoryProducts", "searchResults", "hotProducts", "productStats"}, 
                   allEntries = true)
    })
    void putOffSale(Long productId);
    
    @CacheEvict(value = {"productList", "categoryProducts", "searchResults", "hotProducts", "productStats"}, 
                allEntries = true)
    void batchPutOnSale(List<Long> productIds);
    
    @CacheEvict(value = {"productList", "categoryProducts", "searchResults", "hotProducts", "productStats"}, 
                allEntries = true)
    void batchPutOffSale(List<Long> productIds);
    
    // ========== 统计查询 ==========
    
    @Cacheable(value = "productStats", key = "'totalCount'")
    long getTotalProductCount();
    
    @Cacheable(value = "productStats", key = "'onSaleCount'")
    long getOnSaleProductCount();
    
    @Cacheable(value = "productStats", key = "'categoryCount_' + #categoryId")
    long getProductCountByCategory(Long categoryId);
    
    @Cacheable(value = "productStats", key = "'outOfStockCount'")
    long getOutOfStockCount();
    
    @Cacheable(value = "productStats", key = "'priceStatistics'")
    Map<String, Object> getPriceStatistics();
    
    @Cacheable(value = "productStats", key = "'categoryProductCount'")
    Map<Long, Long> getProductCountByCategory();
    
    // ========== 验证和检查 ==========
    
    void validateProductExists(Long productId);
    
    void validateProductPurchasable(Long productId, Integer quantity);
    
    boolean isProductNameDuplicate(String name, Long excludeId);
}