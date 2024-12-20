package com.ims.ims.Services;

import com.ims.ims.Entities.SellOrder;
import com.ims.ims.Repositories.SellOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SellOrderService {

    @Autowired
    private SellOrderRepository sellOrderRepository;

    
    public void addNewSellOrder(SellOrder sellOrder) {
        sellOrder.calculateTotalAmount();
        sellOrderRepository.save(sellOrder);
    }

    public List<SellOrder> getAllSellOrders() {
        return sellOrderRepository.findAll();
    }

    public SellOrder getSellOrderById(Long id) {
        return sellOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sell Order with ID " + id + " not found"));
    }

    public SellOrder updateSellOrder(Long id, SellOrder updateSellOrder) {
        SellOrder existingSellOrder = getSellOrderById(id);
    
        existingSellOrder.setProduct(updateSellOrder.getProduct());
        existingSellOrder.setQuantity(updateSellOrder.getQuantity());
        updateSellOrder.calculateTotalAmount();
        existingSellOrder.setTotalAmount(updateSellOrder.getTotalAmount());
        return sellOrderRepository.save(existingSellOrder);
    }   

    // @Transactional
    // public void deleteProductCategory(Long id) {
    //     ProductCategory productCategory = getProductCategoryById(id);
    //     List<Inventory> products = inventoryRepository.findAll();

    //     for (Inventory product : products) {
    //         if (product.getCategory() != null && productCategory.getId().equals(product.getCategory().getId())) {
    //             product.setCategory(null);
    //             inventoryRepository.save(product);
    //         }
    //     }

    //     productCategoryRepository.delete(productCategory);
    // }   
}
