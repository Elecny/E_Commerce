package com.example.e_commerce.repository;

import com.example.e_commerce.entity.elasticsearch.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument, Long> {
    List<ProductDocument> findByNameContaining(String name);
    List<ProductDocument> findByCategoryId(Long categoryId);
    List<ProductDocument> findByStatus(String status);
}