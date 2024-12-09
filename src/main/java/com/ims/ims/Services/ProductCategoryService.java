package com.ims.ims.Services;


import com.ims.ims.Entities.Inventory;
import com.ims.ims.Entities.ProductCategory;
import com.ims.ims.Repositories.InventoryRepository;
import com.ims.ims.Repositories.ProductCategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductCategoryService {
    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private InventoryRepository inventoryRepository;
    
    public void addProductCategory(ProductCategory productCategory) {
        productCategoryRepository.save(productCategory);
    }

    public List<ProductCategory> getAllProductCategories() {
        return productCategoryRepository.findAll();
    }

    public ProductCategory getProductCategoryById(Long id) {
        return productCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product Category with ID " + id + " not found"));
    }

    public ProductCategory updateProductCategory(Long id, ProductCategory updatedProductCategory) {
        ProductCategory existingProductCategory = getProductCategoryById(id);
    
        existingProductCategory.setName(updatedProductCategory.getName());
        existingProductCategory.setDescription(updatedProductCategory.getDescription());
    
        return productCategoryRepository.save(existingProductCategory);
    }   

    @Transactional
    public void deleteProductCategory(Long id) {
        ProductCategory productCategory = getProductCategoryById(id);
        List<Inventory> products = inventoryRepository.findAll();

        for (Inventory product : products) {
            if (product.getCategory() != null && productCategory.getId().equals(product.getCategory().getId())) {
                product.setCategory(null);
                inventoryRepository.save(product);
            }
        }

        productCategoryRepository.delete(productCategory);
    }   
}
