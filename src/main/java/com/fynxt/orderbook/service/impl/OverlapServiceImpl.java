package com.fynxt.orderbook.service.impl;

import com.fynxt.orderbook.domain.logic.Basket;
import com.fynxt.orderbook.domain.logic.OverlapCalculator;
import com.fynxt.orderbook.domain.logic.RiskClassifier;
import com.fynxt.orderbook.domain.model.Holding;
import com.fynxt.orderbook.dto.OverlapResponse;
import com.fynxt.orderbook.service.OverlapService;
import com.fynxt.orderbook.service.PortfolioService;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OverlapServiceImpl implements OverlapService {

    private static final Logger log = LoggerFactory.getLogger(OverlapServiceImpl.class);

    private final PortfolioService portfolioService;
    private final OverlapCalculator overlapCalculator = new OverlapCalculator();
    private final RiskClassifier riskClassifier = new RiskClassifier();

    public OverlapServiceImpl(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Override
    public OverlapResponse calculateOverlap(String traderId) {
        log.info("Entry class=OverlapServiceImpl method=calculateOverlap traderId={}", traderId);
        log.debug("Using class=OverlapServiceImpl method=calculateOverlap dependency=PortfolioService.findHoldings");
        Set<String> portfolioStocks = portfolioService.findHoldings(traderId).stream()
                .map(Holding::getStock)
                .collect(Collectors.toSet());

        Map<Basket, Double> overlaps = new EnumMap<>(Basket.class);
        log.debug("Using class=OverlapServiceImpl method=calculateOverlap dependency=OverlapCalculator.calculate");
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

        log.debug("Using class=OverlapServiceImpl method=calculateOverlap dependency=RiskClassifier.classify");
        OverlapResponse response = new OverlapResponse(items, dominantBasket, riskClassifier.classify(highestOverlap));
        log.info("Exit class=OverlapServiceImpl method=calculateOverlap traderId={} dominantBasket={} riskFlag={}",
                traderId, response.dominantBasket(), response.riskFlag());
        return response;
    }
}
