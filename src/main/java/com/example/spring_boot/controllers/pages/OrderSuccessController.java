package com.example.spring_boot.controllers.pages;

import com.example.spring_boot.domains.order.Order;
import com.example.spring_boot.services.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderSuccessController {

    private final OrderService orderService;

    @GetMapping("/ordersuccess")
    public String orderSuccess(@RequestParam(required = false) String orderId, Model model) {
        try {
            if (orderId != null && !orderId.trim().isEmpty()) {
                // Lấy thông tin đơn hàng
                Optional<Order> orderOpt = orderService.getOrderById(orderId);
                if (orderOpt.isPresent()) {
                    Order order = orderOpt.get();
                    
                    // Thêm thông tin vào model
                    model.addAttribute("orderId", order.getId());
                    model.addAttribute("orderNumber", order.getOrderNumber());
                    model.addAttribute("totalPrice", order.getTotalAmount());
                    model.addAttribute("paymentTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                    model.addAttribute("paymentMethod", order.getPaymentMethod());
                    model.addAttribute("statusText", "Đơn hàng đã được tạo thành công");
                    
                    // Thông tin giao hàng
                    model.addAttribute("customerName", order.getUsername());
                    model.addAttribute("customerPhone", order.getPhone());
                    model.addAttribute("shippingAddress", order.getAddress());
                    
                    return "clients/ordersuccess";
                }
            }
            
            // Nếu không có orderId hoặc không tìm thấy đơn hàng
            model.addAttribute("orderId", orderId != null ? orderId : "N/A");
            model.addAttribute("orderNumber", "N/A");
            model.addAttribute("totalPrice", "N/A");
            model.addAttribute("paymentTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            model.addAttribute("paymentMethod", "COD");
            model.addAttribute("statusText", "Đơn hàng đã được tạo thành công");
            
            return "clients/ordersuccess";
            
        } catch (Exception e) {
            // Nếu có lỗi, vẫn hiển thị trang thành công với thông tin cơ bản
            model.addAttribute("orderId", orderId != null ? orderId : "N/A");
            model.addAttribute("orderNumber", "N/A");
            model.addAttribute("totalPrice", "N/A");
            model.addAttribute("paymentTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            model.addAttribute("paymentMethod", "COD");
            model.addAttribute("statusText", "Đơn hàng đã được tạo thành công");
            
            return "clients/ordersuccess";
        }
    }
}
