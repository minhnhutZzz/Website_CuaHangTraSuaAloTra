package com.example.spring_boot.domains.order;

/**
 * Enum cho trạng thái đơn hàng - 4 trạng thái chính
 */
public enum OrderStatus {
    PENDING("Chưa duyệt"),          // Đơn hàng chưa được duyệt
    APPROVED("Đã duyệt"),           // Đơn hàng đã được duyệt
    SHIPPING("Đang giao hàng"),     // Đang giao hàng
    DELIVERED("Đã nhận hàng"),      // Đã nhận hàng
    CANCELLED("Đã hủy");            // Đã hủy
    
    private final String description;
    
    OrderStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
