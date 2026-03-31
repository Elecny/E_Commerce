package com.example.e_commerce.entity.elasticsearch;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.e_commerce.entity.Product;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(indexName = "products")
@Setting(settingPath = "elasticsearch/settings.json") // Optional: custom analyzer settings
public class ProductDocument {
    
    @Id
    private Long id;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String name;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String description;
    
    @Field(type = FieldType.Double)
    private BigDecimal price;
    
    @Field(type = FieldType.Double)
    private BigDecimal originalPrice;
    
    @Field(type = FieldType.Integer)
    private Integer stock;
    
    @Field(type = FieldType.Long)
    private Long categoryId;
    
    @Field(type = FieldType.Keyword)
    private String mainImage;
    
    @Field(type = FieldType.Text)
    private String images;
    
    @Field(type = FieldType.Keyword)
    private String status;
    
    @Field(type = FieldType.Integer)
    private Integer salesCount;
    
    @Field(type = FieldType.Integer)
    private Integer viewCount;
    
    @Field(type = FieldType.Double)
    private BigDecimal weight;
    
    @Field(type = FieldType.Keyword)
    private String unit;
    
    @Field(type = FieldType.Integer)
    private Integer sortOrder;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    // Constructors
    public ProductDocument() {
    }
    
    public ProductDocument(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.originalPrice = product.getOriginalPrice();
        this.stock = product.getStock();
        this.categoryId = product.getCategoryId();
        this.mainImage = product.getMainImage();
        this.images = product.getImages();
        this.status = product.getStatus();
        this.salesCount = product.getSalesCount();
        this.viewCount = product.getViewCount();
        this.weight = product.getWeight();
        this.unit = product.getUnit();
        this.sortOrder = product.getSortOrder();
        this.createTime = product.getCreateTime();
        this.updateTime = product.getUpdateTime();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }
    
    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }
    
    public Integer getStock() {
        return stock;
    }
    
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getMainImage() {
        return mainImage;
    }
    
    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }
    
    public String getImages() {
        return images;
    }
    
    public void setImages(String images) {
        this.images = images;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getSalesCount() {
        return salesCount;
    }
    
    public void setSalesCount(Integer salesCount) {
        this.salesCount = salesCount;
    }
    
    public Integer getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
    
    public BigDecimal getWeight() {
        return weight;
    }
    
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}