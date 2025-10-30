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
 * Controller để test chức năng
 */
@Slf4j
@Controller
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    
    private final PromotionService promotionService;
    private final ProductService productService;
    
    @GetMapping("/price-update")
    public String testPriceUpdate(Model model) {
        log.info("Testing price update functionality");
        
        try {
            // Lấy danh sách sản phẩm
            List<Product> products = productService.getAllActive(0, 10).items;
            model.addAttribute("products", products);
            
            // Lấy danh sách khuyến mãi
            List<Promotion> promotions = promotionService.getCurrentActivePromotions();
            model.addAttribute("promotions", promotions);
            
            return "test/price-update";
            
        } catch (Exception e) {
            log.error("Error in test page", e);
            model.addAttribute("error", e.getMessage());
            return "error/error";
        }
    }
}
