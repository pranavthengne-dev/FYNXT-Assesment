package com.fynxt.orderbook.service;

import com.fynxt.orderbook.domain.logic.Basket;
import com.fynxt.orderbook.domain.logic.OverlapCalculator;
import com.fynxt.orderbook.domain.logic.RiskClassifier;
import com.fynxt.orderbook.domain.model.Holding;
import com.fynxt.orderbook.dto.OverlapResponse;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class OverlapService {

    private final PortfolioService portfolioService;
    private final OverlapCalculator overlapCalculator = new OverlapCalculator();
    private final RiskClassifier riskClassifier = new RiskClassifier();

    public OverlapService(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    public OverlapResponse calculateOverlap(String traderId) {
        Set<String> portfolioStocks = portfolioService.findHoldings(traderId).stream()
                .map(Holding::getStock)
                .collect(Collectors.toSet());

        Map<Basket, Double> overlaps = new EnumMap<>(Basket.class);
        Arrays.stream(Basket.values())
                .forEach(basket -> overlaps.put(basket, overlapCalculator.calculate(portfolioStocks, basket.stocks())));

        Basket dominantBasket = overlaps.entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("No benchmark baskets configured"));
        double highestOverlap = overlaps.get(dominantBasket);

        List<OverlapResponse.OverlapItem> items = Arrays.stream(Basket.values())
                .map(basket -> new OverlapResponse.OverlapItem(basket, String.format("%.2f%%", overlaps.get(basket))))
                .toList();

        return new OverlapResponse(items, dominantBasket, riskClassifier.classify(highestOverlap));
    }
}
