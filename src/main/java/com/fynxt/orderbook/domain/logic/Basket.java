package com.fynxt.orderbook.domain.logic;

import java.util.Set;

public enum Basket {
    TECH_HEAVY(Set.of("AAPL", "MSFT", "GOOGL", "TSLA", "NVDA")),
    FINANCE_HEAVY(Set.of("JPM", "GS", "BAC", "MS", "WFC")),
    BALANCED(Set.of("AAPL", "JPM", "XOM", "JNJ", "TSLA"));

    private final Set<String> stocks;

    Basket(Set<String> stocks) {
        this.stocks = stocks;
    }

    public Set<String> stocks() {
        return stocks;
    }
}
