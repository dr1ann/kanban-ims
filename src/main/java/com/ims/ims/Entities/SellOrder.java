package com.ims.ims.Entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Entity;


@Entity
public class SellOrder extends Orders {
   private String customerName;
   private String customerContact;
   private LocalDateTime confirmDate;

   @Override
    public void calculateTotalAmount() {
        Double price = getProduct().getSellingPrice();
        setTotalAmount(getQuantity() * price);
    }

   public String getCustomerName() {
        return customerName;
   }

   public void setCustomerName(String customerName) {
    this.customerName = customerName;
    }

    public String getCustomerContact() {
        return customerContact;
   }

   public void setCustomerContact(String customerContact) {
    this.customerContact = customerContact;
    }

    public LocalDateTime getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(LocalDateTime confirmDate) {
        this.confirmDate = confirmDate;
    }

    //formated getter(s)
    public String getFormattedConfirmDate() {
        if (confirmDate == null) {
            return ""; 
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' h:mm a");
        return confirmDate.format(formatter); // Formats the LocalDateTime to a String
    }
}
