package com.example.spring_boot.controllers.pages;

import com.example.spring_boot.services.products.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller test đơn giản
 */
@Slf4j
@Controller
@RequestMapping("/simple-test")
@RequiredArgsConstructor
public class SimpleTestController {
    
    private final ProductService productService;
    
    @GetMapping("/products")
    public String testProducts(Model model) {
        log.info("Testing products");
        
        try {
            var products = productService.getAllActive(0, 5);
            model.addAttribute("products", products.items);
            model.addAttribute("count", products.items.size());
            return "test/simple-products";
            
        } catch (Exception e) {
            log.error("Error in test", e);
            model.addAttribute("error", e.getMessage());
            return "error/error";
        }
    }
}
