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
import java.util.List;
import java.util.Random;
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

    @GetMapping("/buy-order/receive")
    public String viewAllBuyOrdersToReceive(Model model) {
        List<BuyOrder> buyOrders = buyOrderService.getAllBuyOrders();
        model.addAttribute("orders", buyOrders);
        model.addAttribute("currentPage", "/receive-order");
        return "buy-order/receive-order-list";
    }

    @GetMapping("/buy-order/data")
    @ResponseBody
    public List<BuyOrder> exportBuyOrders(Model model) {
        List<BuyOrder> buyOrders = buyOrderService.getAllBuyOrders();
        return buyOrders;
    }

    @PostMapping("/buy-order/edit-status/{id}")
    public String editBuyOrderStatus(@RequestParam("orderId") Long orderId, @RequestParam("receivedQuantity") String receivedQuantity, @RequestParam(name= "backOrderQuantity", required = false) String backOrderQuantity,  @RequestParam(name= "badOrderQuantity", required = false) String badOrderQuantity, Model model, RedirectAttributes redirectAttributes) {
        BuyOrder orderToUpdate = buyOrderService.getBuyOrderById(orderId);
        Integer orderQuantity = orderToUpdate.getQuantity();
        Inventory currentProductToUpdate = orderToUpdate.getProduct();
        Integer currentProductQuantity = orderToUpdate.getProduct().getQuantity();
        Integer totalQuantitiesInput = 0;
        Integer receivedQuantityInt = Integer.parseInt(receivedQuantity);
        totalQuantitiesInput += receivedQuantityInt;
        
        StringBuilder statusBuilder = new StringBuilder("Received");
        if (orderToUpdate.getBackOrderQuantity() != 0 || orderToUpdate.getBadOrderQuantity() != 0 ) {
            orderQuantity = orderToUpdate.getBackOrderQuantity() + orderToUpdate.getBadOrderQuantity();
        }

        if (backOrderQuantity != null && !backOrderQuantity.trim().isEmpty()) {
            Integer backOrderQuantityInt = Integer.parseInt(backOrderQuantity);
            totalQuantitiesInput += backOrderQuantityInt;
            orderToUpdate.setBackOrderQuantity(backOrderQuantityInt);
            statusBuilder.append(", ").append(backOrderQuantityInt).append(" Back Order(s)");
        }
    
        if (badOrderQuantity != null && !badOrderQuantity.trim().isEmpty()) {
            Integer badOrderQuantityInt = Integer.parseInt(badOrderQuantity);
            totalQuantitiesInput += badOrderQuantityInt;
            orderToUpdate.setBadOrderQuantity(badOrderQuantityInt);
            statusBuilder.append(", ").append(badOrderQuantityInt).append(" Bad Order(s)");
        }

        if (totalQuantitiesInput.equals(orderQuantity)) {
            orderToUpdate.setStatus(statusBuilder.toString());
            receivedQuantityInt += currentProductQuantity;
            currentProductToUpdate.setQuantity(receivedQuantityInt);
            inventoryService.updateInventoryQuantity(currentProductToUpdate.getId(), currentProductToUpdate);
          
        
            StringBuilder messageBuilder = new StringBuilder("Order ID " + orderId + " has been updated. ");
            messageBuilder.append(totalQuantitiesInput).append(" item(s) processed, " + receivedQuantityInt + " received");

            if (backOrderQuantity != null && !backOrderQuantity.trim().isEmpty()) {
                messageBuilder.append(", including ").append(backOrderQuantity).append(" Back Order(s)");
            }

            if (badOrderQuantity != null && !badOrderQuantity.trim().isEmpty()) {
                messageBuilder.append(", including ").append(badOrderQuantity).append(" Bad Order(s)");
            }
            orderToUpdate.setBackOrderQuantity(0);
            orderToUpdate.setBadOrderQuantity(0);
            buyOrderService.updateBuyOrder(orderId, orderToUpdate);

            // Add flash attribute with the confirmation message
            redirectAttributes.addFlashAttribute("message", messageBuilder.toString());

            return "redirect:/inventory/products";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: The total quantities (received, back orders, and bad orders) do not match the order quantity.");
            return "redirect:/buy-order/receive";
        }
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

        // Constants for EOQ calculation
        double orderCost = 100.0;  // Example order cost per purchase order (can be dynamic)
        double holdingCost = 5.0;  // Example holding cost per unit per year (can be dynamic)

        // Randomize annual demand and calculate EOQ for each product, then automate quantity to order
        Random random = new Random();
        finalFilteredInventoryList.forEach(product -> {
            int annualDemand = random.nextInt(1000) + 100;  // Random demand between 100 and 1000 units
            
            // Calculate EOQ using the formula
            double eoq = Math.sqrt((2 * annualDemand * orderCost) / holdingCost);

            // Automate the quantity to order based on EOQ
            product.setOrderQuantity((int) eoq);  // Setting the order quantity as EOQ (rounding to nearest int)

            // Optionally, you can store the EOQ value if needed
            product.setEoq(eoq);  // Assuming you have a setEoq method in your Inventory class
        });

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

    @PostMapping("/buy-order/cancel-order/{id}")
    public String cancelBuyOrder(@PathVariable("id") Long id, @RequestParam("cancelOrderReason") String cancelOrderReason, RedirectAttributes redirectAttributes) {
        try {
            buyOrderService.cancelBuyOrderTransaction(id, cancelOrderReason);
            redirectAttributes.addFlashAttribute("message", "Order cancelled successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/buy-order/list";
    }
    
    @PostMapping("/buy-order/delete-order/{id}")
    public String deleteBuyOrder(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            buyOrderService.deleteBuyOrderTransaction(id);
            redirectAttributes.addFlashAttribute("message", "Transaction removed successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/buy-order/list";
    }
    
}
