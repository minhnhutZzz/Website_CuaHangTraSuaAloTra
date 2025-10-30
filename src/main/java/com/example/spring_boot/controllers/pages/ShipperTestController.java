package com.example.spring_boot.controllers.pages;

import com.example.spring_boot.domains.order.Order;
import com.example.spring_boot.domains.order.OrderItem;
import com.example.spring_boot.services.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/shipper-test")
public class ShipperTestController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String testPage(Model model) {
        return "test/shipper-test";
    }
    
    @GetMapping("/debug")
    public String debugPage(Model model) {
        return "test/shipper-debug";
    }
    
    @GetMapping("/trà-sữa-demo")
    public String tràSữaDemoPage(Model model) {
        return "test/trà-sữa-demo";
    }
    
    @GetMapping("/header-demo")
    public String headerDemoPage(Model model) {
        return "test/header-demo";
    }
    
    @GetMapping("/search-test")
    public String searchTestPage(Model model) {
        return "test/search-test";
    }
    
    @GetMapping("/search-simple")
    public String searchSimplePage(Model model) {
        return "test/search-simple";
    }
    
    @GetMapping("/review-demo")
    public String reviewDemoPage(Model model) {
        return "test/review-demo";
    }
    
    @GetMapping("/footer-demo")
    public String footerDemoPage(Model model) {
        return "test/footer-demo";
    }

    @PostMapping("/create-test-orders")
    public String createTestOrders() {
        // Tạo 5 đơn hàng test với các trạng thái khác nhau
        for (int i = 1; i <= 5; i++) {
            Order order = new Order();
            order.setUserId("test_user_" + i);
            order.setSessionId("test_session_" + i);
            order.setOrderNumber("TEST" + String.format("%03d", i));
            order.setUsername("Khách hàng " + i);
            order.setPhone("012345678" + i);
            order.setAddress("Địa chỉ " + i + ", Quận " + i + ", TP.HCM");
            order.setNotes("Ghi chú đơn hàng " + i);
            order.setTotalAmount(100000.0 * i);
            order.setPaymentMethod("COD");
            order.setPaymentStatus("PENDING");
            order.setCreatedAt(LocalDateTime.now().minusHours(i));
            order.setUpdatedAt(LocalDateTime.now().minusHours(i));
            
            // Tạo items cho đơn hàng
            List<OrderItem> items = new ArrayList<>();
            for (int j = 1; j <= 3; j++) {
                OrderItem item = new OrderItem();
                item.setProductId("product_" + i + "_" + j);
                item.setProductName("Sản phẩm " + i + "." + j);
                item.setPrice(50000.0 * j);
                item.setQuantity(j);
                items.add(item);
            }
            order.setItems(items);
            
            // Đặt trạng thái khác nhau
            switch (i) {
                case 1:
                case 2:
                    order.setStatus("PENDING");
                    break;
                case 3:
                    order.setStatus("ACCEPTED");
                    order.setShipperId("shipper123");
                    break;
                case 4:
                    order.setStatus("DELIVERED");
                    order.setShipperId("shipper123");
                    order.setDeliveredAt(LocalDateTime.now().minusDays(1));
                    break;
                case 5:
                    order.setStatus("PENDING");
                    break;
            }
            
            orderService.saveOrder(order);
        }
        
        return "redirect:/shipper-test?success=true";
    }
    
    @PostMapping("/delete-test-orders")
    public String deleteTestOrders() {
        // Xóa tất cả đơn hàng test (có orderNumber bắt đầu bằng "TEST")
        orderService.deleteTestOrders();
        return "redirect:/shipper-test?deleted=true";
    }
    
    @GetMapping("/admin-sidebar-demo")
    public String adminSidebarDemo(Model model) {
        return "test/admin-sidebar-demo";
    }
    
    @GetMapping("/order-sync-demo")
    public String orderSyncDemo(Model model) {
        return "test/order-sync-demo";
    }
    
    @GetMapping("/order-notification-demo")
    public String orderNotificationDemo(Model model) {
        return "test/order-notification-demo";
    }
    
    @GetMapping("/create-test-orders")
    public String createTestOrders(Model model) {
        return "test/create-test-orders";
    }
    
    @PostMapping("/create-test-orders-api")
    public ResponseEntity<Map<String, Object>> createTestOrdersApi() {
        try {
            String sessionId = "session_" + System.currentTimeMillis();
            
            // Tạo các đơn hàng test với các trạng thái khác nhau
            List<Order> testOrders = new ArrayList<>();
            
            // PENDING order
            Order pendingOrder = new Order();
            pendingOrder.setUserId(sessionId);
            pendingOrder.setUsername("Nguyễn Văn A");
            pendingOrder.setPhone("0123456789");
            pendingOrder.setAddress("123 Đường ABC, Quận 1, TP.HCM");
            pendingOrder.setTotalAmount(150000.0);
            pendingOrder.setPaymentMethod("COD");
            pendingOrder.setStatus("PENDING");
            pendingOrder.setOrderNumber("TEST_" + System.currentTimeMillis() + "_1");
            pendingOrder.setCreatedAt(LocalDateTime.now());
            pendingOrder.setUpdatedAt(LocalDateTime.now());
            testOrders.add(orderService.saveOrder(pendingOrder));
            
            // APPROVED order
            Order approvedOrder = new Order();
            approvedOrder.setUserId(sessionId);
            approvedOrder.setUsername("Trần Thị B");
            approvedOrder.setPhone("0987654321");
            approvedOrder.setAddress("456 Đường XYZ, Quận 2, TP.HCM");
            approvedOrder.setTotalAmount(200000.0);
            approvedOrder.setPaymentMethod("COD");
            approvedOrder.setStatus("APPROVED");
            approvedOrder.setOrderNumber("TEST_" + System.currentTimeMillis() + "_2");
            approvedOrder.setCreatedAt(LocalDateTime.now());
            approvedOrder.setUpdatedAt(LocalDateTime.now());
            testOrders.add(orderService.saveOrder(approvedOrder));
            
            // SHIPPING order
            Order shippingOrder = new Order();
            shippingOrder.setUserId(sessionId);
            shippingOrder.setUsername("Lê Văn C");
            shippingOrder.setPhone("0369852147");
            shippingOrder.setAddress("789 Đường DEF, Quận 3, TP.HCM");
            shippingOrder.setTotalAmount(300000.0);
            shippingOrder.setPaymentMethod("COD");
            shippingOrder.setStatus("SHIPPING");
            shippingOrder.setOrderNumber("TEST_" + System.currentTimeMillis() + "_3");
            shippingOrder.setCreatedAt(LocalDateTime.now());
            shippingOrder.setUpdatedAt(LocalDateTime.now());
            testOrders.add(orderService.saveOrder(shippingOrder));
            
            // DELIVERED order
            Order deliveredOrder = new Order();
            deliveredOrder.setUserId(sessionId);
            deliveredOrder.setUsername("Phạm Thị D");
            deliveredOrder.setPhone("0741258963");
            deliveredOrder.setAddress("321 Đường GHI, Quận 4, TP.HCM");
            deliveredOrder.setTotalAmount(250000.0);
            deliveredOrder.setPaymentMethod("COD");
            deliveredOrder.setStatus("DELIVERED");
            deliveredOrder.setOrderNumber("TEST_" + System.currentTimeMillis() + "_4");
            deliveredOrder.setCreatedAt(LocalDateTime.now());
            deliveredOrder.setUpdatedAt(LocalDateTime.now());
            testOrders.add(orderService.saveOrder(deliveredOrder));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tạo đơn hàng test thành công");
            response.put("sessionId", sessionId);
            response.put("orderCount", testOrders.size());
            response.put("orders", testOrders);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi tạo đơn hàng test: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
