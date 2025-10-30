package com.example.spring_boot.services;

import com.example.spring_boot.domains.Promotion;
import com.example.spring_boot.repository.PromotionRepository;
import com.example.spring_boot.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

/**
 * Service cho Promotion (Khuyến mãi)
 * 
 * @author Spring Boot Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionService {
    
    private final PromotionRepository promotionRepository;
    
    /**
     * Tạo promotion mới
     * 
     * @param promotion Thông tin promotion cần tạo
     * @return Promotion đã được tạo
     * @throws ResponseStatusException Nếu có lỗi validation
     */
    public Promotion createPromotion(Promotion promotion) {
        log.info("Creating new promotion: {}", promotion.getName());
        
        // Validation
        validatePromotion(promotion);
        
        // Kiểm tra trùng tên
        if (promotionRepository.findByNameIgnoreCase(promotion.getName()).isPresent()) {
            throw new ResponseStatusException(CONFLICT, "Tên khuyến mãi đã tồn tại");
        }
        
        // Chuẩn hóa dữ liệu
        promotion.setName(ValidationUtils.normalize(promotion.getName()));
        promotion.setDescription(ValidationUtils.normalize(promotion.getDescription()));
        
        // Set default values
        if (promotion.getIsActive() == null) {
            promotion.setIsActive(false);
        }
        
        Promotion savedPromotion = promotionRepository.save(promotion);
        log.info("Successfully created promotion with ID: {}", savedPromotion.getId());
        
        return savedPromotion;
    }
    
    /**
     * Lấy promotion theo ID
     * 
     * @param id ID của promotion
     * @return Promotion
     * @throws ResponseStatusException Nếu không tìm thấy
     */
    public Promotion getPromotionById(String id) {
        log.info("Getting promotion by ID: {}", id);
        
        return promotionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Không tìm thấy khuyến mãi"));
    }
    
    /**
     * Cập nhật promotion
     * 
     * @param id ID của promotion cần cập nhật
     * @param promotion Thông tin promotion mới
     * @return Promotion đã được cập nhật
     * @throws ResponseStatusException Nếu có lỗi
     */
    public Promotion updatePromotion(String id, Promotion promotion) {
        log.info("Updating promotion with ID: {}", id);
        
        Promotion existingPromotion = getPromotionById(id);
        
        // Validation
        validatePromotion(promotion);
        
        // Kiểm tra trùng tên (trừ promotion hiện tại)
        Optional<Promotion> duplicatePromotion = promotionRepository.findByNameIgnoreCase(promotion.getName());
        if (duplicatePromotion.isPresent() && !duplicatePromotion.get().getId().equals(id)) {
            throw new ResponseStatusException(CONFLICT, "Tên khuyến mãi đã tồn tại");
        }
        
        // Cập nhật thông tin
        existingPromotion.setName(ValidationUtils.normalize(promotion.getName()));
        existingPromotion.setDescription(ValidationUtils.normalize(promotion.getDescription()));
        existingPromotion.setDiscountPercent(promotion.getDiscountPercent());
        existingPromotion.setStartDate(promotion.getStartDate());
        existingPromotion.setEndDate(promotion.getEndDate());
        existingPromotion.setIsActive(promotion.getIsActive());
        
        Promotion updatedPromotion = promotionRepository.save(existingPromotion);
        log.info("Successfully updated promotion with ID: {}", updatedPromotion.getId());
        
        return updatedPromotion;
    }
    
    /**
     * Xóa promotion (soft delete)
     * 
     * @param id ID của promotion cần xóa
     * @throws ResponseStatusException Nếu không tìm thấy
     */
    public void deletePromotion(String id) {
        log.info("Deleting promotion with ID: {}", id);
        
        Promotion promotion = getPromotionById(id);
        promotion.setIsActive(false);
        
        promotionRepository.save(promotion);
        log.info("Successfully deleted promotion with ID: {}", id);
    }
    
    /**
     * Lấy danh sách tất cả promotion
     * 
     * @return Danh sách promotion
     */
    public List<Promotion> getAllPromotions() {
        log.info("Getting all promotions");
        return promotionRepository.findAll();
    }
    
    /**
     * Lấy danh sách promotion với phân trang
     * 
     * @param page Số trang (bắt đầu từ 0)
     * @param size Kích thước trang
     * @param sortBy Trường sắp xếp
     * @param sortDir Hướng sắp xếp (asc/desc)
     * @return Page chứa danh sách promotion
     */
    public Page<Promotion> getPromotionsPaged(int page, int size, String sortBy, String sortDir) {
        log.info("Getting promotions paged - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return promotionRepository.findAll(pageable);
    }
    
    /**
     * Tìm kiếm promotion theo tên
     * 
     * @param name Tên cần tìm kiếm
     * @return Danh sách promotion
     */
    public List<Promotion> searchPromotionsByName(String name) {
        log.info("Searching promotions by name: {}", name);
        
        if (name == null || name.trim().isEmpty()) {
            return getAllPromotions();
        }
        
        return promotionRepository.findByNameContainingIgnoreCase(name.trim());
    }
    
    /**
     * Lấy danh sách promotion đang hoạt động
     * 
     * @return Danh sách promotion active
     */
    public List<Promotion> getActivePromotions() {
        log.info("Getting active promotions");
        return promotionRepository.findByIsActive(true);
    }
    
    /**
     * Lấy danh sách promotion đang hoạt động trong khoảng thời gian
     * 
     * @return Danh sách promotion đang hoạt động
     */
    public List<Promotion> getCurrentActivePromotions() {
        log.info("Getting current active promotions");
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findActivePromotionsInTimeRange(now, true);
    }
    
    /**
     * Đếm số lượng promotion theo trạng thái
     * 
     * @param isActive Trạng thái active
     * @return Số lượng promotion
     */
    public long countPromotionsByStatus(Boolean isActive) {
        log.info("Counting promotions by status: {}", isActive);
        return promotionRepository.countByIsActive(isActive);
    }
    
    /**
     * Validation cho promotion
     * 
     * @param promotion Promotion cần validate
     * @throws ResponseStatusException Nếu có lỗi validation
     */
    private void validatePromotion(Promotion promotion) {
        if (promotion == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Thông tin khuyến mãi không được để trống");
        }
        
        if (promotion.getName() == null || promotion.getName().trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Tên khuyến mãi không được để trống");
        }
        
        if (promotion.getDiscountPercent() == null || promotion.getDiscountPercent() <= 0 || promotion.getDiscountPercent() > 100) {
            throw new ResponseStatusException(BAD_REQUEST, "Phần trăm giảm giá phải từ 1-100");
        }
        
        if (promotion.getStartDate() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Ngày bắt đầu không được để trống");
        }
        
        if (promotion.getEndDate() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Ngày kết thúc không được để trống");
        }
        
        if (promotion.getEndDate().isBefore(promotion.getStartDate())) {
            throw new ResponseStatusException(BAD_REQUEST, "Ngày kết thúc phải sau ngày bắt đầu");
        }
        
        if (promotion.getEndDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(BAD_REQUEST, "Ngày kết thúc phải sau thời gian hiện tại");
        }
    }
}
