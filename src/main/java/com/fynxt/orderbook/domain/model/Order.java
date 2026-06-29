package com.fynxt.orderbook.domain.model;

import com.fynxt.orderbook.domain.model.enums.OrderSide;
import com.fynxt.orderbook.domain.model.enums.OrderStatus;
import com.fynxt.orderbook.domain.model.enums.Sector;
import com.fynxt.orderbook.exception.InvalidOrderStateException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String traderId;

    @Column(nullable = false)
    private String stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sector sector;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderSide side;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    protected Order() {
    }

    public Order(String traderId, String stock, Sector sector, int quantity, OrderSide side) {
        this.traderId = traderId;
        this.stock = stock;
        this.sector = sector;
        this.quantity = quantity;
        this.side = side;
        this.status = OrderStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public void fill() {
        requirePending("fill");
        this.status = OrderStatus.FILLED;
    }

    public void cancel() {
        requirePending("cancel");
        this.status = OrderStatus.CANCELLED;
    }

    private void requirePending(String action) {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Cannot " + action + " order " + id + " because it is " + status);
        }
    }

    public Long getId() {
        return id;
    }

    public String getTraderId() {
        return traderId;
    }

    public String getStock() {
        return stock;
    }

    public Sector getSector() {
        return sector;
    }

    public int getQuantity() {
        return quantity;
    }

    public OrderSide getSide() {
        return side;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
