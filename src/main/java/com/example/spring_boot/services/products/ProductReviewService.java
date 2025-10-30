package com.example.spring_boot.services.products;

import com.example.spring_boot.domains.products.ProductReview;
import com.example.spring_boot.domains.User;
import com.example.spring_boot.repository.products.ProductReviewRepository;
import com.example.spring_boot.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final AuthService authService;
   

    public ProductReview createReview(String productId, Integer rating, String comment, String authToken) {
        // Xác thực người dùng
        if (authToken == null || authToken.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập để đánh giá sản phẩm");
        }
        
        User user = authService.getUserByRefreshToken(authToken);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token không hợp lệ");
        }

        // Validate
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Đánh giá phải từ 1 đến 5 sao");
        }
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung đánh giá không được để trống");
        }

        // Kiểm tra xem user đã đánh giá sản phẩm này chưa
        List<ProductReview> existingReviews = reviewRepository.findActiveByProductId(new ObjectId(productId));
        boolean hasReviewed = existingReviews.stream()
                .anyMatch(review -> review.getUserId() != null && review.getUserId().equals(new ObjectId(user.getId())));
        
        if (hasReviewed) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bạn đã đánh giá sản phẩm này rồi");
        }

        ProductReview review = ProductReview.builder()
                .productId(new ObjectId(productId))
                .userId(new ObjectId(user.getId()))
                .name(user.getName()) // Lấy từ thông tin user
                .email(user.getEmail()) // Lấy từ thông tin user
                .rating(rating)
                .comment(comment.trim())
                .createdAt(Instant.now())
                .build();

        // Lưu đánh giá
        ProductReview saved = reviewRepository.save(review);
        log.info("Đánh giá mới cho sản phẩm {} từ user {}: {} sao", productId, user.getId(), rating);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<ProductReview> getReviewsByProductId(String productId) {
        return reviewRepository.findActiveByProductId(new ObjectId(productId));
    }

    @Transactional(readOnly = true)
    public long getReviewCount(String productId) {
        return reviewRepository.countByProductId(new ObjectId(productId));
    }

    @Transactional(readOnly = true)
    public double getAverageRating(String productId) {
        List<ProductReview> reviews = getReviewsByProductId(productId);
        if (reviews.isEmpty()) return 0.0;
        return reviews.stream()
                .mapToInt(ProductReview::getRating)
                .average()
                .orElse(0.0);
    }
}
