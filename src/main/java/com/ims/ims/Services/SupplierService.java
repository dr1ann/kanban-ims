package com.ims.ims.Services;

import com.ims.ims.Entities.Supplier;
import com.ims.ims.Repositories.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SupplierService {
    @Autowired
    private SupplierRepository supplierRepository;

    public void addSupplier(Supplier supplier) {
        supplierRepository.save(supplier);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier with ID " + id + " not found"));
    }
    public List<Supplier> findByProductsProvidedContains(String productName) {
        return supplierRepository.findByProductsProvidedContains(productName);
    }

    public Supplier updateSupplier(Long id, Supplier updatedSupplier) {
        return supplierRepository.save(updatedSupplier);
    } 

    public void deleteSupplier(Long id) {
        Supplier supplier = getSupplierById(id);
        supplierRepository.delete(supplier);
    }
}
