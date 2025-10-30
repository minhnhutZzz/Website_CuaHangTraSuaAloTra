package com.example.spring_boot.controllers.modules;

import com.example.spring_boot.domains.order.Order;
import com.example.spring_boot.dto.ApiResponse;
import com.example.spring_boot.dto.PageResponse;
import com.example.spring_boot.services.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Order Controller", description = "Quản lý đơn hàng cho Admin")
public class AdminOrderController {

    private final OrderService orderService;

    /**
     * Lấy danh sách đơn hàng với filter
     * GET /api/orders?filter=all&page=0&size=10
     */
    @GetMapping
    @Operation(summary = "Lấy danh sách đơn hàng với filter")
    public ResponseEntity<ApiResponse<PageResponse<Order>>> getOrders(
            @RequestParam(defaultValue = "all") String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, 
                Sort.by(Sort.Direction.fromString(sortDir), sortBy));
            
            Page<Order> ordersPage = orderService.getOrdersWithFilter(filter, pageable);
            
            PageResponse<Order> response = new PageResponse<>(
                ordersPage.getContent(),
                ordersPage.getTotalElements(),
                page,
                size
            );
            
            return ResponseEntity.ok(ApiResponse.success(response, "Orders retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error getting orders", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Error getting orders: " + e.getMessage()));
        }
    }

    /**
     * Lấy chi tiết đơn hàng
     * GET /api/admin/orders/{orderId}
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "Lấy chi tiết đơn hàng")
    public ResponseEntity<ApiResponse<Order>> getOrderDetail(@PathVariable String orderId) {
        try {
            Order order = orderService.getOrderById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            
            return ResponseEntity.ok(ApiResponse.success(order, "Order retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error getting order detail", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Error getting order: " + e.getMessage()));
        }
    }

    /**
     * Cập nhật trạng thái đơn hàng
     * PUT /api/admin/orders/{orderId}/status
     */
    @PutMapping("/{orderId}/status")
    @Operation(summary = "Cập nhật trạng thái đơn hàng")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(
            @PathVariable String orderId,
            @RequestBody Map<String, String> request) {
        
        try {
            String newStatus = request.get("status");
            if (newStatus == null || newStatus.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.fail("Status is required"));
            }
            
            Order order = orderService.updateOrderStatus(orderId, newStatus);
            
            return ResponseEntity.ok(ApiResponse.success(order, "Order status updated successfully"));
            
        } catch (Exception e) {
            log.error("Error updating order status", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Error updating order status: " + e.getMessage()));
        }
    }

    /**
     * Cập nhật đơn hàng COD khi khách nhận hàng và thanh toán
     * POST /api/admin/orders/{orderId}/cod-delivered
     */
    @PostMapping("/{orderId}/cod-delivered")
    @Operation(summary = "Cập nhật đơn hàng COD đã nhận hàng và thanh toán")
    public ResponseEntity<ApiResponse<Order>> updateCODOrderToDelivered(@PathVariable String orderId) {
        try {
            Order order = orderService.updateCODOrderToDelivered(orderId);
            return ResponseEntity.ok(ApiResponse.success(order, "COD order updated to delivered successfully"));
            
        } catch (Exception e) {
            log.error("Error updating COD order to delivered", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Error updating COD order: " + e.getMessage()));
        }
    }

    /**
     * Lấy thống kê đơn hàng
     * GET /api/admin/orders/stats
     */
    @GetMapping("/stats")
    @Operation(summary = "Lấy thống kê đơn hàng")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrderStats() {
        try {
            Map<String, Object> stats = orderService.getOrderStatistics();
            return ResponseEntity.ok(ApiResponse.success(stats, "Order statistics retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error getting order stats", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Error getting order stats: " + e.getMessage()));
        }
    }
}
