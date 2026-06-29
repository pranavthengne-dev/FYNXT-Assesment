package com.fynxt.orderbook.dto;

import com.fynxt.orderbook.domain.logic.Basket;
import com.fynxt.orderbook.domain.logic.RiskClassifier.Risk;
import java.util.List;

public record OverlapResponse(List<OverlapItem> overlaps, Basket dominantBasket, Risk riskFlag) {

    public record OverlapItem(Basket basket, String overlap) {
    }
}
