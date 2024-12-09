package com.ims.ims.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.text.*;

@Entity
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private Double buyingPrice;
    private Double sellingPrice;
    private Integer quantity;
    private String unit;
    private Integer threshold;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true) 
    @JoinColumn(name = "category_id", nullable = true) 
    private ProductCategory category;
    
    
    public Inventory() {
        quantity = 0;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Double getBuyingPrice() {
        return buyingPrice;
    }
    
    public void setBuyingPrice(Double buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }
    
    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public Integer getThreshold() {
        return threshold;
    }
    
    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }
    

    public ProductCategory getCategory() {
        return category;
    }
    
    public void setCategory(ProductCategory category) {
        this.category = category;
    }
    
     // Formatted getters
     public String getFormattedBuyingPrice() {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(buyingPrice);
    }

    public String getFormattedSellingPrice() {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(sellingPrice);
    }

    public String getFormattedQuantity() {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        return numberFormat.format(quantity);
    }

    public String getFormattedThreshold() {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        return numberFormat.format(threshold);
    }

      public String getStockStatus() {
        if (quantity == 0) {
            return "Out of Stock";
        } else if (quantity < threshold) {
            return "Low Stock";
        } else {
            return "In Stock";
        }
    }

    public boolean needsReorder() {
        return "Low Stock".equals(getStockStatus()) || "Out of Stock".equals(getStockStatus());
    }
    // @JsonProperty("exportBuyingPrice")
    // public String getExportBuyingPrice() {
    //     return exportBuyingPrice;
    // }
    
    // public void setExportBuyingPrice(String exportBuyingPrice) {
    //     this.exportBuyingPrice = exportBuyingPrice;
    // }

    // @JsonProperty("exportSellingPrice")
    // public String getExportSellingPrice() {
    //     return exportSellingPrice;
    // }
    
    // public void setExportSellingPrice(String exportSellingPrice) {
    //     this.exportSellingPrice = exportSellingPrice;
    // }

   
}
