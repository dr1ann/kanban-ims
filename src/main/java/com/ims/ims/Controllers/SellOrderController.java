package com.ims.ims.Controllers;

import com.ims.ims.Entities.BuyOrder;
import com.ims.ims.Entities.Inventory;
import com.ims.ims.Entities.SellOrder;
import com.ims.ims.Services.InventoryService;
import com.ims.ims.Services.SellOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class SellOrderController {

    @Autowired
    private SellOrderService sellOrderService;

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/sell-order/list")
    public String viewAllSellOrders(Model model) {
        List<SellOrder> sellOrders = sellOrderService.getAllSellOrders();
        model.addAttribute("orders", sellOrders);
        model.addAttribute("currentPage", "/sell-order");
        return "sell-order/sell-order-list";
    }

    @GetMapping("/sell-order/data")
    @ResponseBody
    public List<SellOrder> exportSellOrders(Model model) {
        List<SellOrder> sellOrders = sellOrderService.getAllSellOrders();
        return sellOrders;
    }
    
    @GetMapping("/sell-order/add")
    public String showAddSellOrderForm(Model model) {
        List<Inventory> inventoryList = inventoryService.getAllInventoryItems();
        Set<String> statusesToFilter = Set.of("In Stock");
        List<Inventory> stockFilteredInventory = inventoryList.stream()
            .filter(product -> statusesToFilter.contains(product.getStockStatus()))
            .collect(Collectors.toList());
    
        model.addAttribute("products", stockFilteredInventory);
        model.addAttribute("currentPage", "/add-sell-order");
    
        return "/sell-order/add-sell-order";
    }
    
    @PostMapping("/sell-order/add")
    public String addSellOrder(@ModelAttribute SellOrder sellOrder, @RequestParam("quantity") Integer inputQuantity, RedirectAttributes redirectAttributes) {
        Inventory currentProductToUpdate = sellOrder.getProduct();
    
        if (currentProductToUpdate.getQuantity() < inputQuantity) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Order cancelled: The requested quantity (" + inputQuantity + ") exceeds the available stock (" + currentProductToUpdate.getQuantity() + ").");
            return "redirect:/sell-order/list";
        }
    
        sellOrderService.addNewSellOrder(sellOrder);
        redirectAttributes.addFlashAttribute("message", "Order placed successfully!");
        return "redirect:/sell-order/list";
    }
    

    @PostMapping("/sell-order/edit-status/{id}")
    public String editSellOrderStatus(@RequestParam("orderId") Long orderId, @RequestParam("formType") String formType, RedirectAttributes redirectAttributes) {
        SellOrder orderToUpdate = sellOrderService.getSellOrderById(orderId);
        Integer orderQuantity = orderToUpdate.getQuantity();
        Inventory currentProductToUpdate = orderToUpdate.getProduct();
        Integer currentProductQuantity = orderToUpdate.getProduct().getQuantity();
        if ("confirmOrder".equals(formType)) {
            Integer updatedQuantity = currentProductQuantity - orderQuantity;  
            currentProductToUpdate.setQuantity(updatedQuantity);
            inventoryService.updateInventoryQuantity(currentProductToUpdate.getId(), currentProductToUpdate); 
            
            orderToUpdate.setStatus("Confirmed");
            orderToUpdate.setConfirmDate(LocalDateTime.now());
            sellOrderService.updateSellOrder(orderId, orderToUpdate);
            redirectAttributes.addFlashAttribute("message", "Order ID " + orderId + " confirmed successfully! Product quantity updated."); 
        }
        
        return "redirect:/inventory/products";
    }
    @GetMapping("/sell-order/edit-order/{id}")
    public String showEditSellOrderForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            SellOrder order = sellOrderService.getSellOrderById(id);
            List<Inventory> inventoryList = inventoryService.getAllInventoryItems();
            Set<String> statusesToFilter = Set.of("In Stock");
            List<Inventory> stockFilteredInventory = inventoryList.stream()
                .filter(product -> statusesToFilter.contains(product.getStockStatus()))
                .collect(Collectors.toList());
                
            model.addAttribute("order", order);
            model.addAttribute("currentPage", "/sell-order");
            model.addAttribute("products", stockFilteredInventory);
            model.addAttribute("selectedProductId", order.getProduct().getId());
            model.addAttribute("selectedProductSellingPrice", order.getProduct().getSellingPrice());
            model.addAttribute("selectedProductFormattedSellingPrice", order.getProduct().getFormattedSellingPrice());
            model.addAttribute("selectedOrderTotalPrice", order.getFormattedTotalAmount());

            return "sell-order/edit-sell-order";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/sell-order/list";
        }
    }

    @PostMapping("/sell-order/edit-order/{id}")
    public String editSellOrder(@PathVariable("id") Long id, @ModelAttribute SellOrder sellOrder, RedirectAttributes redirectAttributes) {
        try {
            sellOrderService.updateSellOrder(id, sellOrder);
            redirectAttributes.addFlashAttribute("message", "Order updated successfully!");
            return "redirect:/sell-order/list";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/sell-order/list";
        }
    }
}
