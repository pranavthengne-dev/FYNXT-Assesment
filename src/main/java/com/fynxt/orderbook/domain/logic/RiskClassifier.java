package com.fynxt.orderbook.domain.logic;

public class RiskClassifier {

    public Risk classify(double highestOverlapPercent) {
        if (highestOverlapPercent >= 60.0) {
            return Risk.HIGH;
        }
        if (highestOverlapPercent >= 40.0) {
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
