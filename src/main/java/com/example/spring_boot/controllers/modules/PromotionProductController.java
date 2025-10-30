package com.example.spring_boot.controllers.modules;

import com.example.spring_boot.domains.PromotionProduct;
import com.example.spring_boot.dto.ApiResponse;
import com.example.spring_boot.services.PromotionProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller cho PromotionProduct (Áp dụng khuyến mãi cho sản phẩm)
 * 
 * @author Spring Boot Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/promotion-products")
@RequiredArgsConstructor
@Tag(name = "Promotion Product Controller", description = "API áp dụng khuyến mãi cho sản phẩm - Trả về ApiResponse")
public class PromotionProductController {
    
    private final PromotionProductService promotionProductService;
    
    /**
     * Áp dụng khuyến mãi cho sản phẩm
     * 
     * @param promotionId ID của khuyến mãi
     * @param productId ID của sản phẩm
     * @param discountAmount Số tiền giảm cụ thể (có thể null)
     * @return ResponseEntity chứa ApiResponse<PromotionProduct>
     */
    @PostMapping("/apply")
    @Operation(summary = "Áp dụng khuyến mãi cho sản phẩm", 
               description = "Áp dụng khuyến mãi cho một sản phẩm cụ thể. Trả về ApiResponse<PromotionProduct>")
    public ResponseEntity<ApiResponse<PromotionProduct>> applyPromotionToProduct(
            @Parameter(description = "ID của khuyến mãi") @RequestParam String promotionId,
            @Parameter(description = "ID của sản phẩm") @RequestParam String productId,
            @Parameter(description = "Số tiền giảm cụ thể (có thể null)") @RequestParam(required = false) Double discountAmount) {
        
        log.info("Applying promotion {} to product {}", promotionId, productId);
        
        PromotionProduct promotionProduct = promotionProductService.applyPromotionToProduct(promotionId, productId, discountAmount);
        
        ApiResponse<PromotionProduct> response = new ApiResponse<>(
                true,
                "Áp dụng khuyến mãi thành công!",
                promotionProduct
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Áp dụng khuyến mãi cho nhiều sản phẩm
     * 
     * @param promotionId ID của khuyến mãi
     * @param productIds Danh sách ID sản phẩm
     * @param discountAmount Số tiền giảm cụ thể (có thể null)
     * @return ResponseEntity chứa ApiResponse<List<PromotionProduct>>
     */
    @PostMapping("/apply-multiple")
    @Operation(summary = "Áp dụng khuyến mãi cho nhiều sản phẩm", 
               description = "Áp dụng khuyến mãi cho nhiều sản phẩm cùng lúc. Trả về ApiResponse<List<PromotionProduct>>")
    public ResponseEntity<ApiResponse<List<PromotionProduct>>> applyPromotionToProducts(
            @Parameter(description = "ID của khuyến mãi") @RequestParam String promotionId,
            @Parameter(description = "Danh sách ID sản phẩm (comma-separated)") @RequestParam String productIds,
            @Parameter(description = "Số tiền giảm cụ thể (có thể null)") @RequestParam(required = false) String discountAmount) {
        
        // Parse product IDs from comma-separated string
        List<String> productIdList = List.of(productIds.split(","));
        log.info("Applying promotion {} to {} products", promotionId, productIdList.size());
        
        // Parse discount amount if provided
        Double discountAmountValue = null;
        if (discountAmount != null && !discountAmount.trim().isEmpty()) {
            try {
                discountAmountValue = Double.parseDouble(discountAmount);
            } catch (NumberFormatException e) {
                log.warn("Invalid discount amount format: {}", discountAmount);
            }
        }
        
        List<PromotionProduct> promotionProducts = promotionProductService.applyPromotionToProducts(promotionId, productIdList, discountAmountValue);
        
        ApiResponse<List<PromotionProduct>> response = new ApiResponse<>(
                true,
                "Áp dụng khuyến mãi cho " + promotionProducts.size() + " sản phẩm thành công!",
                promotionProducts
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Hủy áp dụng khuyến mãi cho sản phẩm
     * 
     * @param promotionId ID của khuyến mãi
     * @param productId ID của sản phẩm
     * @return ResponseEntity<ApiResponse<Void>>
     */
    @DeleteMapping("/remove")
    @Operation(summary = "Hủy áp dụng khuyến mãi cho sản phẩm", 
               description = "Hủy áp dụng khuyến mãi cho một sản phẩm. Trả về ApiResponse<Void>")
    public ResponseEntity<ApiResponse<Void>> removePromotionFromProduct(
            @Parameter(description = "ID của khuyến mãi") @RequestParam String promotionId,
            @Parameter(description = "ID của sản phẩm") @RequestParam String productId) {
        
        log.info("Removing promotion {} from product {}", promotionId, productId);
        
        promotionProductService.removePromotionFromProduct(promotionId, productId);
        
        ApiResponse<Void> response = new ApiResponse<>(
                true,
                "Hủy áp dụng khuyến mãi thành công!",
                null
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Hủy áp dụng khuyến mãi cho nhiều sản phẩm
     * 
     * @param promotionId ID của khuyến mãi
     * @param productIds Danh sách ID sản phẩm
     * @return ResponseEntity<ApiResponse<Void>>
     */
    @DeleteMapping("/remove-multiple")
    @Operation(summary = "Hủy áp dụng khuyến mãi cho nhiều sản phẩm", 
               description = "Hủy áp dụng khuyến mãi cho nhiều sản phẩm cùng lúc. Trả về ApiResponse<Void>")
    public ResponseEntity<ApiResponse<Void>> removePromotionFromProducts(
            @Parameter(description = "ID của khuyến mãi") @RequestParam String promotionId,
            @Parameter(description = "Danh sách ID sản phẩm") @RequestParam List<String> productIds) {
        
        log.info("Removing promotion {} from {} products", promotionId, productIds.size());
        
        promotionProductService.removePromotionFromProducts(promotionId, productIds);
        
        ApiResponse<Void> response = new ApiResponse<>(
                true,
                "Hủy áp dụng khuyến mãi cho " + productIds.size() + " sản phẩm thành công!",
                null
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Lấy danh sách sản phẩm áp dụng khuyến mãi
     * 
     * @param promotionId ID của khuyến mãi
     * @return ApiResponse<List<PromotionProduct>>
     */
    @GetMapping("/by-promotion/{promotionId}")
    @Operation(summary = "Lấy danh sách sản phẩm áp dụng khuyến mãi", 
               description = "Lấy danh sách sản phẩm đang áp dụng khuyến mãi. Trả về ApiResponse<List<PromotionProduct>>")
    public ApiResponse<List<PromotionProduct>> getProductsByPromotion(
            @Parameter(description = "ID của khuyến mãi") @PathVariable String promotionId) {
        
        log.info("Getting products for promotion {}", promotionId);
        
        List<PromotionProduct> promotionProducts = promotionProductService.getProductsByPromotion(promotionId);
        
        return new ApiResponse<>(
                true,
                "Lấy danh sách sản phẩm áp dụng khuyến mãi thành công!",
                promotionProducts
        );
    }
    
    /**
     * Lấy danh sách khuyến mãi áp dụng cho sản phẩm
     * 
     * @param productId ID của sản phẩm
     * @return ApiResponse<List<PromotionProduct>>
     */
    @GetMapping("/by-product/{productId}")
    @Operation(summary = "Lấy danh sách khuyến mãi áp dụng cho sản phẩm", 
               description = "Lấy danh sách khuyến mãi đang áp dụng cho sản phẩm. Trả về ApiResponse<List<PromotionProduct>>")
    public ApiResponse<List<PromotionProduct>> getPromotionsByProduct(
            @Parameter(description = "ID của sản phẩm") @PathVariable String productId) {
        
        log.info("Getting promotions for product {}", productId);
        
        List<PromotionProduct> promotionProducts = promotionProductService.getPromotionsByProduct(productId);
        
        return new ApiResponse<>(
                true,
                "Lấy danh sách khuyến mãi áp dụng cho sản phẩm thành công!",
                promotionProducts
        );
    }
    
    /**
     * Lấy danh sách sản phẩm đang áp dụng khuyến mãi active
     * 
     * @param promotionId ID của khuyến mãi
     * @return ApiResponse<List<PromotionProduct>>
     */
    @GetMapping("/active/by-promotion/{promotionId}")
    @Operation(summary = "Lấy danh sách sản phẩm đang áp dụng khuyến mãi active", 
               description = "Lấy danh sách sản phẩm đang áp dụng khuyến mãi active. Trả về ApiResponse<List<PromotionProduct>>")
    public ApiResponse<List<PromotionProduct>> getActiveProductsByPromotion(
            @Parameter(description = "ID của khuyến mãi") @PathVariable String promotionId) {
        
        log.info("Getting active products for promotion {}", promotionId);
        
        List<PromotionProduct> promotionProducts = promotionProductService.getActiveProductsByPromotion(promotionId);
        
        return new ApiResponse<>(
                true,
                "Lấy danh sách sản phẩm đang áp dụng khuyến mãi active thành công!",
                promotionProducts
        );
    }
    
    /**
     * Lấy danh sách khuyến mãi đang áp dụng cho sản phẩm active
     * 
     * @param productId ID của sản phẩm
     * @return ApiResponse<List<PromotionProduct>>
     */
    @GetMapping("/active/by-product/{productId}")
    @Operation(summary = "Lấy danh sách khuyến mãi đang áp dụng cho sản phẩm active", 
               description = "Lấy danh sách khuyến mãi đang áp dụng cho sản phẩm active. Trả về ApiResponse<List<PromotionProduct>>")
    public ApiResponse<List<PromotionProduct>> getActivePromotionsByProduct(
            @Parameter(description = "ID của sản phẩm") @PathVariable String productId) {
        
        log.info("Getting active promotions for product {}", productId);
        
        List<PromotionProduct> promotionProducts = promotionProductService.getActivePromotionsByProduct(productId);
        
        return new ApiResponse<>(
                true,
                "Lấy danh sách khuyến mãi đang áp dụng cho sản phẩm active thành công!",
                promotionProducts
        );
    }
    
    /**
     * Kiểm tra xem sản phẩm có đang áp dụng khuyến mãi không
     * 
     * @param productId ID của sản phẩm
     * @return ApiResponse<Boolean>
     */
    @GetMapping("/check/{productId}")
    @Operation(summary = "Kiểm tra sản phẩm có đang áp dụng khuyến mãi không", 
               description = "Kiểm tra xem sản phẩm có đang áp dụng khuyến mãi active không. Trả về ApiResponse<Boolean>")
    public ApiResponse<Boolean> hasActivePromotion(
            @Parameter(description = "ID của sản phẩm") @PathVariable String productId) {
        
        log.info("Checking if product {} has active promotion", productId);
        
        boolean hasPromotion = promotionProductService.hasActivePromotion(productId);
        
        return new ApiResponse<>(
                true,
                "Kiểm tra khuyến mãi thành công!",
                hasPromotion
        );
    }
    
    /**
     * Lấy thông tin chi tiết PromotionProduct
     * 
     * @param id ID của PromotionProduct
     * @return ApiResponse<PromotionProduct>
     */
    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin chi tiết PromotionProduct", 
               description = "Lấy thông tin chi tiết về việc áp dụng khuyến mãi. Trả về ApiResponse<PromotionProduct>")
    public ApiResponse<PromotionProduct> getPromotionProductById(
            @Parameter(description = "ID của PromotionProduct") @PathVariable String id) {
        
        log.info("Getting promotion product by ID: {}", id);
        
        PromotionProduct promotionProduct = promotionProductService.getPromotionProductById(id);
        
        return new ApiResponse<>(
                true,
                "Lấy thông tin áp dụng khuyến mãi thành công!",
                promotionProduct
        );
    }
    
    /**
     * Cập nhật PromotionProduct
     * 
     * @param id ID của PromotionProduct
     * @param discountAmount Số tiền giảm mới
     * @param isActive Trạng thái active mới
     * @return ApiResponse<PromotionProduct>
     */
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật PromotionProduct", 
               description = "Cập nhật thông tin áp dụng khuyến mãi. Trả về ApiResponse<PromotionProduct>")
    public ApiResponse<PromotionProduct> updatePromotionProduct(
            @Parameter(description = "ID của PromotionProduct") @PathVariable String id,
            @Parameter(description = "Số tiền giảm mới") @RequestParam(required = false) Double discountAmount,
            @Parameter(description = "Trạng thái active mới") @RequestParam(required = false) Boolean isActive) {
        
        log.info("Updating promotion product with ID: {}", id);
        
        PromotionProduct promotionProduct = promotionProductService.updatePromotionProduct(id, discountAmount, isActive);
        
        return new ApiResponse<>(
                true,
                "Cập nhật thông tin áp dụng khuyến mãi thành công!",
                promotionProduct
        );
    }
    
    /**
     * Xóa PromotionProduct
     * 
     * @param id ID của PromotionProduct
     * @return ResponseEntity<ApiResponse<Void>>
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa PromotionProduct", 
               description = "Xóa thông tin áp dụng khuyến mãi. Trả về ApiResponse<Void>")
    public ResponseEntity<ApiResponse<Void>> deletePromotionProduct(
            @Parameter(description = "ID của PromotionProduct") @PathVariable String id) {
        
        log.info("Deleting promotion product with ID: {}", id);
        
        promotionProductService.deletePromotionProduct(id);
        
        ApiResponse<Void> response = new ApiResponse<>(
                true,
                "Xóa thông tin áp dụng khuyến mãi thành công!",
                null
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Lấy tất cả PromotionProduct
     * 
     * @return ApiResponse<List<PromotionProduct>>
     */
    @GetMapping
    @Operation(summary = "Lấy tất cả PromotionProduct", 
               description = "Lấy danh sách tất cả thông tin áp dụng khuyến mãi. Trả về ApiResponse<List<PromotionProduct>>")
    public ApiResponse<List<PromotionProduct>> getAllPromotionProducts() {
        
        log.info("Getting all promotion products");
        
        List<PromotionProduct> promotionProducts = promotionProductService.getAllPromotionProducts();
        
        return new ApiResponse<>(
                true,
                "Lấy danh sách tất cả thông tin áp dụng khuyến mãi thành công!",
                promotionProducts
        );
    }
}
