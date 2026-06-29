package com.fynxt.orderbook.domain.logic;

import java.util.Set;

public class OverlapCalculator {

    public double calculate(Set<String> portfolio, Set<String> basket) {
        if (portfolio == null || basket == null || portfolio.isEmpty() || basket.isEmpty()) {
            return 0.0;
        }
        long common = basket.stream().filter(portfolio::contains).count();
        return (2.0 * common / (portfolio.size() + basket.size())) * 100.0;
    }
}
