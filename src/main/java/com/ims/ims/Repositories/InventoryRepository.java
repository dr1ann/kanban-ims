package com.ims.ims.Repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ims.ims.Entities.Inventory;
import com.ims.ims.Entities.ProductCategory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @SuppressWarnings("null")
    Optional<Inventory> findById(Long id);
    boolean existsByNameIgnoreCase(String productName);
    boolean existsByCategory(ProductCategory category);
}
