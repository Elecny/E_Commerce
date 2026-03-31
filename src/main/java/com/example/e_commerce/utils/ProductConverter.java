package com.example.e_commerce.utils;

import com.example.e_commerce.entity.Product;
import com.example.e_commerce.entity.elasticsearch.ProductDocument;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Entity conversion utility class for Product and ProductDocument
 */
@Component
public class ProductConverter {
    
    /**
     * Convert Product entity to ProductDocument
     * 
     * @param product Product entity
     * @return ProductDocument, or null if product is null
     */
    public ProductDocument toDocument(Product product) {
        if (product == null) {
            return null;
        }
        return new ProductDocument(product);
    }
    
    /**
     * Convert ProductDocument to Product entity
     * 
     * @param document ProductDocument
     * @return Product entity, or null if document is null
     */
    public Product toEntity(ProductDocument document) {
        if (document == null) {
            return null;
        }
        
        Product product = new Product();
        product.setId(document.getId());
        product.setName(document.getName());
        product.setDescription(document.getDescription());
        product.setPrice(document.getPrice());
        product.setOriginalPrice(document.getOriginalPrice());
        product.setStock(document.getStock());
        product.setCategoryId(document.getCategoryId());
        product.setMainImage(document.getMainImage());
        product.setImages(document.getImages());
        product.setStatus(document.getStatus());
        product.setSalesCount(document.getSalesCount());
        product.setViewCount(document.getViewCount());
        product.setWeight(document.getWeight());
        product.setUnit(document.getUnit());
        product.setSortOrder(document.getSortOrder());
        product.setCreateTime(document.getCreateTime());
        product.setUpdateTime(document.getUpdateTime());
        
        return product;
    }
    
    /**
     * Convert list of Product entities to list of ProductDocuments
     * 
     * @param products List of Product entities
     * @return List of ProductDocuments, empty list if input is null or empty
     */
    public List<ProductDocument> toDocumentList(List<Product> products) {
        if (CollectionUtils.isEmpty(products)) {
            return Collections.emptyList();
        }
        
        return products.stream()
                .filter(Objects::nonNull)
                .map(this::toDocument)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert list of ProductDocuments to list of Product entities
     * 
     * @param documents List of ProductDocuments
     * @return List of Product entities, empty list if input is null or empty
     */
    public List<Product> toEntityList(List<ProductDocument> documents) {
        if (CollectionUtils.isEmpty(documents)) {
            return Collections.emptyList();
        }
        
        return documents.stream()
                .filter(Objects::nonNull)
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Update an existing Product entity with data from ProductDocument
     * 
     * @param product Target Product entity to update
     * @param document Source ProductDocument containing new data
     * @return Updated Product entity
     * @throws IllegalArgumentException if product is null
     */
    public Product updateEntity(Product product, ProductDocument document) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        if (document == null) {
            return product;
        }
        
        // Update fields from document to entity
        product.setName(document.getName());
        product.setDescription(document.getDescription());
        product.setPrice(document.getPrice());
        product.setOriginalPrice(document.getOriginalPrice());
        product.setStock(document.getStock());
        product.setCategoryId(document.getCategoryId());
        product.setMainImage(document.getMainImage());
        product.setImages(document.getImages());
        product.setStatus(document.getStatus());
        product.setSalesCount(document.getSalesCount());
        product.setViewCount(document.getViewCount());
        product.setWeight(document.getWeight());
        product.setUnit(document.getUnit());
        product.setSortOrder(document.getSortOrder());
        // Note: createTime and updateTime should not be updated here
        
        return product;
    }
    
    /**
     * Update an existing ProductDocument with data from Product entity
     * 
     * @param document Target ProductDocument to update
     * @param product Source Product entity containing new data
     * @return Updated ProductDocument
     * @throws IllegalArgumentException if document is null
     */
    public ProductDocument updateDocument(ProductDocument document, Product product) {
        if (document == null) {
            throw new IllegalArgumentException("ProductDocument cannot be null");
        }
        
        if (product == null) {
            return document;
        }
        
        // Update fields from entity to document
        document.setName(product.getName());
        document.setDescription(product.getDescription());
        document.setPrice(product.getPrice());
        document.setOriginalPrice(product.getOriginalPrice());
        document.setStock(product.getStock());
        document.setCategoryId(product.getCategoryId());
        document.setMainImage(product.getMainImage());
        document.setImages(product.getImages());
        document.setStatus(product.getStatus());
        document.setSalesCount(product.getSalesCount());
        document.setViewCount(product.getViewCount());
        document.setWeight(product.getWeight());
        document.setUnit(product.getUnit());
        document.setSortOrder(product.getSortOrder());
        document.setCreateTime(product.getCreateTime());
        document.setUpdateTime(product.getUpdateTime());
        
        return document;
    }
    
    

    
    /**
     * Check if a Product and ProductDocument represent the same entity
     * 
     * @param product Product entity
     * @param document ProductDocument
     * @return true if they have the same ID, false otherwise
     */
    public boolean isSameEntity(Product product, ProductDocument document) {
        if (product == null && document == null) {
            return true;
        }
        
        if (product == null || document == null) {
            return false;
        }
        
        return product.getId() != null && product.getId().equals(document.getId());
    }
}