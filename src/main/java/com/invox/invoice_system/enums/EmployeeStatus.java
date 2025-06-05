package com.invox.invoice_system.enums;

public enum EmployeeStatus {
    ACTIVE("Active"),   // Đang làm việc
    INACTIVE("Inactive"); // Đã nghỉ việc

    private final String displayName;

    EmployeeStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}