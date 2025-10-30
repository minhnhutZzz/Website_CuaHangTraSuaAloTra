package com.example.spring_boot.controllers.pages;

import com.example.spring_boot.domains.Promotion;
import com.example.spring_boot.domains.products.Product;
import com.example.spring_boot.services.PromotionService;
import com.example.spring_boot.services.products.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Controller đơn giản cho trang áp dụng khuyến mãi
 */
@Slf4j
@Controller
@RequestMapping("/admin/promotion-apply-simple")
@RequiredArgsConstructor
public class PromotionApplyPageControllerSimple {
    
    private final PromotionService promotionService;
    private final ProductService productService;
    
    @GetMapping
    public String promotionApplyPage(Model model) {
        log.info("Loading simple promotion apply page");
        
        try {
            // Lấy danh sách khuyến mãi active
            List<Promotion> activePromotions = promotionService.getCurrentActivePromotions();
            model.addAttribute("activePromotions", activePromotions);
            log.info("Found {} active promotions", activePromotions.size());
            
            // Lấy danh sách sản phẩm active
            List<Product> activeProducts = productService.getAllActive(0, 1000).items;
            model.addAttribute("activeProducts", activeProducts);
            log.info("Found {} active products", activeProducts.size());
            
            return "admin/promotion-apply/simple";
            
        } catch (Exception e) {
            log.error("Error loading promotion apply page", e);
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "error/error";
        }
    }
}
