package com.example.spring_boot.domains;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Domain class cho Promotion (Khuyến mãi)
 * 
 * @author Spring Boot Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "promotions")
public class Promotion {
    
    @Id
    private String id;
    
    @Field("name")
    private String name;
    
    @Field("description")
    private String description;
    
    @Field("discountPercent")
    private Integer discountPercent;
    
    @Field("startDate")
    private LocalDateTime startDate;
    
    @Field("endDate")
    private LocalDateTime endDate;
    
    @Field("isActive")
    private Boolean isActive;
    
    @Field("_class")
    private String className = "com.example.spring_boot.domains.Promotion.Promotion";
    
    // Constructor cho việc tạo mới promotion
    public Promotion(String name, String description, Integer discountPercent, 
                    LocalDateTime startDate, LocalDateTime endDate, Boolean isActive) {
        this.name = name;
        this.description = description;
        this.discountPercent = discountPercent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
        this.className = "com.example.spring_boot.domains.Promotion.Promotion";
    }
}
