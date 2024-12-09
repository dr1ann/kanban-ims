package com.ims.ims.Controllers;

import com.ims.ims.Entities.BuyOrder;
import com.ims.ims.Entities.Inventory;
import com.ims.ims.Entities.Supplier;
import com.ims.ims.Services.BuyOrderService;
import com.ims.ims.Services.InventoryService;
import com.ims.ims.Services.SupplierService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
@Controller
public class BuyOrderController {

    @Autowired
    private BuyOrderService buyOrderService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/buy-order/list")
    public String viewAllBuyOrders(Model model) {
        List<BuyOrder> buyOrders = buyOrderService.getAllBuyOrders();
        model.addAttribute("orders", buyOrders);
        model.addAttribute("currentPage", "/buy-order");
        return "buy-order/buy-order-list";
    }

    @PostMapping("/buy-order/edit-status/{id}")
    public String editBuyOrderStatus(@RequestParam("orderId") Long orderId, @RequestParam("formType") String formType, @RequestParam(value = "backOrderQuantity", required = false) Integer backOrderQuantity,  @RequestParam(value = "badOrderQuantity", required = false) Integer badOrderQuantity, RedirectAttributes redirectAttributes) {
        BuyOrder orderToUpdate = buyOrderService.getBuyOrderById(orderId);
        Integer orderQuantity = orderToUpdate.getQuantity();
        Inventory currentProductToUpdate = orderToUpdate.getProduct();
        Integer currentProductQuantity = orderToUpdate.getProduct().getQuantity();
        if ("receiveOrder".equals(formType)) {
            if(orderToUpdate.getBackOrderQuantity() != 0) {
                currentProductToUpdate.setQuantity(currentProductQuantity + orderToUpdate.getBackOrderQuantity());
            } else if(orderToUpdate.getBadOrderQuantity() != 0) {
                currentProductToUpdate.setQuantity(currentProductToUpdate.getQuantity() + orderToUpdate.getBadOrderQuantity());
            } else {
                currentProductToUpdate.setQuantity(currentProductQuantity + orderQuantity);
            }
            inventoryService.updateInventoryQuantity(currentProductToUpdate.getId(), currentProductToUpdate); 
            
            orderToUpdate.setBackOrderQuantity(0);
            orderToUpdate.setBadOrderQuantity(0);
            orderToUpdate.setStatus("Received");
            orderToUpdate.setReceiveDate(LocalDateTime.now());
            buyOrderService.updateBuyOrder(orderId, orderToUpdate);
            redirectAttributes.addFlashAttribute("message", "Order ID " + orderId + " received successfully! Product quantity updated."); 
        }

        if ("backOrder".equals(formType)) {
            if (backOrderQuantity > orderQuantity) {
                redirectAttributes.addFlashAttribute("errorMessage", "Order ID " +  orderId + " Error: Backorder quantity cannot exceed the total order quantity.");
                return "redirect:/buy-order/list"; 
            }

            Integer updatedQuantity = currentProductQuantity + (orderQuantity - backOrderQuantity);  
            currentProductToUpdate.setQuantity(updatedQuantity);
            inventoryService.updateInventoryQuantity(currentProductToUpdate.getId(), currentProductToUpdate); 
            
            orderToUpdate.setStatus(backOrderQuantity + " Back Order(s)");
            orderToUpdate.setBackOrderQuantity(backOrderQuantity);
            buyOrderService.updateBuyOrder(orderId, orderToUpdate);
            redirectAttributes.addFlashAttribute("message", "Order ID " + orderId + " has been updated. " + (orderQuantity - backOrderQuantity) + " item(s) delivered, and " + backOrderQuantity + " item(s) are pending delivery. Product quantity updated.");
        }

        if ("badOrder".equals(formType)) {
            if (badOrderQuantity > orderQuantity) {
                redirectAttributes.addFlashAttribute("errorMessage", "Order ID " + orderId + " Error: Bad order quantity cannot exceed the total order quantity.");
                return "redirect:/buy-order/list";
            }
        
            // Update the quantity only with successfully delivered items
            Integer updatedQuantity = currentProductQuantity + (orderQuantity - badOrderQuantity);  
            currentProductToUpdate.setQuantity(updatedQuantity);
            inventoryService.updateInventoryQuantity(currentProductToUpdate.getId(), currentProductToUpdate); 
            
            // Set the status to reflect the bad items separately
            orderToUpdate.setStatus(badOrderQuantity + " Bad Order(s)");
            orderToUpdate.setBadOrderQuantity(badOrderQuantity);
            buyOrderService.updateBuyOrder(orderId, orderToUpdate);
        
            redirectAttributes.addFlashAttribute("message", "Order ID " + orderId + " has been updated. " 
                                                  + (orderQuantity - badOrderQuantity) + " item(s) delivered successfully. "
                                                  + badOrderQuantity + " bad item(s) will be replaced. Product quantity updated.");
        }
        
        
        
        return "redirect:/inventory/products";
    }


    @GetMapping("/buy-order/add")
    public String showAddBuyOrderForm(Model model) {
        List<Inventory> inventoryList = inventoryService.getAllInventoryItems();
        List<Supplier> suppliers = supplierService.getAllSuppliers();

        // Filter by stock status
        Set<String> statusesToFilter = Set.of("Out of Stock", "Low Stock");
        List<Inventory> stockFilteredInventory = inventoryList.stream()
            .filter(product -> statusesToFilter.contains(product.getStockStatus()))
            .collect(Collectors.toList());
            
        // Get supplier products
        Set<String> supplierProducts = suppliers.stream()
            .flatMap(supplier -> supplier.getProductsProvided().stream())
            .collect(Collectors.toSet());

        // Filter inventory by supplier products
        List<Inventory> finalFilteredInventoryList = stockFilteredInventory.stream()
            .filter(product -> supplierProducts.contains(product.getName()))
            .collect(Collectors.toList());
    
        model.addAttribute("products", finalFilteredInventoryList);
        model.addAttribute("currentPage", "/add-buy-order");
    
        return "/buy-order/add-buy-order";
    }
    



    @GetMapping("/suppliers/products")
    public ResponseEntity<?> getSuppliersByProduct(@RequestParam(value = "product", required = false) Long productId) {
        List<Supplier> suppliers;
    
        if (productId != null) {
            Inventory selectedProduct = inventoryService.getInventoryItemById(productId);
            suppliers = supplierService.findByProductsProvidedContains(selectedProduct.getName());
        } else {
            suppliers = supplierService.getAllSuppliers();
        }
        return ResponseEntity.ok(suppliers);
    }
    
    @PostMapping("/buy-order/add")
    public String addBuyOrder(@ModelAttribute BuyOrder buyOrder, RedirectAttributes redirectAttributes) {
        buyOrderService.addNewBuyOrder(buyOrder);
        redirectAttributes.addFlashAttribute("message", "Order placed sucessfully!");
        return "redirect:/buy-order/list";
    }

    @GetMapping("/buy-order/edit-order/{id}")
    public String showEditBuyOrderForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            BuyOrder order = buyOrderService.getBuyOrderById(id);
            List<Supplier> suppliers = supplierService.getAllSuppliers();
            List<Supplier> filteredSuppliers;
            List<Inventory> inventoryList = inventoryService.getAllInventoryItems();

            // Filter by stock status
            Set<String> statusesToFilter = Set.of("Out of Stock", "Low Stock");
            List<Inventory> stockFilteredInventory = inventoryList.stream()
                .filter(product -> statusesToFilter.contains(product.getStockStatus()))
                .collect(Collectors.toList());
                stockFilteredInventory.forEach(product -> System.out.println(product.getName()));
            // Get supplier products
            Set<String> supplierProducts = suppliers.stream()
                .flatMap(supplier -> supplier.getProductsProvided().stream())
                .collect(Collectors.toSet());

            // Filter inventory by supplier products
            List<Inventory> finalFilteredInventoryList = stockFilteredInventory.stream()
                .filter(product -> supplierProducts.contains(product.getName()))
                .collect(Collectors.toList());

            Inventory selectedProduct = inventoryService.getInventoryItemById(order.getProduct().getId());
            filteredSuppliers = supplierService.findByProductsProvidedContains(selectedProduct.getName());
                
            model.addAttribute("order", order);
            model.addAttribute("currentPage", "/buy-order");
            model.addAttribute("products", finalFilteredInventoryList);
            model.addAttribute("selectedProductId", order.getProduct().getId());
            model.addAttribute("selectedProductBuyingPrice", order.getProduct().getBuyingPrice());
            model.addAttribute("selectedProductFormattedBuyingPrice", order.getProduct().getFormattedBuyingPrice());
            model.addAttribute("selectedOrderTotalPrice", order.getFormattedTotalAmount());
            model.addAttribute("selectedSupplierId", order.getSupplier().getId());
            model.addAttribute("selectedStatus", order.getStatus());
            model.addAttribute("suppliers", filteredSuppliers);

            return "buy-order/edit-buy-order";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/buy-order/list";
        }
    }

    @PostMapping("/buy-order/edit-order/{id}")
    public String editBuyOrder(@PathVariable("id") Long id, @ModelAttribute BuyOrder buyOrder, RedirectAttributes redirectAttributes) {
        try {
            buyOrderService.updateBuyOrder(id, buyOrder);
            redirectAttributes.addFlashAttribute("message", "Order updated successfully!");
            return "redirect:/buy-order/list";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/buy-order/list";
        }
    }

    @GetMapping("/buy-order/reorder")
    public String reorderBuyOrder(@RequestParam Long productId, Model model) {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        List<Supplier> filteredSuppliers;
        List<Inventory> inventoryList = inventoryService.getAllInventoryItems();
        BuyOrder latestOrder = buyOrderService.getLatestOrderByProduct(productId);
        Inventory selectedProduct = inventoryService.getInventoryItemById(productId);

        // Filter by stock status
        Set<String> statusesToFilter = Set.of("Out of Stock", "Low Stock");
        List<Inventory> stockFilteredInventory = inventoryList.stream()
            .filter(product -> statusesToFilter.contains(product.getStockStatus()))
            .collect(Collectors.toList());
            stockFilteredInventory.forEach(product -> System.out.println(product.getName()));
        // Get supplier products
        Set<String> supplierProducts = suppliers.stream()
            .flatMap(supplier -> supplier.getProductsProvided().stream())
            .collect(Collectors.toSet());

        // Filter inventory by supplier products
        List<Inventory> finalFilteredInventoryList = stockFilteredInventory.stream()
            .filter(product -> supplierProducts.contains(product.getName()))
            .collect(Collectors.toList());

        filteredSuppliers = supplierService.findByProductsProvidedContains(selectedProduct.getName());
       
        BuyOrder newOrder = new BuyOrder();
        Long selectedSupplierId;
        if (latestOrder == null) {
            newOrder.setSupplier(null);
            newOrder.setQuantity(0);
            selectedSupplierId = 0L;
        } else {
            newOrder.setSupplier(latestOrder.getSupplier());
            newOrder.setQuantity(latestOrder.getQuantity());
            selectedSupplierId = newOrder.getSupplier().getId();
        }
        newOrder.setProduct(selectedProduct);
        newOrder.calculateTotalAmount();

        model.addAttribute("reorder", newOrder);
        model.addAttribute("currentPage", "/add-buy-order");
        model.addAttribute("products", finalFilteredInventoryList);
        model.addAttribute("selectedProductId", newOrder.getProduct().getId());
        model.addAttribute("selectedProductBuyingPrice", newOrder.getProduct().getBuyingPrice());
        model.addAttribute("selectedProductFormattedBuyingPrice", newOrder.getProduct().getFormattedBuyingPrice());
        model.addAttribute("selectedOrderTotalPrice", newOrder.getFormattedTotalAmount());
        model.addAttribute("selectedSupplierId", selectedSupplierId);
        model.addAttribute("suppliers", filteredSuppliers);

        return "buy-order/reorder-buy-order"; 
    }

}
