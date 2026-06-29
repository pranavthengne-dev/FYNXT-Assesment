package com.fynxt.orderbook.dto;

import com.fynxt.orderbook.domain.model.Holding;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public record PortfolioResponse(String traderId, Map<String, Integer> positions, Map<String, Integer> sectorBreakdown) {

    public static PortfolioResponse from(String traderId, List<Holding> holdings) {
        Map<String, Integer> positions = holdings.stream()
                .collect(Collectors.toMap(Holding::getStock, Holding::getQuantity, Integer::sum, TreeMap::new));
        Map<String, Integer> sectorBreakdown = holdings.stream()
                .collect(Collectors.toMap(holding -> holding.getSector().name(), Holding::getQuantity, Integer::sum, TreeMap::new));
        return new PortfolioResponse(traderId, positions, sectorBreakdown);
    }
}
