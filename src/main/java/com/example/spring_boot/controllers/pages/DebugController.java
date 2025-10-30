package com.example.spring_boot.controllers.pages;

import com.example.spring_boot.services.PromotionService;
import com.example.spring_boot.services.products.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller để debug các lỗi
 */
@Slf4j
@Controller
@RequestMapping("/debug")
@RequiredArgsConstructor
public class DebugController {
    
    private final PromotionService promotionService;
    private final ProductService productService;
    
    @GetMapping("/test")
    public String testPage(Model model) {
        log.info("Testing debug page");
        
        try {
            // Test PromotionService
            var promotions = promotionService.getCurrentActivePromotions();
            log.info("Found {} active promotions", promotions.size());
            model.addAttribute("promotions", promotions);
            
            // Test ProductService
            var products = productService.getAllActive(0, 10);
            log.info("Found {} products", products.items.size());
            model.addAttribute("products", products.items);
            
            model.addAttribute("status", "SUCCESS");
            
        } catch (Exception e) {
            log.error("Error in debug page", e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("status", "ERROR");
        }
        
        return "debug/test";
    }
}
