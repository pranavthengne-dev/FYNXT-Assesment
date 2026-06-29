package com.fynxt.orderbook.validation;

import com.fynxt.orderbook.domain.model.enums.Sector;
import java.util.Map;
import java.util.Set;

public final class StockSectorRules {

    private static final Map<Sector, Set<String>> STOCKS_BY_SECTOR = Map.of(
            Sector.TECH, Set.of("AAPL", "MSFT", "GOOGL", "TSLA", "NVDA"),
            Sector.FINANCE, Set.of("JPM", "GS", "BAC", "MS", "WFC"),
            Sector.ENERGY, Set.of("XOM"),
            Sector.HEALTHCARE, Set.of("JNJ")
    );

    private StockSectorRules() {
    }

    public static boolean matches(String stock, Sector sector) {
        if (stock == null || sector == null) {
            return true;
        }
        return STOCKS_BY_SECTOR.getOrDefault(sector, Set.of()).contains(stock);
    }
}
