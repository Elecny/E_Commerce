package com.example.e_commerce.dto;


import java.util.List;

public class BatchOperationDTO {
    
    private List<Long> productIds;
    private String status;
    
    public BatchOperationDTO() {
    }
    
    public BatchOperationDTO(List<Long> productIds, String status) {
        this.productIds = productIds;
        this.status = status;
    }
    
    public List<Long> getProductIds() {
        return productIds;
    }
    
    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

}
