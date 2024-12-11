package com.ims.ims.Controllers;

import com.ims.ims.Entities.Inventory;
import com.ims.ims.Entities.ProductCategory;
import com.ims.ims.Entities.Supplier;
import com.ims.ims.Services.InventoryService;
import com.ims.ims.Services.ProductCategoryService;
import com.ims.ims.Services.SupplierService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller

public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private SupplierService supplierService;

    @GetMapping("/inventory/products")
    public String viewAllInventory(Model model) {
        List<Inventory> inventoryList = inventoryService.getAllInventoryItems();
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        // Get supplier products
        Set<String> supplierProducts = suppliers.stream()
            .flatMap(supplier -> supplier.getProductsProvided().stream())
            .collect(Collectors.toSet());

        // Filter inventory by supplier products
        List<Inventory> filteredInventoryList = inventoryList.stream()
            .filter(product -> supplierProducts.contains(product.getName()))
            .collect(Collectors.toList());
        
            model.addAttribute("filteredInventoryList", filteredInventoryList);
        model.addAttribute("products", inventoryList);
        model.addAttribute("currentPage", "/products");
        return "inventory/products-list";
    }

    @GetMapping("/inventory/data")
    @ResponseBody
    public List<Inventory> getInventoryData() {
        List<Inventory> inventoryList = inventoryService.getAllInventoryItems();
        System.out.println(inventoryList.size());
        return inventoryList;
    }

    
    @GetMapping("/inventory/add-product")
    public String showAddInventoryForm(Model model) {
        List<ProductCategory> productCategories = productCategoryService.getAllProductCategories();
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("categories", productCategories);
        model.addAttribute("currentPage", "/add-product");
        return "inventory/add-product";
    }

    @PostMapping("/inventory/add-product")
    public String addInventoryItem(@ModelAttribute Inventory inventory, @RequestParam(value = "category", required = false) Long categoryId, @RequestParam("name") String name, RedirectAttributes redirectAttributes) {
        if(inventoryService.doesProductExist(name)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding the product: Product already exists.");
        } else {
            inventoryService.addInventoryItem(inventory, categoryId);
            redirectAttributes.addFlashAttribute("message", "Product added sucessfully!");
        }
        return "redirect:/inventory/products";
    }

    @GetMapping("/inventory/edit-product/{id}")
    public String showEditInventoryForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Inventory item = inventoryService.getInventoryItemById(id);
            List<ProductCategory> productCategories = productCategoryService.getAllProductCategories();

            model.addAttribute("product", item);
            model.addAttribute("currentPage", "/products");
            model.addAttribute("categories", productCategories);
            return "inventory/edit-product";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/inventory/products";
        }
    }

    @PostMapping("/inventory/edit-product/{id}")
    public String editInventoryItem(@PathVariable("id") Long id, @ModelAttribute Inventory inventory, @RequestParam(value = "category", required = false) Long categoryId, @RequestParam("name") String name, RedirectAttributes redirectAttributes) {
        try {
            if(inventoryService.doesProductExistNotSelf(name, id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error editing the product: Product already exists.");
            } else {
                inventoryService.updateInventoryItem(id, inventory, categoryId);
                redirectAttributes.addFlashAttribute("message", "Product updated successfully!");
            }
            return "redirect:/inventory/products";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/inventory/products";
        }
    }

    @PostMapping("/inventory/delete-product/{id}")
    public String deleteInventoryItem(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            inventoryService.deleteInventoryItem(id);
            redirectAttributes.addFlashAttribute("message", "Product removed successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/inventory/products";
    }
}
