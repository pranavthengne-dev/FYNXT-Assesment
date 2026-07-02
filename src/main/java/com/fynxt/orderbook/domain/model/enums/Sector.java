package com.fynxt.orderbook.domain.model.enums;

import java.util.Set;

public enum Sector {
    TECH(Set.of("AAPL", "MSFT", "GOOGL", "TSLA", "NVDA")),
    FINANCE(Set.of("JPM", "GS", "BAC", "MS", "WFC")),
    ENERGY(Set.of("XOM")),
    HEALTHCARE(Set.of("JNJ"));

    private final Set<String> stocks;

    Sector(Set<String> stocks) {
        this.stocks = stocks;
    }

    public boolean containsStock(String stock) {
        return stocks.contains(stock);
    }
}
