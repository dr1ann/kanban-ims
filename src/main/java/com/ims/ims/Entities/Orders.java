package com.ims.ims.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Orders {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)

    private Inventory product;
    private Integer quantity;
    private Double totalAmount;
    private String status;
   
    public Orders() {
        this.status = "Pending";
    }
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime orderDate;
    
    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public Inventory getProduct() {
        return product;
    }
    
    public void setProduct(Inventory product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    } 

    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
     // Formatted getters
     public String getFormattedTotalAmount() {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(totalAmount);
    }
    
    public String getFormattedQuantity() {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        return numberFormat.format(quantity);
    }

   public String getFormattedOrderDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' h:mm a");
        return orderDate.format(formatter);  // Formats the LocalDateTime to a String
    }

    public abstract void calculateTotalAmount();
}
