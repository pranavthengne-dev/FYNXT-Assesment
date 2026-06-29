package com.fynxt.orderbook.domain.model;

import com.fynxt.orderbook.domain.model.enums.Sector;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "holdings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"trader_id", "stock"}))
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trader_id", nullable = false)
    private String traderId;

    @Column(nullable = false)
    private String stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sector sector;

    @Column(nullable = false)
    private int quantity;

    protected Holding() {
    }

    public Holding(String traderId, String stock, Sector sector, int quantity) {
        this.traderId = traderId;
        this.stock = stock;
        this.sector = sector;
        this.quantity = quantity;
    }

    public void add(int quantity) {
        this.quantity += quantity;
    }

    public void remove(int quantity) {
        this.quantity -= quantity;
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
}
