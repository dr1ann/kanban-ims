package com.ims.ims.Repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ims.ims.Entities.SellOrder;

public interface SellOrderRepository extends JpaRepository<SellOrder, Long> {

    @SuppressWarnings("null")
    Optional<SellOrder> findById(Long id);
}
