package com.fynxt.orderbook.validation;

import com.fynxt.orderbook.domain.model.enums.Sector;
import org.springframework.stereotype.Component;

@Component
public class StockSectorRules {

    public boolean matches(String stock, Sector sector) {
        if (stock == null || sector == null) {
            return true;
        }
        return sector.containsStock(stock);
    }
}
