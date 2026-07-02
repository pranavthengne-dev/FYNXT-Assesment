package com.fynxt.orderbook.validation;

import com.fynxt.orderbook.config.OrderBookProperties;
import com.fynxt.orderbook.domain.model.enums.Sector;
import org.springframework.stereotype.Component;

@Component
public class StockSectorRules {

    private final OrderBookProperties properties;

    public StockSectorRules(OrderBookProperties properties) {
        this.properties = properties;
    }

    public boolean matches(String stock, Sector sector) {
        if (stock == null || sector == null) {
            return true;
        }
        return properties.stocksFor(sector).contains(stock);
    }
}
