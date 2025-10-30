package com.example.spring_boot.domains.products;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;



import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "product_reviews")
public class ProductReview {
    @Id private String id;
    @Field(targetType = FieldType.OBJECT_ID) private ObjectId productId;
    @Field(targetType = FieldType.OBJECT_ID) private ObjectId userId; // Thay thế name/email bằng userId
    private String name; // Giữ lại để hiển thị (lấy từ User)
    private String email; // Giữ lại để hiển thị (lấy từ User)
    private Integer rating;
    private String comment;
    @Builder.Default private Instant createdAt = Instant.now();
    private Instant deletedAt;
}