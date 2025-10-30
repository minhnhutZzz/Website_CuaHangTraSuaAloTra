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
@RequestMapping("/api/shipper")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Shipper API Controller", description = "API cho Shipper quản lý đơn hàng")
@CrossOrigin(origins = "*")
public class ShipperApiController {

    private final OrderService orderService;

    /**
     * Lấy danh sách đơn hàng có sẵn cho shipper
     * GET /api/shipper/orders/available?page=0&size=20
     */
    @GetMapping("/orders/available")
    @Operation(summary = "Lấy danh sách đơn hàng có sẵn cho shipper")
    public ResponseEntity<ApiResponse<PageResponse<Order>>> getAvailableOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, 
                Sort.by(Sort.Direction.DESC, "createdAt"));
            
            // Lấy đơn hàng có trạng thái pending hoặc accepted
            Page<Order> ordersPage = orderService.getOrdersForShipper(pageable);
            
            PageResponse<Order> response = new PageResponse<>(
                ordersPage.getContent(),
                ordersPage.getTotalElements(),
                page,
                size
            );
            
            return ResponseEntity.ok(ApiResponse.success(response, "Available orders retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error getting available orders", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Error getting available orders: " + e.getMessage()));
        }
    }

    /**
     * Shipper nhận giao đơn hàng
     * POST /api/shipper/orders/{orderId}/accept
     */
    @PostMapping("/orders/{orderId}/accept")
    @Operation(summary = "Shipper nhận giao đơn hàng")
    public ResponseEntity<ApiResponse<Order>> acceptOrder(
            @PathVariable String orderId,
            @RequestBody Map<String, String> request) {
        
        try {
            String shipperId = request.get("shipperId");
            if (shipperId == null || shipperId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.fail("Shipper ID is required"));
            }
            
            Order order = orderService.acceptOrderByShipper(orderId, shipperId);
            
            return ResponseEntity.ok(ApiResponse.success(order, "Order accepted successfully"));
            
        } catch (Exception e) {
            log.error("Error accepting order", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Error accepting order: " + e.getMessage()));
        }
    }

    /**
     * Shipper xác nhận đã giao hàng thành công
     * POST /api/shipper/orders/{orderId}/deliver
     */
    @PostMapping("/orders/{orderId}/deliver")
    @Operation(summary = "Shipper xác nhận đã giao hàng thành công")
    public ResponseEntity<ApiResponse<Order>> confirmDelivery(
            @PathVariable String orderId,
            @RequestBody Map<String, String> request) {
        
        try {
            String shipperId = request.get("shipperId");
            if (shipperId == null || shipperId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.fail("Shipper ID is required"));
            }
            
            Order order = orderService.deliverOrderByShipper(orderId, shipperId);
            
            return ResponseEntity.ok(ApiResponse.success(order, "Order delivered successfully"));
            
        } catch (Exception e) {
            log.error("Error confirming delivery", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Error confirming delivery: " + e.getMessage()));
        }
    }

    /**
     * Lấy chi tiết đơn hàng
     * GET /api/shipper/orders/{orderId}
     */
    @GetMapping("/orders/{orderId}")
    @Operation(summary = "Lấy chi tiết đơn hàng")
    public ResponseEntity<ApiResponse<Order>> getOrderDetail(@PathVariable String orderId) {
        
        try {
            Order order = orderService.getOrderById(orderId)
                    .orElse(null);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(ApiResponse.success(order, "Order detail retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error getting order detail", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Error getting order detail: " + e.getMessage()));
        }
    }

    /**
     * Lấy thống kê đơn hàng của shipper
     * GET /api/shipper/stats
     */
    @GetMapping("/stats")
    @Operation(summary = "Lấy thống kê đơn hàng của shipper")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getShipperStats(
            @RequestParam String shipperId) {
        
        try {
            Map<String, Object> stats = orderService.getShipperStats(shipperId);
            return ResponseEntity.ok(ApiResponse.success(stats, "Shipper stats retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error getting shipper stats", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Error getting shipper stats: " + e.getMessage()));
        }
    }
    
    /**
     * Lấy tất cả đơn hàng của shipper (bao gồm cả delivered)
     * GET /api/shipper/orders/all
     */
    @GetMapping("/orders/all")
    @Operation(summary = "Lấy tất cả đơn hàng của shipper")
    public ResponseEntity<ApiResponse<PageResponse<Order>>> getAllShipperOrders(
            @RequestParam String shipperId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, 
                Sort.by(Sort.Direction.DESC, "createdAt"));
            
            Page<Order> ordersPage = orderService.getAllOrdersByShipper(shipperId, pageable);
            
            PageResponse<Order> response = new PageResponse<>(
                ordersPage.getContent(),
                ordersPage.getTotalElements(),
                page,
                size
            );
            
            return ResponseEntity.ok(ApiResponse.success(response, "All shipper orders retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error getting all shipper orders", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Error getting all shipper orders: " + e.getMessage()));
        }
    }
}
