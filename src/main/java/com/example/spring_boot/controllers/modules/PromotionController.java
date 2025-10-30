package com.example.spring_boot.controllers.modules;

import com.example.spring_boot.domains.Promotion;
import com.example.spring_boot.dto.ApiResponse;
import com.example.spring_boot.dto.PageResponse;
import com.example.spring_boot.services.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller cho Promotion (Khuyến mãi)
 * 
 * @author Spring Boot Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@Tag(name = "Promotion Controller", description = "API quản lý khuyến mãi - Trả về ApiResponse/PageResponse")
public class PromotionController {
    
    private final PromotionService promotionService;
    
    /**
     * Tạo khuyến mãi mới
     * 
     * @param promotion Thông tin khuyến mãi
     * @return ResponseEntity chứa ApiResponse<Promotion>
     */
    @PostMapping
    @Operation(summary = "Tạo khuyến mãi mới", 
               description = "Tạo khuyến mãi mới với thông tin đầy đủ. Trả về ApiResponse<Promotion>")
    public ResponseEntity<ApiResponse<Promotion>> createPromotion(@Valid @RequestBody Promotion promotion) {
        try {
            System.out.println("==> [API] promotion.isActive: " + promotion.getIsActive());
            log.info("Creating new promotion: {}", promotion.getName());
            Promotion createdPromotion = promotionService.createPromotion(promotion);
            ApiResponse<Promotion> response = new ApiResponse<>(
                    true,
                    "Thêm khuyến mãi thành công!",
                    createdPromotion
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception ex) {
            log.error("Exception when create promotion", ex);
            ApiResponse<Promotion> response = new ApiResponse<>(false, "Có lỗi xảy ra khi tạo khuyến mãi!", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Lấy chi tiết khuyến mãi theo ID
     * 
     * @param id ID của khuyến mãi
     * @return ApiResponse<Promotion>
     */
    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết khuyến mãi", 
               description = "Lấy thông tin chi tiết khuyến mãi theo ID. Trả về ApiResponse<Promotion>")
    public ApiResponse<Promotion> getPromotionById(
            @Parameter(description = "ID của khuyến mãi") @PathVariable String id) {
        
        log.info("Getting promotion by ID: {}", id);
        
        Promotion promotion = promotionService.getPromotionById(id);
        
        return new ApiResponse<>(
                true,
                "Lấy thông tin khuyến mãi thành công!",
                promotion
        );
    }
    
    /**
     * Cập nhật khuyến mãi
     * 
     * @param id ID của khuyến mãi cần cập nhật
     * @param promotion Thông tin khuyến mãi mới
     * @return ApiResponse<Promotion>
     */
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật khuyến mãi", 
               description = "Cập nhật thông tin khuyến mãi theo ID. Trả về ApiResponse<Promotion>")
    public ApiResponse<Promotion> updatePromotion(
            @Parameter(description = "ID của khuyến mãi") @PathVariable String id,
            @Valid @RequestBody Promotion promotion) {
        
        log.info("Updating promotion with ID: {}", id);
        
        Promotion updatedPromotion = promotionService.updatePromotion(id, promotion);
        
        return new ApiResponse<>(
                true,
                "Cập nhật khuyến mãi thành công!",
                updatedPromotion
        );
    }
    
    /**
     * Xóa khuyến mãi (soft delete)
     * 
     * @param id ID của khuyến mãi cần xóa
     * @return ResponseEntity<ApiResponse<Void>>
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa khuyến mãi", 
               description = "Xóa khuyến mãi theo ID (soft delete). Trả về ApiResponse<Void>")
    public ResponseEntity<ApiResponse<Void>> deletePromotion(
            @Parameter(description = "ID của khuyến mãi") @PathVariable String id) {
        
        log.info("Deleting promotion with ID: {}", id);
        
        promotionService.deletePromotion(id);
        
        ApiResponse<Void> response = new ApiResponse<>(
                true,
                "Xóa khuyến mãi thành công!",
                null
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Lấy danh sách tất cả khuyến mãi
     * 
     * @return ApiResponse<List<Promotion>>
     */
    @GetMapping
    @Operation(summary = "Lấy danh sách khuyến mãi", 
               description = "Lấy danh sách tất cả khuyến mãi. Trả về ApiResponse<List<Promotion>>")
    public ApiResponse<List<Promotion>> getAllPromotions() {
        
        log.info("Getting all promotions");
        
        List<Promotion> promotions = promotionService.getAllPromotions();
        
        return new ApiResponse<>(
                true,
                "Lấy danh sách khuyến mãi thành công!",
                promotions
        );
    }
    
    /**
     * Lấy danh sách khuyến mãi với phân trang
     * 
     * @param page Số trang (bắt đầu từ 0)
     * @param size Kích thước trang
     * @param sortBy Trường sắp xếp
     * @param sortDir Hướng sắp xếp (asc/desc)
     * @return ApiResponse<PageResponse<Promotion>>
     */
    @GetMapping("/paged")
    @Operation(summary = "Lấy danh sách khuyến mãi có phân trang", 
               description = "Lấy danh sách khuyến mãi với phân trang và sắp xếp. Trả về ApiResponse<PageResponse<Promotion>>")
    public ApiResponse<PageResponse<Promotion>> getPromotionsPaged(
            @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Trường sắp xếp") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Hướng sắp xếp (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Getting promotions paged - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                page, size, sortBy, sortDir);
        
        Page<Promotion> promotionPage = promotionService.getPromotionsPaged(page, size, sortBy, sortDir);
        
        PageResponse<Promotion> pageResponse = new PageResponse<>(
                promotionPage.getContent(),
                promotionPage.getTotalElements(),
                promotionPage.getNumber(),
                promotionPage.getSize()
        );
        
        return new ApiResponse<>(
                true,
                "Lấy danh sách khuyến mãi thành công!",
                pageResponse
        );
    }
    
    /**
     * Tìm kiếm khuyến mãi theo tên
     * 
     * @param name Tên cần tìm kiếm
     * @return ApiResponse<List<Promotion>>
     */
    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm khuyến mãi theo tên", 
               description = "Tìm kiếm khuyến mãi theo tên. Trả về ApiResponse<List<Promotion>>")
    public ApiResponse<List<Promotion>> searchPromotionsByName(
            @Parameter(description = "Tên khuyến mãi cần tìm") @RequestParam(required = false) String name) {
        
        log.info("Searching promotions by name: {}", name);
        
        List<Promotion> promotions = promotionService.searchPromotionsByName(name);
        
        return new ApiResponse<>(
                true,
                "Tìm kiếm khuyến mãi thành công!",
                promotions
        );
    }
    
    /**
     * Lấy danh sách khuyến mãi đang hoạt động
     * 
     * @return ApiResponse<List<Promotion>>
     */
    @GetMapping("/active")
    @Operation(summary = "Lấy danh sách khuyến mãi đang hoạt động", 
               description = "Lấy danh sách khuyến mãi đang hoạt động. Trả về ApiResponse<List<Promotion>>")
    public ApiResponse<List<Promotion>> getActivePromotions() {
        
        log.info("Getting active promotions");
        
        List<Promotion> promotions = promotionService.getActivePromotions();
        
        return new ApiResponse<>(
                true,
                "Lấy danh sách khuyến mãi đang hoạt động thành công!",
                promotions
        );
    }
    
    /**
     * Lấy danh sách khuyến mãi đang hoạt động trong khoảng thời gian hiện tại
     * 
     * @return ApiResponse<List<Promotion>>
     */
    @GetMapping("/current-active")
    @Operation(summary = "Lấy danh sách khuyến mãi đang hoạt động hiện tại", 
               description = "Lấy danh sách khuyến mãi đang hoạt động trong khoảng thời gian hiện tại. Trả về ApiResponse<List<Promotion>>")
    public ApiResponse<List<Promotion>> getCurrentActivePromotions() {
        
        log.info("Getting current active promotions");
        
        List<Promotion> promotions = promotionService.getCurrentActivePromotions();
        
        return new ApiResponse<>(
                true,
                "Lấy danh sách khuyến mãi đang hoạt động hiện tại thành công!",
                promotions
        );
    }
    
    /**
     * Đếm số lượng khuyến mãi theo trạng thái
     * 
     * @param isActive Trạng thái active
     * @return ApiResponse<Long>
     */
    @GetMapping("/count")
    @Operation(summary = "Đếm số lượng khuyến mãi", 
               description = "Đếm số lượng khuyến mãi theo trạng thái. Trả về ApiResponse<Long>")
    public ApiResponse<Long> countPromotionsByStatus(
            @Parameter(description = "Trạng thái active (true/false)") @RequestParam(required = false) Boolean isActive) {
        
        log.info("Counting promotions by status: {}", isActive);
        
        long count = promotionService.countPromotionsByStatus(isActive);
        
        return new ApiResponse<>(
                true,
                "Đếm số lượng khuyến mãi thành công!",
                count
        );
    }

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleStatusException(org.springframework.web.server.ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(new ApiResponse<>(false, ex.getReason(), null));
    }
}
