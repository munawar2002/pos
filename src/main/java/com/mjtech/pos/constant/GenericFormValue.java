package com.mjtech.pos.constant;

public enum GenericFormValue {
    PRODUCT_CATEGORY("Product Category");

    private String value;

    private GenericFormValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
