package com.example.spring_boot.domains.order;

/**
 * Enum cho phương thức thanh toán
 */
public enum PaymentMethod {
    VNPAY("Thanh toán VNPay"),
    COD("Thanh toán khi nhận hàng (COD)");
    
    private final String description;
    
    PaymentMethod(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
