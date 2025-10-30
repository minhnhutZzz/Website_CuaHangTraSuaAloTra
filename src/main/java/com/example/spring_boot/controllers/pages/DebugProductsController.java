package com.example.spring_boot.controllers.pages;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller để debug products
 */
@Controller
@RequestMapping("/debug-products")
public class DebugProductsController {
    
    @GetMapping
    public String debugProducts() {
        return "test/debug-products";
    }
}
