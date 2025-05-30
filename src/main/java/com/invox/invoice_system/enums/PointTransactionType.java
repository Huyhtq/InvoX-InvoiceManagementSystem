package com.invox.invoice_system.enums;

public enum PointTransactionType {
    EARN("Earn"),         // Tích điểm
    REDEEM("Redeem"),     // Đổi điểm
    ADJUSTMENT("Adjustment"); // Điều chỉnh thủ công (ví dụ: thêm/bớt điểm)

    private final String displayName;

    PointTransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}