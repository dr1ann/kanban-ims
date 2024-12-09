package com.ims.ims.Repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ims.ims.Entities.BuyOrder;

public interface BuyOrderRepository extends JpaRepository<BuyOrder, Long> {

    @SuppressWarnings("null")
    Optional<BuyOrder> findById(Long id);
    Optional<BuyOrder> findFirstByProductIdOrderByReceiveDateDesc(Long productId);
}
