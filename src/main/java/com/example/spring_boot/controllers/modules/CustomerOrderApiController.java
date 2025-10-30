package com.example.spring_boot.controllers.modules;

import com.example.spring_boot.domains.order.Order;
import com.example.spring_boot.services.order.OrderService;
import com.example.spring_boot.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/orders")
@Slf4j
public class CustomerOrderApiController {

    @Autowired
    private OrderService orderService;

    /**
     * Lấy danh sách đơn hàng của khách hàng
     * GET /api/customer/orders?page=0&size=10&status=all
     */
    @GetMapping
    @Operation(summary = "Lấy danh sách đơn hàng của khách hàng")
    public ResponseEntity<ApiResponse<Page<Order>>> getCustomerOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(required = false) String sessionId) {

        try {
            // Sử dụng sessionId từ request hoặc tạo mới
            if (sessionId == null || sessionId.trim().isEmpty()) {
                sessionId = "session_" + System.currentTimeMillis();
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Order> orders = Page.empty();

            if ("all".equals(status)) {
                orders = orderService.getOrdersBySessionId(sessionId, pageable);
            } else {
                orders = orderService.getOrdersBySessionIdAndStatus(sessionId, status, pageable);
            }

            return ResponseEntity.ok(ApiResponse.success(orders, "Orders retrieved successfully"));

        } catch (Exception e) {
            log.error("Error getting customer orders", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Error getting orders: " + e.getMessage()));
        }
    }

    /**
     * Lấy đơn hàng gần đây (cho notification)
     * GET /api/customer/orders/recent?limit=5
     */
    @GetMapping("/recent")
    @Operation(summary = "Lấy đơn hàng gần đây cho notification")
    public ResponseEntity<ApiResponse<List<Order>>> getRecentOrders(
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String sessionId) {
        try {
            Pageable pageable = PageRequest.of(0, limit);

            // Lấy theo userId nếu có
            java.util.List<Order> byUser = java.util.List.of();
            if (userId != null && !userId.trim().isEmpty()) {
                byUser = orderService.getOrdersByUserId(userId, pageable).getContent();
            }

            // Lấy theo sessionId nếu có
            java.util.List<Order> bySession = java.util.List.of();
            if (sessionId != null && !sessionId.trim().isEmpty()) {
                bySession = orderService.getOrdersBySessionId(sessionId, pageable).getContent();
            }

            // Hợp nhất: ưu tiên đơn mới nhất, loại trùng theo id, cắt limit
            java.util.Map<String, Order> distinct = new java.util.LinkedHashMap<>();
            java.util.Comparator<Order> cmp = java.util.Comparator
                    .comparing(Order::getCreatedAt, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()))
                    .reversed();
            java.util.stream.Stream.concat(byUser.stream(), bySession.stream())
                    .sorted(cmp)
                    .forEach(o -> distinct.putIfAbsent(o.getId(), o));

            java.util.List<Order> merged = distinct.values().stream()
                    .sorted(cmp)
                    .limit(limit)
                    .toList();

            return ResponseEntity.ok(ApiResponse.success(merged, "Recent orders merged successfully"));
        } catch (Exception e) {
            log.error("Error getting recent orders", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Error getting recent orders: " + e.getMessage()));
        }
    }

    /**
     * Đếm số đơn hàng theo trạng thái
     * GET /api/customer/orders/count
     */
    @GetMapping("/count")
    @Operation(summary = "Đếm số đơn hàng theo trạng thái")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getOrderCounts(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String sessionId) {
        try {
            // Lấy dữ liệu cả hai nguồn và hợp nhất, loại trùng theo id
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 100);

            java.util.List<com.example.spring_boot.domains.order.Order> byUser = java.util.List.of();
            if (userId != null && !userId.trim().isEmpty()) {
                byUser = orderService.getOrdersByUserId(userId, pageable).getContent();
            }

            java.util.List<com.example.spring_boot.domains.order.Order> bySession = java.util.List.of();
            if (sessionId != null && !sessionId.trim().isEmpty()) {
                bySession = orderService.getOrdersBySessionId(sessionId, pageable).getContent();
            }

            java.util.Map<String, com.example.spring_boot.domains.order.Order> distinct = new java.util.LinkedHashMap<>();
            java.util.stream.Stream.concat(byUser.stream(), bySession.stream())
                    .forEach(o -> distinct.putIfAbsent(o.getId(), o));

            java.util.Collection<com.example.spring_boot.domains.order.Order> merged = distinct.values();

            long pending = merged.stream().filter(o -> "PENDING".equalsIgnoreCase(o.getStatus())).count();
            long approved = merged.stream().filter(o -> "APPROVED".equalsIgnoreCase(o.getStatus())).count();
            long shipping = merged.stream().filter(o -> "SHIPPING".equalsIgnoreCase(o.getStatus())).count();

            java.util.Map<String, Long> counts = java.util.Map.of(
                    "pending", pending,
                    "approved", approved,
                    "shipping", shipping);

            return ResponseEntity.ok(ApiResponse.success(counts, "Order counts merged successfully"));
        } catch (Exception e) {
            log.error("Error getting order counts", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Error getting order counts: " + e.getMessage()));
        }
    }
}
