package com.example.e_commerce.repository;

import com.example.e_commerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // ========== 基础查询方法 ==========
    
    /**
     * 根据状态查询商品
     */
    List<Product> findByStatus(String status);
    
    /**
     * 根据状态分页查询商品
     */
    Page<Product> findByStatus(String status, Pageable pageable);
    
    /**
     * 根据分类ID查询商品
     */
    List<Product> findByCategoryId(Long categoryId);
    
    /**
     * 根据分类ID分页查询商品
     */
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    /**
     * 根据分类ID和状态查询商品
     */
    List<Product> findByCategoryIdAndStatus(Long categoryId, String status);
    
    /**
     * 根据分类ID和状态分页查询商品
     */
    Page<Product> findByCategoryIdAndStatus(Long categoryId, String status, Pageable pageable);
    
    /**
     * 根据名称模糊查询商品
     */
    List<Product> findByNameContaining(String name);
    
    /**
     * 根据名称模糊查询并分页
     */
    Page<Product> findByNameContaining(String name, Pageable pageable);
    
    /**
     * 根据名称和状态模糊查询
     */
    Page<Product> findByNameContainingAndStatus(String name, String status, Pageable pageable);
    
    // ========== 价格范围查询 ==========
    
    /**
     * 查询价格在指定范围内的商品
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * 查询价格在指定范围内且上架的商品
     */
    List<Product> findByPriceBetweenAndStatus(BigDecimal minPrice, BigDecimal maxPrice, String status);
    
    /**
     * 查询价格在指定范围内的商品（分页）
     */
    Page<Product> findByPriceBetweenAndStatus(BigDecimal minPrice, BigDecimal maxPrice, String status, Pageable pageable);
    
    // ========== 库存相关查询 ==========
    
    /**
     * 查询库存不足的商品（低于阈值）
     */
    List<Product> findByStockLessThan(Integer stockThreshold);
    
    /**
     * 查询库存不足且上架的商品
     */
    List<Product> findByStockLessThanAndStatus(Integer stockThreshold, String status);
    
    /**
     * 查询库存大于0且上架的商品
     */
    List<Product> findByStockGreaterThanAndStatus(Integer stock, String status);
    
    // ========== 销量排行 ==========
    
    /**
     * 查询销量最高的前N个商品
     */
    List<Product> findTop10ByOrderBySalesCountDesc();
    
    /**
     * 查询销量最高的前N个上架商品
     */
    List<Product> findTop10ByStatusOrderBySalesCountDesc(String status);
    
    /**
     * 自定义查询销量最高的前N个商品（原生MySQL）
     */
    @Query(value = "SELECT * FROM product WHERE status = :status ORDER BY sales_count DESC LIMIT :limit", 
           nativeQuery = true)
    List<Product> findTopSalesProducts(@Param("status") String status, 
                                       @Param("limit") int limit);
    
    // ========== 浏览排行 ==========
    
    /**
     * 查询浏览量最高的前N个商品
     */
    List<Product> findTop10ByOrderByViewCountDesc();
    
    /**
     * 查询浏览量最高的前N个上架商品
     */
    List<Product> findTop10ByStatusOrderByViewCountDesc(String status);
    
    // ========== 综合查询 ==========
    
    /**
     * 多条件分页查询商品（原生MySQL）
     */
    @Query(value = "SELECT * FROM product WHERE " +
           "(:name IS NULL OR name LIKE CONCAT('%', :name, '%')) AND " +
           "(:categoryId IS NULL OR category_id = :categoryId) AND " +
           "(:status IS NULL OR status = :status) AND " +
           "(:minPrice IS NULL OR price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR price <= :maxPrice) AND " +
           "(:minStock IS NULL OR stock >= :minStock) AND " +
           "(:maxStock IS NULL OR stock <= :maxStock) " +
           "ORDER BY sort_order ASC, create_time DESC",
           countQuery = "SELECT COUNT(*) FROM product WHERE " +
                        "(:name IS NULL OR name LIKE CONCAT('%', :name, '%')) AND " +
                        "(:categoryId IS NULL OR category_id = :categoryId) AND " +
                        "(:status IS NULL OR status = :status) AND " +
                        "(:minPrice IS NULL OR price >= :minPrice) AND " +
                        "(:maxPrice IS NULL OR price <= :maxPrice) AND " +
                        "(:minStock IS NULL OR stock >= :minStock) AND " +
                        "(:maxStock IS NULL OR stock <= :maxStock)",
           nativeQuery = true)
    Page<Product> findByConditions(@Param("name") String name,
                                   @Param("categoryId") Long categoryId,
                                   @Param("status") String status,
                                   @Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   @Param("minStock") Integer minStock,
                                   @Param("maxStock") Integer maxStock,
                                   Pageable pageable);
    
    // ========== 更新操作 ==========
    
    /**
     * 更新商品库存（原生MySQL）
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE product SET stock = stock - :quantity WHERE id = :productId AND stock >= :quantity", 
           nativeQuery = true)
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    /**
     * 增加商品库存（原生MySQL）
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE product SET stock = stock + :quantity WHERE id = :productId", 
           nativeQuery = true)
    int increaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    /**
     * 增加商品销量（原生MySQL）
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE product SET sales_count = sales_count + :quantity WHERE id = :productId", 
           nativeQuery = true)
    int increaseSalesCount(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    /**
     * 增加商品浏览量（原生MySQL）
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE product SET view_count = view_count + 1 WHERE id = :productId", 
           nativeQuery = true)
    int increaseViewCount(@Param("productId") Long productId);
    
    /**
     * 批量更新商品状态（原生MySQL）
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE product SET status = :status WHERE id IN :productIds", 
           nativeQuery = true)
    int batchUpdateStatus(@Param("productIds") List<Long> productIds, @Param("status") String status);
    
    /**
     * 批量删除商品（逻辑删除）（原生MySQL）
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE product SET status = 'DELETED' WHERE id IN :productIds", 
           nativeQuery = true)
    int batchDelete(@Param("productIds") List<Long> productIds);
    
    // ========== 统计查询 ==========
    
    /**
     * 统计分类下的商品数量
     */
    long countByCategoryId(Long categoryId);
    
    /**
     * 统计分类下指定状态的商品数量
     */
    long countByCategoryIdAndStatus(Long categoryId, String status);
    
    /**
     * 统计上架商品数量
     */
    long countByStatus(String status);
    
    /**
     * 统计库存为0的商品数量
     */
    long countByStock(int stock);
    
    /**
     * 统计价格区间内的商品数量
     */
    long countByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * 查询商品价格统计信息（原生MySQL）
     */
    @Query(value = "SELECT MIN(price), MAX(price), AVG(price) FROM product WHERE status = 'ON_SALE'", 
           nativeQuery = true)
    Object[] getPriceStatistics();
    
    /**
     * 查询每个分类的商品数量（原生MySQL）
     */
    @Query(value = "SELECT category_id, COUNT(*) FROM product GROUP BY category_id", 
           nativeQuery = true)
    List<Object[]> countProductsByCategory();
    
    // ========== 存在性检查 ==========
    
    /**
     * 检查商品名称是否已存在（排除指定ID）
     */
    boolean existsByNameAndIdNot(String name, Long id);
    
    /**
     * 检查商品是否存在且状态为上架（原生MySQL）
     */
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM product WHERE id = :productId AND status = 'ON_SALE'", 
           nativeQuery = true)
    boolean existsByIdAndStatusOnSale(@Param("productId") Long productId);
}