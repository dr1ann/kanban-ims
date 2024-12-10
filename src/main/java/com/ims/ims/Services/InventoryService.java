package com.ims.ims.Services;

import com.ims.ims.Entities.Inventory;
import com.ims.ims.Entities.ProductCategory;
import com.ims.ims.Entities.Supplier;
import com.ims.ims.Repositories.InventoryRepository;
import com.ims.ims.Repositories.ProductCategoryRepository;
import com.ims.ims.Repositories.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InventoryService {
    
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    public void addInventoryItem(Inventory inventory, Long categoryId) {
        if (categoryId != null) {
            ProductCategory category = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + categoryId));
            inventory.setCategory(category);
        } else {
            inventory.setCategory(null);
        }
        inventoryRepository.save(inventory); 
    }
    
    public boolean doesProductExist(String productName) {
        return inventoryRepository.existsByNameIgnoreCase(productName);
    }

    public boolean doesProductExistNotSelf(String productName, Long excludeId) {
        return inventoryRepository.existsByNameIgnoreCaseAndIdNot(productName, excludeId);
    }
        
    public List<Inventory> getAllInventoryItems() {
        return inventoryRepository.findAll();
    }

    public Inventory getInventoryItemById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item with ID " + id + " not found"));
    }

    public Supplier getSupplierbyId(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier with ID " + id + " not found"));
    }

    public Inventory updateInventoryItem(Long id, Inventory updatedInventory, Long categoryId) {
        Inventory existingInventory = getInventoryItemById(id);
        if (categoryId != null) {
            ProductCategory category = productCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
        
            existingInventory.setCategory(category);
        } else {
            existingInventory.setCategory(null);
        }

        existingInventory.setName(updatedInventory.getName());
        existingInventory.setUnit(updatedInventory.getUnit());
        existingInventory.setBuyingPrice(updatedInventory.getBuyingPrice());
        existingInventory.setThreshold(updatedInventory.getThreshold());
        
        return inventoryRepository.save(existingInventory);
    }    

    public Inventory updateInventoryQuantity(Long id, Inventory updatedInventory) {
        Inventory existingInventory = getInventoryItemById(id);
        existingInventory.setQuantity(updatedInventory.getQuantity());
 
        return inventoryRepository.save(existingInventory);
    }

    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier with ID " + id + " not found"));
    }

    public void deleteInventoryItem(Long id) {
        Inventory inventory = getInventoryItemById(id);
        List<Supplier> suppliers = supplierRepository.findAll();
        
        for (Supplier supplier : suppliers) {

            if(supplier.getProductsProvided().contains(inventory.getName())) {
                supplier.getProductsProvided().remove(inventory.getName());
            }

            if(supplier.getProductsProvided().isEmpty()) {
                Supplier supplierToRemove = getSupplierById(supplier.getId());
                supplierRepository.delete(supplierToRemove);
            }
        }
        
        inventoryRepository.delete(inventory);
    }
}

