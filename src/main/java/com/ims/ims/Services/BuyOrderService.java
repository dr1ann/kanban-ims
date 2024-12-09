package com.ims.ims.Services;


import com.ims.ims.Entities.BuyOrder;
import com.ims.ims.Repositories.BuyOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BuyOrderService {

    @Autowired
    private BuyOrderRepository buyOrderRepository;

    
    public void addNewBuyOrder(BuyOrder buyOrder) {
        buyOrder.calculateTotalAmount();
        buyOrderRepository.save(buyOrder);
    }

    public BuyOrder getLatestOrderByProduct(Long productId) {
        return buyOrderRepository.findFirstByProductIdOrderByReceiveDateDesc(productId)
                .orElse(null);
    }
    
    public List<BuyOrder> getAllBuyOrders() {
        return buyOrderRepository.findAll();
    }

    public BuyOrder getBuyOrderById(Long id) {
        return buyOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Buy Order with ID " + id + " not found"));
    }

    public BuyOrder updateBuyOrder(Long id, BuyOrder updateBuyOrder) {
        BuyOrder existingBuyOrder = getBuyOrderById(id);
    
        existingBuyOrder.setProduct(updateBuyOrder.getProduct());
        existingBuyOrder.setQuantity(updateBuyOrder.getQuantity());
        updateBuyOrder.calculateTotalAmount();
        existingBuyOrder.setTotalAmount(updateBuyOrder.getTotalAmount());
        existingBuyOrder.setSupplier(updateBuyOrder.getSupplier());
        existingBuyOrder.setStatus(updateBuyOrder.getStatus());
        existingBuyOrder.setReceiveDate(updateBuyOrder.getReceiveDate());
        existingBuyOrder.setBackOrderQuantity(updateBuyOrder.getBackOrderQuantity());
        existingBuyOrder.setBadOrderQuantity(updateBuyOrder.getBadOrderQuantity());
        return buyOrderRepository.save(existingBuyOrder);
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
