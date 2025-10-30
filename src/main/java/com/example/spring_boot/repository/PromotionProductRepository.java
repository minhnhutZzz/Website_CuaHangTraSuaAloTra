package com.example.spring_boot.repository;

import com.example.spring_boot.domains.PromotionProduct;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho PromotionProduct (Áp dụng khuyến mãi cho sản phẩm)
 * 
 * @author Spring Boot Team
 * @version 1.0
 */
@Repository
public interface PromotionProductRepository extends MongoRepository<PromotionProduct, String> {
    
    /**
     * Tìm kiếm theo promotion ID
     * 
     * @param promotionId ID của khuyến mãi
     * @return Danh sách sản phẩm áp dụng khuyến mãi
     */
    List<PromotionProduct> findByPromotionId(String promotionId);
    
    /**
     * Tìm kiếm theo product ID
     * 
     * @param productId ID của sản phẩm
     * @return Danh sách khuyến mãi áp dụng cho sản phẩm
     */
    List<PromotionProduct> findByProductId(String productId);
    
    /**
     * Tìm kiếm theo promotion ID và product ID
     * 
     * @param promotionId ID của khuyến mãi
     * @param productId ID của sản phẩm
     * @return PromotionProduct nếu tìm thấy
     */
    Optional<PromotionProduct> findByPromotionIdAndProductId(String promotionId, String productId);
    
    /**
     * Tìm kiếm theo trạng thái active
     * 
     * @param isActive Trạng thái active
     * @return Danh sách promotion products theo trạng thái
     */
    List<PromotionProduct> findByIsActive(Boolean isActive);
    
    /**
     * Tìm kiếm sản phẩm đang áp dụng khuyến mãi active
     * 
     * @param productId ID của sản phẩm
     * @param isActive Trạng thái active
     * @return Danh sách promotion products
     */
    List<PromotionProduct> findByProductIdAndIsActive(String productId, Boolean isActive);
    
    /**
     * Tìm kiếm khuyến mãi đang áp dụng cho sản phẩm active
     * 
     * @param promotionId ID của khuyến mãi
     * @param isActive Trạng thái active
     * @return Danh sách promotion products
     */
    List<PromotionProduct> findByPromotionIdAndIsActive(String promotionId, Boolean isActive);
    
    /**
     * Kiểm tra xem sản phẩm đã áp dụng khuyến mãi chưa
     * 
     * @param promotionId ID của khuyến mãi
     * @param productId ID của sản phẩm
     * @return true nếu đã áp dụng
     */
    boolean existsByPromotionIdAndProductId(String promotionId, String productId);
    
    /**
     * Đếm số lượng sản phẩm áp dụng khuyến mãi
     * 
     * @param promotionId ID của khuyến mãi
     * @return Số lượng sản phẩm
     */
    long countByPromotionId(String promotionId);
    
    /**
     * Đếm số lượng khuyến mãi áp dụng cho sản phẩm
     * 
     * @param productId ID của sản phẩm
     * @return Số lượng khuyến mãi
     */
    long countByProductId(String productId);
    
    /**
     * Xóa tất cả promotion products theo promotion ID
     * 
     * @param promotionId ID của khuyến mãi
     */
    void deleteByPromotionId(String promotionId);
    
    /**
     * Xóa tất cả promotion products theo product ID
     * 
     * @param productId ID của sản phẩm
     */
    void deleteByProductId(String productId);
    
    /**
     * Tìm kiếm promotion products với thông tin chi tiết
     * Sử dụng aggregation để join với collections khác
     * 
     * @param promotionId ID của khuyến mãi
     * @return Danh sách promotion products với thông tin chi tiết
     */
    @Query("{ 'promotionId': ?0 }")
    List<PromotionProduct> findPromotionProductsWithDetails(String promotionId);
}
