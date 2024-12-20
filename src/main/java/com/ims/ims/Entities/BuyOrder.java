package com.ims.ims.Entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class BuyOrder extends Orders {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)

    private Supplier supplier;
    private Integer backOrderQuantity;
    private Integer badOrderQuantity;
    private LocalDateTime receiveDate;
    private String cancelOrderReason;
    
    public BuyOrder() {
        this.backOrderQuantity = 0;
        this.badOrderQuantity = 0;
    }
    
    public Integer getBackOrderQuantity() {
        return backOrderQuantity;
    }

    public void setBackOrderQuantity(Integer backOrderQuantity) {
        this.backOrderQuantity = backOrderQuantity;
    }

    public Integer getBadOrderQuantity() {
        return badOrderQuantity;
    }

    public void setBadOrderQuantity(Integer badOrderQuantity) {
        this.badOrderQuantity = badOrderQuantity;
    }

    public String getCancelOrderReason() {
        return cancelOrderReason;
    }

    public void setCancelOrderReason(String cancelOrderReason) {
        this.cancelOrderReason = cancelOrderReason;
    }

    public LocalDateTime getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(LocalDateTime receiveDate) {
        this.receiveDate = receiveDate;
    }

    @Override
    public void calculateTotalAmount() {
        Double price = getProduct().getBuyingPrice();
        setTotalAmount(getQuantity() * price);
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    //formated getter(s)
    public String getFormattedReceiveDate() {
        if (receiveDate == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' h:mm a");
        return receiveDate.format(formatter); // Formats the LocalDateTime to a String
    }
    
}
