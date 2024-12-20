package com.ims.ims.Controllers;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.ims.ims.Entities.BuyOrder;
import com.ims.ims.Entities.Inventory;
import com.ims.ims.Entities.Supplier;
import com.ims.ims.Services.BuyOrderService;
import com.ims.ims.Services.InventoryService;

import com.ims.ims.Services.SupplierService;

import org.springframework.ui.Model;

@Controller
public class DashboardController {
    @Autowired
    private BuyOrderService buyOrderService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private SupplierService supplierService;



    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<BuyOrder> buyOrders = buyOrderService.getAllBuyOrders();
        List<BuyOrder> pendingBuyOrders = new ArrayList<>();
        List<Inventory> lowQuantityProducts = new ArrayList<>();
        List<Inventory> inventoryList = inventoryService.getAllInventoryItems();
        List<Inventory> filteredInventoryList = inventoryList.stream().filter(product -> product.getQuantity() != 0)
        .collect(Collectors.toList());  
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        Map<Long, Integer> productOrderQuantities = new HashMap<>();

    
        
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        int count = 0;
        Integer totalQuantity = 0;
        Integer suppliersCount = suppliers.size();
        Integer purchaseCount = buyOrders.size();
        Double totalPurchaseCost = 0.0;
        for (BuyOrder order : buyOrders) {
            for (Inventory product : inventoryList) {   
                int productTotalOrderQuantity = productOrderQuantities.getOrDefault(product.getId(), 0);

                if (order.getProduct().getId().equals(product.getId())) {
                    if ("Pending".equals(order.getStatus())) {
                        pendingBuyOrders.add(order);
                        productTotalOrderQuantity += order.getQuantity();
                    }
                    if (order.getBackOrderQuantity() != 0) {
                        productTotalOrderQuantity += order.getBackOrderQuantity();
                    }
                    if (order.getBadOrderQuantity() != 0) {
                        productTotalOrderQuantity += order.getBadOrderQuantity();
                    }
                }
        
                // Update the map with the correct total for this product
                productOrderQuantities.put(product.getId(), productTotalOrderQuantity);
        
                // Accumulate the total purchase cost
                totalPurchaseCost += order.getTotalAmount();
            }

        }
          

        for (Inventory product : inventoryList) {
            totalQuantity += product.getQuantity();

            if ("Low Stock".equals(product.getStockStatus())) {
                lowQuantityProducts.add(product);
                count++;

                if (count == 4) {
                    break;
                }
            }
        }

        model.addAttribute("totalPurchaseCost", decimalFormat.format(totalPurchaseCost));
        model.addAttribute("purchaseCount", numberFormat.format(purchaseCount));
        model.addAttribute("totalSuppliers", numberFormat.format(suppliersCount));
        model.addAttribute("totalQuantity", numberFormat.format(totalQuantity));
        model.addAttribute("productOrderQuantities", productOrderQuantities);
        model.addAttribute("products", filteredInventoryList);
        model.addAttribute("lowQuantityProducts", lowQuantityProducts);
        model.addAttribute("orders", pendingBuyOrders);
        model.addAttribute("currentPage", "/dashboard");
        return "dashboard";
    }
}
