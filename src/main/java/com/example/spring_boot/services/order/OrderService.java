package com.example.spring_boot.services.order;

import com.example.spring_boot.domains.cart.Cart;
import com.example.spring_boot.domains.cart.CartItem;
import com.example.spring_boot.domains.order.Order;
import com.example.spring_boot.domains.order.OrderItem;
import com.example.spring_boot.repository.order.OrderRepository;
import com.example.spring_boot.services.cart.CartService;
import com.example.spring_boot.services.products.ProductService;
import com.example.spring_boot.domains.products.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private ProductService productService;
    
    /**
     * Tạo đơn hàng từ giỏ hàng
     */
    public Order createOrderFromCart(String userIdOrSessionId, String username, String phone, String address, String notes) {
        // Lấy giỏ hàng
        Cart cart = cartService.getCart(userIdOrSessionId).orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));
        
        if (cart.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }
        
        // Tạo đơn hàng
        String normalizedUserId = (userIdOrSessionId != null && !userIdOrSessionId.startsWith("session_"))
                ? userIdOrSessionId : null;
        String normalizedSessionId = (normalizedUserId == null) ? userIdOrSessionId : null;
        Order order = new Order(normalizedUserId, normalizedSessionId, username, phone, address);
        order.setNotes(notes);
        
        // Chuyển items từ cart sang order
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem(
                cartItem.getProductId(),
                cartItem.getProductName(),
                cartItem.getPrice(),
                cartItem.getQuantity()
            );
            order.addItem(orderItem);
        }
        
        // Lưu đơn hàng (chưa xóa cart)
        Order savedOrder = orderRepository.save(order);
        
        // KHÔNG xóa cart ở đây - chỉ xóa khi thanh toán thành công
        // cartService.clearCart(userIdOrSessionId);
        
        return savedOrder;
    }

    /**
     * Tạo đơn hàng từ giỏ, cho phép gán userId nếu đã đăng nhập (giỏ vẫn lấy theo sessionId/userIdOrSessionId)
     */
    public Order createOrderFromCartWithOptionalUser(String userIdOrSessionId, String username, String phone, String address, String notes, String overrideUserId) {
        Cart cart = cartService.getCart(userIdOrSessionId).orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));
        if (cart.isEmpty()) { throw new RuntimeException("Giỏ hàng trống"); }
        Order order = new Order(null, userIdOrSessionId, username, phone, address);
        if (overrideUserId != null && !overrideUserId.isBlank()) {
            order.setUserId(overrideUserId);
        }
        order.setNotes(notes);
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem(
                cartItem.getProductId(),
                cartItem.getProductName(),
                cartItem.getPrice(),
                cartItem.getQuantity()
            );
            order.addItem(orderItem);
        }
        return orderRepository.save(order);
    }
    
    /**
     * Lấy đơn hàng theo ID
     */
    public Optional<Order> getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }
    
    /**
     * Lấy đơn hàng theo order number
     */
    public Optional<Order> getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }
    
    /**
     * Lấy đơn hàng theo user ID
     */
    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
    
    /**
     * Lấy đơn hàng theo user ID với pagination
     */
    public Page<Order> getOrdersByUserId(String userId, Pageable pageable) {
        // Trả về theo thời gian tạo mới nhất để hiển thị đúng đơn vừa thanh toán
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    /**
     * Lấy đơn hàng theo session ID
     */
    public List<Order> getOrdersBySessionId(String sessionId) {
        return orderRepository.findBySessionId(sessionId);
    }
    
    /**
     * Lấy đơn hàng theo status
     */
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }
    
    /**
     * Lấy đơn hàng theo status với pagination
     */
    public Page<Order> getOrdersByStatus(String status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }
    
    /**
     * Lấy đơn hàng theo payment status
     */
    public List<Order> getOrdersByPaymentStatus(String paymentStatus) {
        return orderRepository.findByPaymentStatus(paymentStatus);
    }
    
    /**
     * Lấy đơn hàng theo payment status với pagination
     */
    public Page<Order> getOrdersByPaymentStatus(String paymentStatus, Pageable pageable) {
        return orderRepository.findByPaymentStatus(paymentStatus, pageable);
    }
    
    /**
     * Lấy đơn hàng theo user ID và status
     */
    public List<Order> getOrdersByUserIdAndStatus(String userId, String status) {
        return orderRepository.findByUserIdAndStatus(userId, status);
    }
    
    /**
     * Lấy đơn hàng theo user ID và status với pagination
     */
    public Page<Order> getOrdersByUserIdAndStatus(String userId, String status, Pageable pageable) {
        return orderRepository.findByUserIdAndStatus(userId, status, pageable);
    }
    
    /**
     * Lấy đơn hàng theo date range
     */
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByCreatedAtBetween(startDate, endDate);
    }
    
    /**
     * Lấy đơn hàng theo date range với pagination
     */
    public Page<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return orderRepository.findByCreatedAtBetween(startDate, endDate, pageable);
    }
    
    /**
     * Lấy đơn hàng theo transaction ID
     */
    public Optional<Order> getOrderByTransactionId(String transactionId) {
        return orderRepository.findByTransactionId(transactionId);
    }
    
    /**
     * Lấy đơn hàng theo phone
     */
    public List<Order> getOrdersByPhone(String phone) {
        return orderRepository.findByPhone(phone);
    }
    
    /**
     * Lấy đơn hàng theo phone với pagination
     */
    public Page<Order> getOrdersByPhone(String phone, Pageable pageable) {
        return orderRepository.findByPhone(phone, pageable);
    }
    
    /**
     * Tìm kiếm đơn hàng theo username
     */
    public List<Order> searchOrdersByUsername(String username) {
        return orderRepository.findByUsernameContainingIgnoreCase(username);
    }
    
    /**
     * Tìm kiếm đơn hàng theo username với pagination
     */
    public Page<Order> searchOrdersByUsername(String username, Pageable pageable) {
        return orderRepository.findByUsernameContainingIgnoreCase(username, pageable);
    }
    
    /**
     * Cập nhật thông tin thanh toán
     */
    public Order updatePaymentInfo(String orderId, String paymentMethod, String transactionId) {
        Order order = getOrderById(orderId).orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
        
        order.updatePaymentInfo(paymentMethod, transactionId);
        
        return orderRepository.save(order);
    }
    
    
    /**
     * Lấy tất cả đơn hàng
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    /**
     * Lấy tất cả đơn hàng với pagination
     */
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
    
    /**
     * Xóa đơn hàng
     */
    public void deleteOrder(String orderId) {
        orderRepository.deleteById(orderId);
    }
    
    /**
     * Xử lý thanh toán thành công - xóa cart và cập nhật order
     * 
     * CART: ❌ XÓA HOÀN TOÀN (clearCart)
     * ORDER: ✅ Cập nhật thành PROCESSING + PAID
     */
    public Order handlePaymentSuccess(String orderId, String transactionId) {
        Order order = getOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        
        System.out.println("=== THANH TOÁN THÀNH CÔNG ===");
        System.out.println("Order ID: " + orderId);
        System.out.println("Transaction ID: " + transactionId);
        
        // 0. Trừ kho tất cả sản phẩm trong đơn hàng (nếu thiếu thì throw ngay)
        decreaseProductStockForOrder(order);
        
        // 1. Cập nhật trạng thái Order
        order.setStatus("PENDING");              // Đơn hàng chưa duyệt
        order.setPaymentStatus("PAID");          // Đã thanh toán
        order.setPaymentMethod("VNPAY");         // Phương thức VNPay
        order.setTransactionId(transactionId);    // Mã giao dịch VNPay
        order.setPaymentTime(LocalDateTime.now()); // Thời gian thanh toán
        order.setUpdatedAt(LocalDateTime.now());
        
        // 2. XÓA CART HOÀN TOÀN (vì đã thanh toán thành công)
        String identifier = order.getUserId() != null ? order.getUserId() : order.getSessionId();
        cartService.clearCart(identifier);
        System.out.println("✅ Cart đã được xóa cho user: " + identifier);
        
        // 3. Lưu Order
        Order savedOrder = orderRepository.save(order);
        System.out.println("✅ Order đã được cập nhật: " + savedOrder.getId());
        
        return savedOrder;
    }
    
    /**
     * Xử lý đơn hàng COD - tạo đơn hàng chờ thanh toán
     * 
     * CART: ❌ XÓA HOÀN TOÀN (clearCart)
     * ORDER: ✅ Tạo với trạng thái PENDING + COD_PENDING
     */
    public Order handleCODOrder(String userIdOrSessionId, String username, String phone, String address, String notes) {
        System.out.println("=== TẠO ĐƠN HÀNG COD ===");
        System.out.println("User/Session: " + userIdOrSessionId);
        
        // 1. Tạo đơn hàng từ cart
        Order order = createOrderFromCart(userIdOrSessionId, username, phone, address, notes);
        
        // 0. Trừ kho tất cả sản phẩm trong đơn hàng (nếu thiếu thì throw ngay)
        decreaseProductStockForOrder(order);
        
        // 2. Cập nhật thông tin thanh toán COD
        order.setPaymentMethod("COD");
        order.setPaymentStatus("COD_PAID");  // COD không cần chờ thanh toán
        order.setStatus("PENDING");          // Đơn hàng chưa duyệt
        order.setUpdatedAt(LocalDateTime.now());
        
        // 3. XÓA CART (vì đã tạo đơn hàng)
        cartService.clearCart(userIdOrSessionId);
        System.out.println("✅ Cart đã được xóa cho user: " + userIdOrSessionId);
        
        // 4. Lưu Order
        Order savedOrder = orderRepository.save(order);
        System.out.println("✅ Đơn hàng COD đã được tạo: " + savedOrder.getId());
        
        return savedOrder;
    }

    /**
     * Tạo đơn COD, lấy giỏ bằng identifier (ưu tiên sessionId) nhưng gán chủ đơn theo userId nếu có
     */
    public Order handleCODOrderWithOptionalUser(String userIdOrSessionId, String username, String phone, String address, String notes, String overrideUserId) {
        // 1. Tạo đơn hàng từ cart (sử dụng identifier để load cart)
        Order order = createOrderFromCart(userIdOrSessionId, username, phone, address, notes);
        // 2. Nếu có user đăng nhập, gán userId cho order
        if (overrideUserId != null && !overrideUserId.isBlank()) {
            order.setUserId(overrideUserId);
        }
        // 3. Trừ kho
        decreaseProductStockForOrder(order);
        // 4. Cập nhật thông tin COD
        order.setPaymentMethod("COD");
        order.setPaymentStatus("COD_PAID");
        order.setStatus("PENDING");
        order.setUpdatedAt(LocalDateTime.now());
        // 5. Xóa giỏ theo identifier (sessionId)
        cartService.clearCart(userIdOrSessionId);
        // 6. Lưu
        return orderRepository.save(order);
    }
    
    /**
     * Cập nhật trạng thái đơn hàng COD khi khách nhận hàng và thanh toán
     * 
     * ORDER: ✅ Cập nhật thành DELIVERED + COD_PAID
     */
    public Order updateCODOrderToDelivered(String orderId) {
        Order order = getOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        
        if (!"COD".equals(order.getPaymentMethod())) {
            throw new IllegalArgumentException("Order is not COD payment method");
        }
        
        if (!"SHIPPING".equals(order.getStatus())) {
            throw new IllegalArgumentException("Order must be in SHIPPING status to be delivered");
        }
        
        System.out.println("=== CẬP NHẬT ĐƠN HÀNG COD ĐÃ NHẬN HÀNG ===");
        System.out.println("Order ID: " + orderId);
        
        // Cập nhật trạng thái - khách đã nhận hàng (COD đã thanh toán từ trước)
        order.setStatus("DELIVERED");
        // COD_PAID đã được set từ khi tạo đơn hàng
        order.setPaymentTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        // Lưu Order
        Order savedOrder = orderRepository.save(order);
        System.out.println("✅ Đơn hàng COD đã được cập nhật: " + savedOrder.getId());
        
        return savedOrder;
    }
    
    /**
     * Lấy đơn hàng với filter cho Admin
     */
    public Page<Order> getOrdersWithFilter(String filter, Pageable pageable) {
        if ("all".equals(filter)) {
            return orderRepository.findAll(pageable);
        } else if ("pending".equals(filter)) {
            return orderRepository.findByStatus("PENDING", pageable);
        } else if ("approved".equals(filter)) {
            return orderRepository.findByStatus("APPROVED", pageable);
        } else if ("shipping".equals(filter)) {
            return orderRepository.findByStatus("SHIPPING", pageable);
        } else if ("delivered".equals(filter)) {
            return orderRepository.findByStatus("DELIVERED", pageable);
        } else {
            return orderRepository.findAll(pageable);
        }
    }
    
    /**
     * Cập nhật trạng thái đơn hàng
     */
    public Order updateOrderStatus(String orderId, String newStatus) {
        Order order = getOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    
    /**
     * Lấy thống kê đơn hàng
     */
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> stats = new java.util.HashMap<>();
        
        // Tổng số đơn hàng
        long totalOrders = orderRepository.count();
        stats.put("totalOrders", totalOrders);
        
        // Đơn hàng theo trạng thái
        stats.put("pendingOrders", orderRepository.countByStatus("PENDING"));
        stats.put("processingOrders", orderRepository.countByStatus("PROCESSING"));
        stats.put("shippedOrders", orderRepository.countByStatus("SHIPPED"));
        stats.put("deliveredOrders", orderRepository.countByStatus("DELIVERED"));
        stats.put("cancelledOrders", orderRepository.countByStatus("CANCELLED"));
        
        // Đơn hàng theo phương thức thanh toán
        stats.put("vnpayOrders", orderRepository.countByPaymentMethod("VNPAY"));
        stats.put("codOrders", orderRepository.countByPaymentMethod("COD"));
        
        // Đơn hàng COD chờ thanh toán
        stats.put("codPendingOrders", orderRepository.countByPaymentStatus("COD_PENDING"));
        
        return stats;
    }
    
    /**
     * Xử lý thanh toán thất bại - chỉ cập nhật trạng thái
     * 
     * CART: ✅ GIỮ NGUYÊN (không xóa)
     * ORDER: ❌ Cập nhật thành CANCELLED + FAILED
     */
    public Order handlePaymentFailure(String orderId, String reason) {
        Order order = getOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        
        System.out.println("=== THANH TOÁN THẤT BẠI ===");
        System.out.println("Order ID: " + orderId);
        System.out.println("Reason: " + reason);
        
        // 1. Cập nhật trạng thái Order
        order.setStatus("CANCELLED");            // Đơn hàng bị hủy
        order.setPaymentStatus("FAILED");         // Thanh toán thất bại
        if (order.getPaymentMethod() == null || order.getPaymentMethod().isBlank()) {
            order.setPaymentMethod("VNPAY");      // Lưu phương thức để UI không null
        }
        order.setTransactionId(null);            // Không có mã giao dịch
        order.setPaymentTime(null);              // Không có thời gian thanh toán
        order.setUpdatedAt(LocalDateTime.now());
        
        // 2. KHÔNG XÓA CART (để user có thể thử lại)
        System.out.println("✅ Cart được giữ nguyên để user thử lại");
        
        // 3. Lưu Order
        Order savedOrder = orderRepository.save(order);
        System.out.println("❌ Order đã được hủy: " + savedOrder.getId());
        
        return savedOrder;
    }
    
    /**
     * Đếm đơn hàng theo status
     */
    public long countOrdersByStatus(String status) {
        return orderRepository.countByStatus(status);
    }
    
    /**
     * Đếm đơn hàng theo payment status
     */
    public long countOrdersByPaymentStatus(String paymentStatus) {
        return orderRepository.countByPaymentStatus(paymentStatus);
    }
    
    /**
     * Đếm đơn hàng theo user ID
     */
    public long countOrdersByUserId(String userId) {
        return orderRepository.countByUserId(userId);
    }
    
    // ==================== SHIPPER METHODS ====================
    
    /**
     * Lấy đơn hàng cho shipper (chỉ APPROVED và SHIPPING)
     */
    public Page<Order> getOrdersForShipper(Pageable pageable) {
        return orderRepository.findByStatusIn(
            List.of("APPROVED", "SHIPPING"),
            pageable
        );
    }
    
    /**
     * Shipper nhận giao đơn hàng (có thể ở trạng thái PENDING hoặc APPROVED)
     */
    public Order acceptOrderByShipper(String orderId, String shipperId) {
        Order order = getOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        String currentStatus = order.getStatus();
        // Cho nhận nếu ở PENDING hoặc APPROVED
        if (currentStatus == null ||
            (!currentStatus.equalsIgnoreCase("pending") && !currentStatus.equalsIgnoreCase("approved"))) {
            throw new IllegalStateException("Order is not available for delivery. Current status: " + currentStatus);
        }
        // Cập nhật trạng thái thành SHIPPING (đang giao)
        order.setStatus("SHIPPING");
        order.setShipperId(shipperId);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
    
    /**
     * Shipper xác nhận đã giao hàng thành công (chỉ 1 shipper nên bỏ kiểm tra shipperId)
     */
    public Order deliverOrderByShipper(String orderId, String shipperId) {
        Order order = getOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        String currentStatus = order.getStatus();
        // Cho xác nhận nếu trạng thái đang là SHIPPING, ACCEPTED hoặc APPROVED
        if (currentStatus == null ||
            !(currentStatus.equalsIgnoreCase("shipping")
              || currentStatus.equalsIgnoreCase("accepted")
              || currentStatus.equalsIgnoreCase("approved"))) {
            throw new IllegalStateException("Order is not in shipping/approved status.");
        }
        // Auto set lại shipperId nếu chưa có (dự án chỉ có 1 shipper)
        if (order.getShipperId() == null || order.getShipperId().isEmpty()) {
            order.setShipperId(shipperId);
        }
        order.setStatus("DELIVERED");
        order.setDeliveredAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
    
    /**
     * Lấy thống kê đơn hàng của shipper (chỉ đơn hàng COD)
     */
    public Map<String, Object> getShipperStats(String shipperId) {
        // Đếm với status không phân biệt hoa thường và chỉ đơn hàng COD
        long pending = orderRepository.countByShipperIdAndStatusAndPaymentMethod(shipperId, "pending", "COD") +
                       orderRepository.countByShipperIdAndStatusAndPaymentMethod(shipperId, "PENDING", "COD");
        long shipping = orderRepository.countByShipperIdAndStatusAndPaymentMethod(shipperId, "shipping", "COD") +
                        orderRepository.countByShipperIdAndStatusAndPaymentMethod(shipperId, "SHIPPING", "COD");
        long delivered = orderRepository.countByShipperIdAndStatusAndPaymentMethod(shipperId, "delivered", "COD") +
                         orderRepository.countByShipperIdAndStatusAndPaymentMethod(shipperId, "DELIVERED", "COD");
        long total = pending + shipping + delivered;
        
        return Map.of(
            "pending", pending,
            "accepted", shipping, // Hiển thị "accepted" nhưng thực tế là "shipping"
            "delivered", delivered,
            "total", total
        );
    }
    
    /**
     * Lưu đơn hàng
     */
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    /**
     * Tạo đơn hàng VNPay ở trạng thái chờ thanh toán (PENDING)
     * - Không xóa giỏ hàng tại thời điểm này
     * - Gắn phương thức thanh toán là VNPAY để UI không bị null
     */
    public Order createVnpayPendingOrder(String userIdOrSessionId, String username, String phone, String address, String notes, String overrideUserId) {
        // Tạo đơn từ giỏ (có thể chỉ có sessionId). Nếu thiếu thông tin KH thì cho phép truyền rỗng
        if (username == null) username = "";
        if (phone == null) phone = "";
        if (address == null) address = "";
        if (notes == null) notes = "";

        Order order = createOrderFromCartWithOptionalUser(userIdOrSessionId, username, phone, address, notes, overrideUserId);
        if (overrideUserId != null && !overrideUserId.isBlank()) {
            order.setUserId(overrideUserId);
        }
        order.setPaymentMethod("VNPAY");
        order.setPaymentStatus("PENDING");
        order.setStatus("PENDING");
        order.setUpdatedAt(LocalDateTime.now());
        return saveOrder(order);
    }
    
    /**
     * Lấy tất cả đơn hàng của shipper (bao gồm cả delivered)
     */
    public Page<Order> getAllOrdersByShipper(String shipperId, Pageable pageable) {
        return orderRepository.findByShipperId(shipperId, pageable);
    }
    
    /**
     * Xóa tất cả đơn hàng test (có orderNumber bắt đầu bằng "TEST")
     */
    public void deleteTestOrders() {
        orderRepository.deleteByOrderNumberStartingWith("TEST");
    }
    
    /**
     * Đếm đơn hàng theo date range
     */
    public long countOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.countByCreatedAtBetween(startDate, endDate);
    }
    
    // ==================== CUSTOMER ORDER METHODS ====================
    
    /**
     * Lấy đơn hàng theo sessionId
     */
    public Page<Order> getOrdersBySessionId(String sessionId, Pageable pageable) {
        return orderRepository.findBySessionIdOrderByCreatedAtDesc(sessionId, pageable);
    }
    
    /**
     * Lấy đơn hàng theo sessionId và status
     */
    public Page<Order> getOrdersBySessionIdAndStatus(String sessionId, String status, Pageable pageable) {
        return orderRepository.findBySessionIdAndStatusOrderByCreatedAtDesc(sessionId, status, pageable);
    }
    
    /**
     * Đếm đơn hàng theo sessionId và status
     */
    public Map<String, Long> getOrderCountsBySessionId(String sessionId) {
        long pending = orderRepository.countBySessionIdAndStatus(sessionId, "PENDING");
        long approved = orderRepository.countBySessionIdAndStatus(sessionId, "APPROVED");
        long shipping = orderRepository.countBySessionIdAndStatus(sessionId, "SHIPPING");
        long delivered = orderRepository.countBySessionIdAndStatus(sessionId, "DELIVERED");
        long cancelled = orderRepository.countBySessionIdAndStatus(sessionId, "CANCELLED");
        long total = orderRepository.countBySessionId(sessionId);
        
        return Map.of(
            "pending", pending,
            "approved", approved,
            "shipping", shipping,
            "delivered", delivered,
            "cancelled", cancelled,
            "total", total
        );
    }
    
    /**
     * Đếm đơn hàng theo userId và trạng thái
     */
    public Map<String, Long> getOrderCountsByUserId(String userId) {
        long pending = orderRepository.countByUserIdAndStatus(userId, "PENDING");
        long approved = orderRepository.countByUserIdAndStatus(userId, "APPROVED");
        long shipping = orderRepository.countByUserIdAndStatus(userId, "SHIPPING");
        long delivered = orderRepository.countByUserIdAndStatus(userId, "DELIVERED");
        long cancelled = orderRepository.countByUserIdAndStatus(userId, "CANCELLED");
        long total = pending + approved + shipping + delivered + cancelled;
        return Map.of(
                "pending", pending,
                "approved", approved,
                "shipping", shipping,
                "delivered", delivered,
                "cancelled", cancelled,
                "total", total
        );
    }
    
    /**
     * Trừ tồn kho sản phẩm theo đơn hàng - rollback ngay khi thiếu hàng
     */
    private void decreaseProductStockForOrder(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = productService.getById(item.getProductId());
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Sản phẩm '" + product.getName() + "' không đủ hàng trong kho.");
            }
            product.setStock(product.getStock() - item.getQuantity());
            productService.update(product.getId(), product);
        }
    }
}
