package com.example.spring_boot.controllers.pages;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller để test API
 */
@Controller
@RequestMapping("/api-test")
public class ApiTestController {
    
    @GetMapping
    public String apiTest() {
        return "test/api-test";
    }
}
