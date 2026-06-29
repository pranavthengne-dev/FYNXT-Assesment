package com.fynxt.orderbook.repository;

import com.fynxt.orderbook.domain.model.Trader;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface TraderRepository extends JpaRepository<Trader, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Trader> findLockedById(String id);
}
