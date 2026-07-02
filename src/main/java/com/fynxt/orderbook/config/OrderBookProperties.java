package com.fynxt.orderbook.config;

import com.fynxt.orderbook.domain.logic.Basket;
import com.fynxt.orderbook.domain.model.enums.Sector;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "orderbook")
public class OrderBookProperties {

    private int maxPendingOrders;
    private Risk risk = new Risk();
    private Map<Basket, Set<String>> baskets = new EnumMap<>(Basket.class);
    private Map<Sector, Set<String>> stocksBySector = new EnumMap<>(Sector.class);

    public int getMaxPendingOrders() {
        return maxPendingOrders;
    }

    public void setMaxPendingOrders(int maxPendingOrders) {
        this.maxPendingOrders = maxPendingOrders;
    }

    public Risk getRisk() {
        return risk;
    }

    public void setRisk(Risk risk) {
        this.risk = risk;
    }

    public Map<Basket, Set<String>> getBaskets() {
        return baskets;
    }

    public void setBaskets(Map<Basket, Set<String>> baskets) {
        this.baskets = baskets;
    }

    public Map<Sector, Set<String>> getStocksBySector() {
        return stocksBySector;
    }

    public void setStocksBySector(Map<Sector, Set<String>> stocksBySector) {
        this.stocksBySector = stocksBySector;
    }

    public Set<String> stocksFor(Basket basket) {
        return baskets.getOrDefault(basket, Set.of());
    }

    public Set<String> stocksFor(Sector sector) {
        return stocksBySector.getOrDefault(sector, Set.of());
    }

    public static class Risk {

        private double highOverlapPercent;
        private double mediumOverlapPercent;

        public double getHighOverlapPercent() {
            return highOverlapPercent;
        }

        public void setHighOverlapPercent(double highOverlapPercent) {
            this.highOverlapPercent = highOverlapPercent;
        }

        public double getMediumOverlapPercent() {
            return mediumOverlapPercent;
        }

        public void setMediumOverlapPercent(double mediumOverlapPercent) {
            this.mediumOverlapPercent = mediumOverlapPercent;
        }
    }
}
