package com.invox.invoice_system.enums;

public enum PaymentMethod {
    CASH("Cash"),                 // Tiền mặt
    CARD("Card"),                 // Thẻ (ví dụ: thẻ tín dụng/ghi nợ)
    BANK_TRANSFER("Bank Transfer"), // Chuyển khoản ngân hàng
    POINTS("Points"),             // Thanh toán bằng điểm (nếu có)
    OTHER("Other");               // Phương thức khác

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}