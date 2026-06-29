package com.fynxt.orderbook.domain.logic;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;

class OverlapCalculatorTest {

    private final OverlapCalculator calculator = new OverlapCalculator();

    @Test
    void calculatesWorkedExampleTechOverlap() {
        double overlap = calculator.calculate(Set.of("AAPL", "TSLA", "NVDA"), Basket.TECH_HEAVY.stocks());
        assertThat(overlap).isEqualTo(75.0);
    }

    @Test
    void calculatesWorkedExampleBalancedOverlap() {
        double overlap = calculator.calculate(Set.of("AAPL", "TSLA", "NVDA"), Basket.BALANCED.stocks());
        assertThat(overlap).isEqualTo(50.0);
    }

    @Test
    void calculatesFinanceHeavyOverlap() {
        double overlap = calculator.calculate(Set.of("JPM", "BAC", "AAPL"), Basket.FINANCE_HEAVY.stocks());
        assertThat(overlap).isEqualTo(50.0);
    }

    @Test
    void calculatesPdfSampleResponseOverlaps() {
        Set<String> portfolio = Set.of("AAPL", "TSLA", "NVDA", "AMZN", "NFLX");

        assertThat(calculator.calculate(portfolio, Basket.TECH_HEAVY.stocks())).isEqualTo(60.0);
        assertThat(calculator.calculate(portfolio, Basket.FINANCE_HEAVY.stocks())).isZero();
        assertThat(calculator.calculate(portfolio, Basket.BALANCED.stocks())).isEqualTo(40.0);
    }

    @Test
    void basketsMatchAssignmentBenchmarks() {
        assertThat(Basket.TECH_HEAVY.stocks()).containsExactlyInAnyOrder("AAPL", "MSFT", "GOOGL", "TSLA", "NVDA");
        assertThat(Basket.FINANCE_HEAVY.stocks()).containsExactlyInAnyOrder("JPM", "GS", "BAC", "MS", "WFC");
        assertThat(Basket.BALANCED.stocks()).containsExactlyInAnyOrder("AAPL", "JPM", "XOM", "JNJ", "TSLA");
    }

    @Test
    void returnsZeroForEmptyBasket() {
        assertThat(calculator.calculate(Set.of("AAPL"), Set.of())).isZero();
    }
}
