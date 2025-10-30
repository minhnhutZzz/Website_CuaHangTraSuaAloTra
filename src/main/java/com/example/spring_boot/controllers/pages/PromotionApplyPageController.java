package com.example.spring_boot.controllers.pages;

import com.example.spring_boot.domains.Promotion;
import com.example.spring_boot.domains.PromotionProduct;
import com.example.spring_boot.domains.products.Product;
import com.example.spring_boot.services.PromotionService;
import com.example.spring_boot.services.PromotionProductService;
import com.example.spring_boot.services.products.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller cho trang áp dụng khuyến mãi
 * 
 * @author Spring Boot Team
 * @version 1.0
 */
@Slf4j
@Controller
@RequestMapping("/admin/promotion-apply")
@RequiredArgsConstructor
public class PromotionApplyPageController {
    
    private final PromotionService promotionService;
    private final ProductService productService;
    private final PromotionProductService promotionProductService;
    
    /**
     * Trang áp dụng khuyến mãi
     * 
     * @param model Model để truyền dữ liệu
     * @param promotionId ID của khuyến mãi (tùy chọn)
     * @return Tên view
     */
    @GetMapping
    public String promotionApplyPage(Model model,
                                   @RequestParam(required = false) String promotionId) {
        
        log.info("Loading promotion apply page with promotionId: {}", promotionId);
        
        try {
            // Lấy danh sách khuyến mãi active
            List<Promotion> activePromotions = promotionService.getCurrentActivePromotions();
            model.addAttribute("activePromotions", activePromotions);
            
            // Lấy danh sách sản phẩm active
            List<Product> activeProducts = productService.getAllActive(0, 1000).items;
            model.addAttribute("activeProducts", activeProducts);
            
            // Nếu có promotionId, lấy thông tin khuyến mãi và sản phẩm đã áp dụng
            if (promotionId != null && !promotionId.trim().isEmpty()) {
                try {
                    Promotion selectedPromotion = promotionService.getPromotionById(promotionId);
                    model.addAttribute("selectedPromotion", selectedPromotion);
                    
                    // Lấy danh sách sản phẩm đã áp dụng khuyến mãi này
                    List<PromotionProduct> appliedProducts = promotionProductService.getProductsByPromotion(promotionId);
                    model.addAttribute("appliedProducts", appliedProducts);
                    
                    // Lấy danh sách sản phẩm chưa áp dụng
                    List<String> appliedProductIds = appliedProducts.stream()
                            .map(PromotionProduct::getProductId)
                            .toList();
                    
                    List<Product> availableProducts = activeProducts.stream()
                            .filter(product -> !appliedProductIds.contains(product.getId()))
                            .toList();
                    model.addAttribute("availableProducts", availableProducts);
                    
                } catch (Exception e) {
                    log.error("Error loading promotion details for ID: {}", promotionId, e);
                    model.addAttribute("error", "Không tìm thấy khuyến mãi với ID: " + promotionId);
                }
            }
            
        } catch (Exception e) {
            log.error("Error loading promotion apply page", e);
            model.addAttribute("error", "Có lỗi xảy ra khi tải trang áp dụng khuyến mãi");
        }
        
        return "admin/promotion-apply/enhanced";
    }
    
    /**
     * Trang chọn sản phẩm để áp dụng khuyến mãi
     * 
     * @param promotionId ID của khuyến mãi
     * @param model Model để truyền dữ liệu
     * @return Tên view
     */
    @GetMapping("/select-products/{promotionId}")
    public String selectProductsPage(@PathVariable String promotionId, Model model) {
        
        log.info("Loading select products page for promotion: {}", promotionId);
        
        try {
            // Lấy thông tin khuyến mãi
            Promotion promotion = promotionService.getPromotionById(promotionId);
            model.addAttribute("promotion", promotion);
            
            // Lấy danh sách sản phẩm active
            List<Product> activeProducts = productService.getAllActive(0, 1000).items;
            model.addAttribute("activeProducts", activeProducts);
            
            // Lấy danh sách sản phẩm đã áp dụng khuyến mãi này
            List<PromotionProduct> appliedProducts = promotionProductService.getProductsByPromotion(promotionId);
            model.addAttribute("appliedProducts", appliedProducts);
            
            // Lấy danh sách sản phẩm chưa áp dụng
            List<String> appliedProductIds = appliedProducts.stream()
                    .map(PromotionProduct::getProductId)
                    .toList();
            
            List<Product> availableProducts = activeProducts.stream()
                    .filter(product -> !appliedProductIds.contains(product.getId()))
                    .toList();
            model.addAttribute("availableProducts", availableProducts);
            
        } catch (Exception e) {
            log.error("Error loading select products page for promotion: {}", promotionId, e);
            model.addAttribute("error", "Có lỗi xảy ra khi tải trang chọn sản phẩm");
        }
        
        return "admin/promotion-apply/select-products";
    }
    
    /**
     * Xử lý áp dụng khuyến mãi cho sản phẩm
     * 
     * @param promotionId ID của khuyến mãi
     * @param productIds Danh sách ID sản phẩm
     * @param discountAmount Số tiền giảm cụ thể (có thể null)
     * @param model Model để truyền dữ liệu
     * @return Redirect đến trang áp dụng khuyến mãi
     */
    @PostMapping("/apply")
    public String applyPromotion(@RequestParam String promotionId,
                               @RequestParam List<String> productIds,
                               @RequestParam(required = false) Double discountAmount,
                               Model model) {
        
        log.info("Applying promotion {} to {} products", promotionId, productIds.size());
        
        try {
            // Áp dụng khuyến mãi cho sản phẩm
            promotionProductService.applyPromotionToProducts(promotionId, productIds, discountAmount);
            
            return "redirect:/admin/promotion-apply?promotionId=" + promotionId + "&success=Áp dụng khuyến mãi thành công cho " + productIds.size() + " sản phẩm!";
            
        } catch (Exception e) {
            log.error("Error applying promotion to products", e);
            return "redirect:/admin/promotion-apply?promotionId=" + promotionId + "&error=Có lỗi xảy ra khi áp dụng khuyến mãi: " + e.getMessage();
        }
    }
    
    /**
     * Xử lý hủy áp dụng khuyến mãi cho sản phẩm
     * 
     * @param promotionId ID của khuyến mãi
     * @param productIds Danh sách ID sản phẩm
     * @param model Model để truyền dữ liệu
     * @return Redirect đến trang áp dụng khuyến mãi
     */
    @PostMapping("/remove")
    public String removePromotion(@RequestParam String promotionId,
                                @RequestParam List<String> productIds,
                                Model model) {
        
        log.info("Removing promotion {} from {} products", promotionId, productIds.size());
        
        try {
            // Hủy áp dụng khuyến mãi cho sản phẩm
            promotionProductService.removePromotionFromProducts(promotionId, productIds);
            
            return "redirect:/admin/promotion-apply?promotionId=" + promotionId + "&success=Hủy áp dụng khuyến mãi thành công cho " + productIds.size() + " sản phẩm!";
            
        } catch (Exception e) {
            log.error("Error removing promotion from products", e);
            return "redirect:/admin/promotion-apply?promotionId=" + promotionId + "&error=Có lỗi xảy ra khi hủy áp dụng khuyến mãi: " + e.getMessage();
        }
    }
    
    /**
     * API endpoint để lấy danh sách sản phẩm theo khuyến mãi
     * 
     * @param promotionId ID của khuyến mãi
     * @return JSON response
     */
    @GetMapping("/api/products/{promotionId}")
    @ResponseBody
    public List<Product> getProductsByPromotion(@PathVariable String promotionId) {
        log.info("Getting products for promotion: {}", promotionId);
        
        try {
            List<PromotionProduct> promotionProducts = promotionProductService.getProductsByPromotion(promotionId);
            List<String> productIds = promotionProducts.stream()
                    .map(PromotionProduct::getProductId)
                    .toList();
            
            return productService.getAllActive(0, 1000).items.stream()
                    .filter(product -> productIds.contains(product.getId()))
                    .toList();
                    
        } catch (Exception e) {
            log.error("Error getting products for promotion: {}", promotionId, e);
            return List.of();
        }
    }
    
    /**
     * API endpoint để lấy danh sách sản phẩm chưa áp dụng khuyến mãi
     * 
     * @param promotionId ID của khuyến mãi
     * @return JSON response
     */
    @GetMapping("/api/available-products/{promotionId}")
    @ResponseBody
    public List<Product> getAvailableProducts(@PathVariable String promotionId) {
        log.info("Getting available products for promotion: {}", promotionId);
        
        try {
            List<PromotionProduct> appliedProducts = promotionProductService.getProductsByPromotion(promotionId);
            List<String> appliedProductIds = appliedProducts.stream()
                    .map(PromotionProduct::getProductId)
                    .toList();
            
            return productService.getAllActive(0, 1000).items.stream()
                    .filter(product -> !appliedProductIds.contains(product.getId()))
                    .toList();
                    
        } catch (Exception e) {
            log.error("Error getting available products for promotion: {}", promotionId, e);
            return List.of();
        }
    }
}
