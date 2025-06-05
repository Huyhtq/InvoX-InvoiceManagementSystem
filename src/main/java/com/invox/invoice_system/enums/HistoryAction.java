package com.invox.invoice_system.enums;

public enum HistoryAction {
    CREATE("Create"),
    UPDATE("Update"),
    DELETE("Delete"),
    LOGIN("Login"),
    LOGOUT("Logout"),
    VIEW("View"), // Xem một bản ghi
    STATUS_CHANGE("Status Change"), // Thay đổi trạng thái (VD: hóa đơn)
    PASSWORD_CHANGE("Password Change"), // Thay đổi mật khẩu
    GRANT_PERMISSION("Grant Permission"), // Cấp quyền
    REVOKE_PERMISSION("Revoke Permission"); // Thu hồi quyền

    private final String displayName;

    HistoryAction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}