package com.fynxt.orderbook.domain.logic;

import com.fynxt.orderbook.config.OrderBookProperties;
import org.springframework.stereotype.Component;

@Component
public class RiskClassifier {

    private final OrderBookProperties properties;

    public RiskClassifier(OrderBookProperties properties) {
        this.properties = properties;
    }

    public Risk classify(double highestOverlapPercent) {
        if (highestOverlapPercent >= properties.getRisk().getHighOverlapPercent()) {
            return Risk.HIGH;
        }
        if (highestOverlapPercent >= properties.getRisk().getMediumOverlapPercent()) {
            return Risk.MEDIUM;
        }
        return Risk.LOW;
    }

    public enum Risk {
        HIGH,
        MEDIUM,
        LOW
    }
}
