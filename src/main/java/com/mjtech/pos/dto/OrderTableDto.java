package com.mjtech.pos.dto;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderTableDto {
    private String code;
    private String productName;
    private int productId;
    private int quantity;
    private String price;
    private String total;
    private boolean existingOrder = false;
    private int orderId;
    private BooleanProperty selected;

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelectedProperty(BooleanProperty selected) {
        this.selected = selected;
    }
}
