package com.example.spring_boot.domains;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Domain class cho PromotionProduct (Áp dụng khuyến mãi cho sản phẩm)
 * 
 * @author Spring Boot Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "promotion_products")
public class PromotionProduct {
    
    @Id
    private String id;
    
    @Field("promotionId")
    private String promotionId;
    
    @Field("productId")
    private String productId;
    
    @Field("discountAmount")
    private Double discountAmount; // Số tiền giảm cụ thể (nếu có)
    
    @Field("isActive")
    private Boolean isActive;
    
    @Field("createdAt")
    private LocalDateTime createdAt;
    
    @Field("updatedAt")
    private LocalDateTime updatedAt;
    
    @Field("_class")
    private String className = "com.example.spring_boot.domains.PromotionProduct";
    
    // Constructor cho việc tạo mới
    public PromotionProduct(String promotionId, String productId, Double discountAmount, Boolean isActive) {
        this.promotionId = promotionId;
        this.productId = productId;
        this.discountAmount = discountAmount;
        this.isActive = isActive;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.className = "com.example.spring_boot.domains.PromotionProduct";
    }
    
    // Constructor với thời gian tùy chỉnh
    public PromotionProduct(String promotionId, String productId, Double discountAmount, 
                           Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.promotionId = promotionId;
        this.productId = productId;
        this.discountAmount = discountAmount;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.className = "com.example.spring_boot.domains.PromotionProduct";
    }
}
