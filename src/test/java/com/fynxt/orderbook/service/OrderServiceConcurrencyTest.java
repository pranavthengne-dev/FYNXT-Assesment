package com.fynxt.orderbook.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fynxt.orderbook.domain.model.Trader;
import com.fynxt.orderbook.domain.model.enums.OrderSide;
import com.fynxt.orderbook.domain.model.enums.OrderStatus;
import com.fynxt.orderbook.domain.model.enums.Sector;
import com.fynxt.orderbook.dto.OrderRequest;
import com.fynxt.orderbook.repository.OrderRepository;
import com.fynxt.orderbook.repository.TraderRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrderServiceConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TraderRepository traderRepository;

    @Test
    void pendingOrderCapHoldsUnderConcurrentPlacement() throws InterruptedException {
        String traderId = traderRepository.save(new Trader("T-CONCURRENT", "Concurrent Trader")).getId();
        int attempts = 10;
        ExecutorService executor = Executors.newFixedThreadPool(attempts);
        CountDownLatch ready = new CountDownLatch(attempts);
        CountDownLatch start = new CountDownLatch(1);
        List<Exception> failures = new ArrayList<>();

        for (int i = 0; i < attempts; i++) {
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    orderService.placeOrder(new OrderRequest(traderId, "AAPL", Sector.TECH, 1, OrderSide.BUY));
                } catch (Exception exception) {
                    synchronized (failures) {
                        failures.add(exception);
                    }
                }
            });
        }

        ready.await(5, TimeUnit.SECONDS);
        start.countDown();
        executor.shutdown();
        assertThat(executor.awaitTermination(10, TimeUnit.SECONDS)).isTrue();
        assertThat(orderRepository.countByTraderIdAndStatus(traderId, OrderStatus.PENDING)).isLessThanOrEqualTo(3);
    }
}
