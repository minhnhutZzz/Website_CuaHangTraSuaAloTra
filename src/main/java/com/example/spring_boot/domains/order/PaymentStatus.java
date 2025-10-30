package com.example.spring_boot.domains.order;

/**
 * Enum cho trạng thái thanh toán - đơn giản hóa
 */
public enum PaymentStatus {
    PAID("Đã thanh toán"),         // VNPay: Đã thanh toán
    COD_PAID("Đã thanh toán"),      // COD: Đã thanh toán khi nhận hàng
    FAILED("Thanh toán thất bại"),
    REFUNDED("Đã hoàn tiền");
    
    private final String description;
    
    PaymentStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
