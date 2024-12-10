package com.ims.ims.Controllers;

import com.ims.ims.Entities.ProductCategory;
import com.ims.ims.Repositories.InventoryRepository;
import com.ims.ims.Services.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;


    @Autowired
    private InventoryRepository inventoryRepository;

    @GetMapping("/inventory/categories")
    public String viewAllCategories(Model model) {
       List<ProductCategory> productCategories = productCategoryService.getAllProductCategories();
       Map<Long, Boolean> categoryProductStatus = new HashMap<>();

        for (ProductCategory category : productCategories) {
            boolean hasProducts = inventoryRepository.existsByCategory(category);
            categoryProductStatus.put(category.getId(), hasProducts);
        }
        model.addAttribute("categoryProductStatus", categoryProductStatus);
        model.addAttribute("categories", productCategories);

        model.addAttribute("currentPage", "/categories");
        return "inventory/product-categories-list";
    }

    @GetMapping("/inventory/add-product-category")
    public String showAddProductCategoryForm(Model model) {
        model.addAttribute("currentPage", "/add-cat");
        return "inventory/add-product-category";
    }

    @PostMapping("/inventory/add-product-category")
    public String addInventoryItem(@ModelAttribute ProductCategory productCategory,  @RequestParam("name") String name, RedirectAttributes redirectAttributes) {
        if (productCategoryService.doesProductCategoryExist(name)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding the product category: Product Category already exists.");
        } else {
            productCategoryService.addProductCategory(productCategory);
            redirectAttributes.addFlashAttribute("message", "Product Category added sucessfully!");
        }

        return "redirect:/inventory/categories";
    }

    @GetMapping("/inventory/edit-product-category/{id}")
    public String showEditProductCategoryForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ProductCategory category = productCategoryService.getProductCategoryById(id);
            model.addAttribute("category", category);
            model.addAttribute("currentPage", "/categories");
            return "inventory/edit-product-category";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/inventory/categories";
        }
    }

    @PostMapping("/inventory/edit-product-category/{id}")
    public String editProductCategory(@PathVariable("id") Long id, @ModelAttribute ProductCategory productCategory,  @RequestParam("name") String name, RedirectAttributes redirectAttributes) {
        try {
            if (productCategoryService.doesProductCategoryExistNotSelf(name, id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error editing the product category: Product Category already exists.");
            } else {
                productCategoryService.updateProductCategory(id, productCategory);
                redirectAttributes.addFlashAttribute("message", "Product Category updated successfully!");
            }
            return "redirect:/inventory/categories";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/inventory/categories";
        }
    }

    @PostMapping("/inventory/delete-product-category/{id}")
    public String deleteInventoryItem(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            productCategoryService.deleteProductCategory(id);
            redirectAttributes.addFlashAttribute("message", "Product Category removed successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/inventory/categories";
    }
}
