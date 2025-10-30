package com.example.spring_boot.repository;

import com.example.spring_boot.domains.Promotion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository cho Promotion (Khuyến mãi)
 * 
 * @author Spring Boot Team
 * @version 1.0
 */
@Repository
public interface PromotionRepository extends MongoRepository<Promotion, String> {
    
    /**
     * Tìm kiếm promotion theo tên (ignore case)
     * 
     * @param name Tên promotion cần tìm
     * @return Danh sách promotions có tên chứa từ khóa
     */
    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<Promotion> findByNameContainingIgnoreCase(String name);
    
    /**
     * Tìm kiếm promotion theo trạng thái active
     * 
     * @param isActive Trạng thái active
     * @return Danh sách promotions theo trạng thái
     */
    List<Promotion> findByIsActive(Boolean isActive);
    
    /**
     * Tìm kiếm promotion đang hoạt động trong khoảng thời gian
     * 
     * @param currentTime Thời gian hiện tại
     * @param isActive Trạng thái active
     * @return Danh sách promotions đang hoạt động
     */
    @Query("{ 'startDate': { $lte: ?0 }, 'endDate': { $gte: ?0 }, 'isActive': ?1 }")
    List<Promotion> findActivePromotionsInTimeRange(LocalDateTime currentTime, Boolean isActive);
    
    /**
     * Kiểm tra xem có promotion nào trùng tên không
     * 
     * @param name Tên promotion cần kiểm tra
     * @return Optional chứa promotion nếu tìm thấy
     */
    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    Optional<Promotion> findByNameIgnoreCase(String name);
    
    /**
     * Đếm số lượng promotion theo trạng thái
     * 
     * @param isActive Trạng thái active
     * @return Số lượng promotion
     */
    long countByIsActive(Boolean isActive);
}
