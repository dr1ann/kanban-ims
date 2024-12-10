package com.ims.ims.Repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ims.ims.Entities.ProductCategory;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    @SuppressWarnings("null")
    Optional<ProductCategory> findById(Long id);
    boolean existsByNameIgnoreCase(String productCategoryName);
    boolean existsByNameIgnoreCaseAndIdNot(String productCategoryName, Long excludeId);
}
