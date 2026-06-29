package com.fynxt.orderbook.repository;

import com.fynxt.orderbook.domain.model.Order;
import com.fynxt.orderbook.domain.model.enums.OrderStatus;
import jakarta.persistence.LockModeType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.traderId = :traderId and o.status = :status")
    List<Order> findLockedByTraderIdAndStatus(@Param("traderId") String traderId, @Param("status") OrderStatus status);

    @Transactional
    default long countByTraderIdAndStatus(String traderId, OrderStatus status) {
        return findLockedByTraderIdAndStatus(traderId, status).size();
    }
}
