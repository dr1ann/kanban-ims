package com.ims.ims.Entities;

import jakarta.persistence.Entity;

import java.text.DecimalFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class SellOrder extends Orders {
    private Double amountPayed;
    private Double change;

    @Override
    public void calculateTotalAmount() {
        Double price = getProduct().getSellingPrice();
        setTotalAmount(getQuantity() * price);
        calculateChange(); // Recalculate change when total amount is updated
    }

    public Double getAmountPayed() {
        return amountPayed;
    }

    public void setAmountPayed(Double amountPayed) {
        this.amountPayed = amountPayed;
        calculateChange(); // Recalculate change when amountPayed is updated
    }

    public Double getChange() {
        return change;
    }

    private void calculateChange() {
        if (amountPayed != null && getTotalAmount() != null) {
            this.change = amountPayed - getTotalAmount();
            if (this.change < 0) {
                this.change = 0.0; // Ensure change is not negative
            }
        } else {
            this.change = 0.0;
        }
    }

    public void setChange(Double change) {
        this.change = change;
    }

    // Formatted getters
    public String getFormattedAmountPayed() {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(amountPayed);
    }

    public String getFormattedChange() {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(change);
    }
}
