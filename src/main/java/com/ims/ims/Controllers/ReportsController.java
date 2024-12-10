package com.ims.ims.Controllers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.ims.ims.Entities.Inventory;
import com.ims.ims.Entities.SellOrder;
import com.ims.ims.Services.InventoryService;
import com.ims.ims.Services.SellOrderService;

import org.springframework.ui.Model;

@Controller
public class ReportsController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private SellOrderService sellOrderService;

    @GetMapping("/reports")
    public String reports(Model model) {
        List<Inventory> inventoryList = inventoryService.getAllInventoryItems();
        List<Inventory> lowStockQuantityProducts = new ArrayList<>();
        List<Inventory> noStockQuantityProducts = new ArrayList<>();
        List<SellOrder> productSales = sellOrderService.getAllSellOrders();
        List<SellOrder> topSellingProducts = productSales.stream()
        .filter(product -> "Confirmed".equals(product.getStatus())) 
        .sorted((p1, p2) -> Double.compare(p2.getTotalAmount(), p1.getTotalAmount())) 
        .collect(Collectors.toList());

        Double totalInventoryValue = 0.0;
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

        for (Inventory product : inventoryList) {
            if ("Low Stock".equals(product.getStockStatus())) {
                lowStockQuantityProducts.add(product);
            }
            if ("Out of Stock".equals(product.getStockStatus())) {
                noStockQuantityProducts.add(product);
            }
            Double productValue = product.getQuantity() * product.getSellingPrice();
            totalInventoryValue += productValue;
            
        }

        model.addAttribute("totalInventoryValue",  decimalFormat.format(totalInventoryValue));
        model.addAttribute("totalNumberOfLowStockProducts", lowStockQuantityProducts.size());
        model.addAttribute("totalNumberOfNoStockProducts", noStockQuantityProducts.size());
        model.addAttribute("totalNumberOfProducts", inventoryList.size());
        model.addAttribute("topSellingProducts", topSellingProducts);
        model.addAttribute("currentPage", "/reports");
        return "reports"; 
    }
}
