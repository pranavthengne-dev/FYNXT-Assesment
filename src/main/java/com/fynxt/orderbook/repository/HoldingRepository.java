package com.fynxt.orderbook.repository;

import com.fynxt.orderbook.domain.model.Holding;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface HoldingRepository extends JpaRepository<Holding, Long> {

    List<Holding> findByTraderId(String traderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Holding> findByTraderIdAndStock(String traderId, String stock);
}
