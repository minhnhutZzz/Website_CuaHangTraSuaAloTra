package com.example.spring_boot.controllers.modules;

import com.example.spring_boot.domains.order.Order;
import com.example.spring_boot.dto.ApiResponse;
import com.example.spring_boot.services.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cod")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "COD Controller", description = "Xử lý thanh toán COD (Cash on Delivery)")
public class CODController {

    private final OrderService orderService;

    /**
     * Tạo đơn hàng COD
     * POST /api/cod/create-order
     */
    @PostMapping("/create-order")
    @Operation(summary = "Tạo đơn hàng COD")
    public ResponseEntity<ApiResponse<Order>> createCODOrder(@RequestBody Map<String, String> request) {
        try {
            String userIdOrSessionId = request.get("userIdOrSessionId");
            String userId = request.get("userId");
            String username = request.get("username");
            String phone = request.get("phone");
            String address = request.get("address");
            String notes = request.get("notes");

            if (username == null || phone == null || address == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.fail("Thiếu thông tin bắt buộc: username, phone, address"));
            }

            Order order = orderService.handleCODOrderWithOptionalUser(userIdOrSessionId, username, phone, address, notes, userId);
            
            log.info("COD order created: {}", order.getId());
            return ResponseEntity.ok(ApiResponse.success(order, "Đơn hàng COD đã được tạo thành công!"));
            
        } catch (Exception e) {
            log.error("Error creating COD order", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Lỗi tạo đơn hàng COD: " + e.getMessage()));
        }
    }

    /**
     * Cập nhật đơn hàng COD đã nhận hàng và thanh toán
     * POST /api/cod/delivered/{orderId}
     */
    @PostMapping("/delivered/{orderId}")
    @Operation(summary = "Cập nhật đơn hàng COD đã nhận hàng và thanh toán")
    public ResponseEntity<ApiResponse<Order>> updateCODOrderToDelivered(@PathVariable String orderId) {
        try {
            Order order = orderService.updateCODOrderToDelivered(orderId);
            
            log.info("COD order updated to delivered for order: {}", orderId);
            return ResponseEntity.ok(ApiResponse.success(order, "Đã cập nhật đơn hàng COD: Khách đã nhận hàng và thanh toán!"));
            
        } catch (Exception e) {
            log.error("Error updating COD order to delivered for order: {}", orderId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Lỗi cập nhật đơn hàng COD: " + e.getMessage()));
        }
    }

    /**
     * Lấy danh sách đơn hàng COD chờ xác nhận
     * GET /api/cod/pending-orders
     */
    @GetMapping("/pending-orders")
    @Operation(summary = "Lấy danh sách đơn hàng COD chờ xác nhận")
    public ResponseEntity<ApiResponse<Object>> getPendingCODOrders() {
        try {
            // TODO: Implement getPendingCODOrders method in OrderService
            return ResponseEntity.ok(ApiResponse.success("Chức năng đang phát triển", "Danh sách đơn hàng COD chờ xác nhận"));
            
        } catch (Exception e) {
            log.error("Error getting pending COD orders", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("Lỗi lấy danh sách đơn hàng COD: " + e.getMessage()));
        }
    }
}
