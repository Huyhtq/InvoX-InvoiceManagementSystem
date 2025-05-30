package com.invox.invoice_system.enums;

public enum ProductStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    OOS("Out of Stock");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
