package com.example.spring_boot.controllers.pages;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller để test API đơn giản
 */
@Controller
@RequestMapping("/api-test-simple")
public class ApiTestSimpleController {
    
    @GetMapping
    public String apiTestSimple() {
        return "test/api-test-simple";
    }
}
