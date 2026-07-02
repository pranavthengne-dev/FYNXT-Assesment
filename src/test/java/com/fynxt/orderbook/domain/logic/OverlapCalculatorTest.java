package com.fynxt.orderbook.domain.logic;

import static org.assertj.core.api.Assertions.assertThat;

import com.fynxt.orderbook.config.OrderBookProperties;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OverlapCalculatorTest {

    private final OverlapCalculator calculator = new OverlapCalculator();
    private final OrderBookProperties properties = testProperties();

    @Test
    void calculatesWorkedExampleTechOverlap() {
        double overlap = calculator.calculate(Set.of("AAPL", "TSLA", "NVDA"), properties.stocksFor(Basket.TECH_HEAVY));
        assertThat(overlap).isEqualTo(75.0);
    }

    @Test
    void calculatesWorkedExampleBalancedOverlap() {
        double overlap = calculator.calculate(Set.of("AAPL", "TSLA", "NVDA"), properties.stocksFor(Basket.BALANCED));
        assertThat(overlap).isEqualTo(50.0);
    }

    @Test
    void calculatesFinanceHeavyOverlap() {
        double overlap = calculator.calculate(Set.of("JPM", "BAC", "AAPL"), properties.stocksFor(Basket.FINANCE_HEAVY));
        assertThat(overlap).isEqualTo(50.0);
    }

    @Test
    void calculatesPdfSampleResponseOverlaps() {
        Set<String> portfolio = Set.of("AAPL", "TSLA", "NVDA", "AMZN", "NFLX");

        assertThat(calculator.calculate(portfolio, properties.stocksFor(Basket.TECH_HEAVY))).isEqualTo(60.0);
        assertThat(calculator.calculate(portfolio, properties.stocksFor(Basket.FINANCE_HEAVY))).isZero();
        assertThat(calculator.calculate(portfolio, properties.stocksFor(Basket.BALANCED))).isEqualTo(40.0);
    }

    @Test
    void basketsMatchAssignmentBenchmarks() {
        assertThat(properties.stocksFor(Basket.TECH_HEAVY)).containsExactlyInAnyOrder("AAPL", "MSFT", "GOOGL", "TSLA", "NVDA");
        assertThat(properties.stocksFor(Basket.FINANCE_HEAVY)).containsExactlyInAnyOrder("JPM", "GS", "BAC", "MS", "WFC");
        assertThat(properties.stocksFor(Basket.BALANCED)).containsExactlyInAnyOrder("AAPL", "JPM", "XOM", "JNJ", "TSLA");
    }

    @Test
    void returnsZeroForEmptyBasket() {
        assertThat(calculator.calculate(Set.of("AAPL"), Set.of())).isZero();
    }

    private OrderBookProperties testProperties() {
        OrderBookProperties orderBookProperties = new OrderBookProperties();
        Map<Basket, Set<String>> baskets = new EnumMap<>(Basket.class);
        baskets.put(Basket.TECH_HEAVY, Set.of("AAPL", "MSFT", "GOOGL", "TSLA", "NVDA"));
        baskets.put(Basket.FINANCE_HEAVY, Set.of("JPM", "GS", "BAC", "MS", "WFC"));
        baskets.put(Basket.BALANCED, Set.of("AAPL", "JPM", "XOM", "JNJ", "TSLA"));
        orderBookProperties.setBaskets(baskets);
        return orderBookProperties;
    }
}
