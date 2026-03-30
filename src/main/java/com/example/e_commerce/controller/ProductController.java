package com.example.e_commerce.controller;


import com.example.e_commerce.entity.Product;
import com.example.e_commerce.common.ApiResponse;
import com.example.e_commerce.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductRepository productRepository;
    
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    // ========== 基础CRUD操作 ==========
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Product>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> products = productRepository.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(product.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("商品不存在"));
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody Product product) {
        // 设置默认值
        if (product.getStatus() == null) {
            product.setStatus("OFF_SALE");
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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("商品创建成功", savedProduct));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {
        
        if (!productRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("商品不存在"));
        }
        
        product.setId(id);
        Product updatedProduct = productRepository.save(product);
        return ResponseEntity.ok(ApiResponse.success("商品更新成功", updatedProduct));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("商品不存在"));
        }
        
        productRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("商品删除成功", null));
    }
    
    // ========== 状态查询 ==========
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<Product>>> getProductsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    // ========== 分类查询 ==========
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<Product>>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products;
        
        if (status != null && !status.isEmpty()) {
            products = productRepository.findByCategoryIdAndStatus(categoryId, status, pageable);
        } else {
            products = productRepository.findByCategoryId(categoryId, pageable);
        }
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/category/{categoryId}/list")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByCategoryList(
            @PathVariable Long categoryId,
            @RequestParam(required = false) String status) {
        
        List<Product> products;
        
        if (status != null && !status.isEmpty()) {
            products = productRepository.findByCategoryIdAndStatus(categoryId, status);
        } else {
            products = productRepository.findByCategoryId(categoryId);
        }
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    // ========== 搜索功能 ==========
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Product>>> searchProductsByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products;
        
        if (status != null && !status.isEmpty()) {
            products = productRepository.findByNameContainingAndStatus(name, status, pageable);
        } else {
            products = productRepository.findByNameContaining(name, pageable);
        }
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/search/list")
    public ResponseEntity<ApiResponse<List<Product>>> searchProductsByNameList(
            @RequestParam String name) {
        
        List<Product> products = productRepository.findByNameContaining(name);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    // ========== 价格范围查询 ==========
    
    @GetMapping("/price-range")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(required = false) String status) {
        
        List<Product> products;
        
        if (status != null && !status.isEmpty()) {
            products = productRepository.findByPriceBetweenAndStatus(minPrice, maxPrice, status);
        } else {
            products = productRepository.findByPriceBetween(minPrice, maxPrice);
        }
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/price-range/page")
    public ResponseEntity<ApiResponse<Page<Product>>> getProductsByPriceRangePaged(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findByPriceBetweenAndStatus(
                minPrice, maxPrice, status, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    // ========== 库存相关 ==========
    
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<Product>>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold,
            @RequestParam(required = false) String status) {
        
        List<Product> products;
        
        if (status != null && !status.isEmpty()) {
            products = productRepository.findByStockLessThanAndStatus(threshold, status);
        } else {
            products = productRepository.findByStockLessThan(threshold);
        }
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/in-stock")
    public ResponseEntity<ApiResponse<List<Product>>> getInStockProducts() {
        List<Product> products = productRepository.findByStockGreaterThanAndStatus(0, "ON_SALE");
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    // ========== 销量排行 ==========
    
    @GetMapping("/top-sales")
    public ResponseEntity<ApiResponse<List<Product>>> getTopSalesProducts(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String status) {
        
        List<Product> products;
        
        if (status != null && !status.isEmpty()) {
            products = productRepository.findTopSalesProducts(status, limit);
        } else {
            products = productRepository.findTop10ByOrderBySalesCountDesc();
        }
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    // ========== 浏览排行 ==========
    
    @GetMapping("/top-views")
    public ResponseEntity<ApiResponse<List<Product>>> getTopViewProducts(
            @RequestParam(required = false) String status) {
        
        List<Product> products;
        
        if (status != null && !status.isEmpty()) {
            products = productRepository.findTop10ByStatusOrderByViewCountDesc(status);
        } else {
            products = productRepository.findTop10ByOrderByViewCountDesc();
        }
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    // ========== 综合查询 ==========
    
    @GetMapping("/advanced-search")
    public ResponseEntity<ApiResponse<Page<Product>>> advancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findByConditions(
                name, categoryId, status, minPrice, maxPrice, minStock, maxStock, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    // ========== 库存操作 ==========
    
    @PutMapping("/{id}/decrease-stock")
    public ResponseEntity<ApiResponse<Void>> decreaseStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        
        int result = productRepository.decreaseStock(id, quantity);
        
        if (result > 0) {
            return ResponseEntity.ok(ApiResponse.success("库存扣减成功", null));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("库存不足或商品不存在"));
        }
    }
    
    @PutMapping("/{id}/increase-stock")
    public ResponseEntity<ApiResponse<Void>> increaseStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        
        int result = productRepository.increaseStock(id, quantity);
        
        if (result > 0) {
            return ResponseEntity.ok(ApiResponse.success("库存增加成功", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("商品不存在"));
        }
    }
    
    @PutMapping("/{id}/increase-sales")
    public ResponseEntity<ApiResponse<Void>> increaseSalesCount(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        
        int result = productRepository.increaseSalesCount(id, quantity);
        
        if (result > 0) {
            return ResponseEntity.ok(ApiResponse.success("销量更新成功", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("商品不存在"));
        }
    }
    
    @PutMapping("/{id}/increase-view")
    public ResponseEntity<ApiResponse<Void>> increaseViewCount(@PathVariable Long id) {
        int result = productRepository.increaseViewCount(id);
        
        if (result > 0) {
            return ResponseEntity.ok(ApiResponse.success("浏览量更新成功", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("商品不存在"));
        }
    }
    
    // ========== 批量操作 ==========
    
    @PutMapping("/batch/status")
    public ResponseEntity<ApiResponse<Integer>> batchUpdateStatus(
            @RequestParam List<Long> productIds,
            @RequestParam String status) {
        
        int updatedCount = productRepository.batchUpdateStatus(productIds, status);
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("成功更新 %d 个商品状态", updatedCount), 
                updatedCount));
    }
    
    @PutMapping("/batch/delete")
    public ResponseEntity<ApiResponse<Integer>> batchDelete(
            @RequestParam List<Long> productIds) {
        
        int deletedCount = productRepository.batchDelete(productIds);
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("成功删除 %d 个商品", deletedCount), 
                deletedCount));
    }
    
    // ========== 统计查询 ==========
    
    @GetMapping("/statistics/count-by-category")
    public ResponseEntity<ApiResponse<List<Object[]>>> countProductsByCategory() {
        List<Object[]> statistics = productRepository.countProductsByCategory();
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    @GetMapping("/statistics/price")
    public ResponseEntity<ApiResponse<Object[]>> getPriceStatistics() {
        Object[] statistics = productRepository.getPriceStatistics();
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    @GetMapping("/statistics/counts")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getProductCounts() {
        long onSaleCount = productRepository.countByStatus("ON_SALE");
        long offSaleCount = productRepository.countByStatus("OFF_SALE");
        long deletedCount = productRepository.countByStatus("DELETED");
        long outOfStockCount = productRepository.countByStock(0);
        long totalCount = productRepository.count();
        
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("totalCount", totalCount);
        statistics.put("onSaleCount", onSaleCount);
        statistics.put("offSaleCount", offSaleCount);
        statistics.put("deletedCount", deletedCount);
        statistics.put("outOfStockCount", outOfStockCount);
        
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    @GetMapping("/statistics/category/{categoryId}")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getCategoryStatistics(
            @PathVariable Long categoryId) {
        
        long totalCount = productRepository.countByCategoryId(categoryId);
        long onSaleCount = productRepository.countByCategoryIdAndStatus(categoryId, "ON_SALE");
        
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("totalCount", totalCount);
        statistics.put("onSaleCount", onSaleCount);
        
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    // ========== 存在性检查 ==========
    
    @GetMapping("/check/name")
    public ResponseEntity<ApiResponse<Boolean>> checkNameExists(
            @RequestParam String name,
            @RequestParam(required = false) Long excludeId) {
        
        boolean exists;
        if (excludeId != null) {
            exists = productRepository.existsByNameAndIdNot(name, excludeId);
        } else {
            exists = productRepository.findByNameContaining(name).size() > 0;
        }
        
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
    
    @GetMapping("/{id}/check-on-sale")
    public ResponseEntity<ApiResponse<Boolean>> checkProductOnSale(@PathVariable Long id) {
        boolean isOnSale = productRepository.existsByIdAndStatusOnSale(id);
        return ResponseEntity.ok(ApiResponse.success(isOnSale));
    }
    
    // ========== 额外辅助方法 ==========
    
    @GetMapping("/count/by-status/{status}")
    public ResponseEntity<ApiResponse<Long>> countByStatus(@PathVariable String status) {
        long count = productRepository.countByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
    
    @GetMapping("/count/by-price-range")
    public ResponseEntity<ApiResponse<Long>> countByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        
        long count = productRepository.countByPriceBetween(minPrice, maxPrice);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}