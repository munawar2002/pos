package com.mjtech.pos.constant;

public enum GenericFormValue {
    PRODUCT_CATEGORY("Product Category"),
    PRODUCT_COMPANY("Product Company"),
    SUPPLIER("Supplier");

    private String value;

    private GenericFormValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
