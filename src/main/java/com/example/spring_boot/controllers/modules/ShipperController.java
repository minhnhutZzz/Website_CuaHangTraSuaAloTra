package com.example.spring_boot.controllers.modules;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/shipper")
public class ShipperController {

    @GetMapping
    public String dashboard() {
        return "shipper-dashboard";
    }
}