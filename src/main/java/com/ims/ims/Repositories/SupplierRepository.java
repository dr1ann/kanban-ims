package com.ims.ims.Repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ims.ims.Entities.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    @SuppressWarnings("null")
    Optional<Supplier> findById(Long id);
    List<Supplier> findByProductsProvidedContains(String productName);
    boolean existsByNameIgnoreCase(String supplierName);
    boolean existsByNameIgnoreCaseAndIdNot(String supplierName, Long excludeId);
}
