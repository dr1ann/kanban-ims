package com.ims.ims.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.ims.ims.Entities.Inventory;
import com.ims.ims.Entities.Supplier;
import com.ims.ims.Services.InventoryService;
import com.ims.ims.Services.SupplierService;
import org.springframework.ui.Model;

@Controller
public class SuppliersController {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/suppliers/list")
    public String suppliers(Model model) {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("currentPage", "/suppliers");
        return "suppliers/suppliers-list";
    }

    @GetMapping("/suppliers/add")
    public String showAddSupplierForm(Model model) {
        List<Inventory> inventoryList = inventoryService.getAllInventoryItems();
        model.addAttribute("products", inventoryList);
        model.addAttribute("currentPage", "/add-supplier");
        return "suppliers/add-supplier";
    }

    @PostMapping("/suppliers/add-supplier")
    public String addSupplier(@ModelAttribute Supplier supplier, @RequestParam("name") String name, RedirectAttributes redirectAttributes) {
        if (supplierService.doesSupplierExist(name)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding the supplier: Supplier already exists.");
        } else {
            supplierService.addSupplier(supplier);
            redirectAttributes.addFlashAttribute("message", "Supplier added sucessfully!");
        }
        return "redirect:/suppliers/list";
    }

    @GetMapping("/suppliers/edit-supplier/{id}")
    public String showEditSupplierForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Supplier supplier = supplierService.getSupplierById(id);
            List<Inventory> availableProducts = inventoryService.getAllInventoryItems();
            model.addAttribute("availableProducts", availableProducts);
            model.addAttribute("supplier", supplier);
            model.addAttribute("currentPage", "/suppliers");
            return "suppliers/edit-supplier";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/suppliers/list";
        }
    }

    @PostMapping("/suppliers/edit-supplier/{id}")
    public String editSupplierForm(@PathVariable("id") Long id, @ModelAttribute Supplier supplier, @RequestParam("name") String name, RedirectAttributes redirectAttributes) {
        try {
            if (supplierService.doesSupplierExistNotSelf(name, id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error editing the supplier: Supplier already exists.");
            } else {
                supplierService.updateSupplier(id, supplier);
                redirectAttributes.addFlashAttribute("message", "Supplier updated successfully!");
            }
            return "redirect:/suppliers/list";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/suppliers/list";
        }
    }

    @PostMapping("/suppliers/delete-supplier/{id}")
    public String deleteSupplier(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            supplierService.deleteSupplier(id);
            redirectAttributes.addFlashAttribute("message", "Supplier removed successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/suppliers/list";
    }
}
