package com.example.e_commerce.dto;

import java.math.BigDecimal;

public class ProductQueryDTO {
    
    private String name;
    private Long categoryId;
    private String status;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minStock;
    private Integer maxStock;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
    
    public ProductQueryDTO() {
        this.page = 0;
        this.size = 20;
        this.sortBy = "createTime";
        this.sortDirection = "DESC";
    }
    
    public ProductQueryDTO(String name, Long categoryId, String status, BigDecimal minPrice,
                           BigDecimal maxPrice, Integer minStock, Integer maxStock,
                           Integer page, Integer size, String sortBy, String sortDirection) {
        this.name = name;
        this.categoryId = categoryId;
        this.status = status;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minStock = minStock;
        this.maxStock = maxStock;
        this.page = page != null ? page : 0;
        this.size = size != null ? size : 20;
        this.sortBy = sortBy != null ? sortBy : "createTime";
        this.sortDirection = sortDirection != null ? sortDirection : "DESC";
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public BigDecimal getMinPrice() {
        return minPrice;
    }
    
    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }
    
    public BigDecimal getMaxPrice() {
        return maxPrice;
    }
    
    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }
    
    public Integer getMinStock() {
        return minStock;
    }
    
    public void setMinStock(Integer minStock) {
        this.minStock = minStock;
    }
    
    public Integer getMaxStock() {
        return maxStock;
    }
    
    public void setMaxStock(Integer maxStock) {
        this.maxStock = maxStock;
    }
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public void setSize(Integer size) {
        this.size = size;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortDirection() {
        return sortDirection;
    }
    
    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

}
