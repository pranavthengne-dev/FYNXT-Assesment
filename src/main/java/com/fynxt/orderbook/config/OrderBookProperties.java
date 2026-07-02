package com.fynxt.orderbook.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "orderbook")
public class OrderBookProperties {

    private int maxPendingOrders;
    private Risk risk = new Risk();

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
