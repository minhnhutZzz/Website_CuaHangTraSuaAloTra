package com.example.spring_boot.controllers.pages;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller cho trang demo
 */
@Controller
@RequestMapping("/demo")
public class DemoController {
    
    @GetMapping("/promotion-apply")
    public String promotionApplyDemo() {
        return "admin/promotion-apply/demo";
    }
}
