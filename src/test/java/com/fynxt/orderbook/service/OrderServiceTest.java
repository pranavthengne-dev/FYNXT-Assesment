package com.fynxt.orderbook.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.fynxt.orderbook.domain.model.Holding;
import com.fynxt.orderbook.domain.model.Trader;
import com.fynxt.orderbook.domain.model.enums.OrderSide;
import com.fynxt.orderbook.domain.model.enums.OrderStatus;
import com.fynxt.orderbook.domain.model.enums.Sector;
import com.fynxt.orderbook.dto.OrderRequest;
import com.fynxt.orderbook.exception.InsufficientSharesException;
import com.fynxt.orderbook.exception.PendingOrderLimitExceededException;
import com.fynxt.orderbook.repository.HoldingRepository;
import com.fynxt.orderbook.repository.OrderRepository;
import com.fynxt.orderbook.repository.TraderRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private TraderRepository traderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void rejectsFourthPendingOrderForTrader() {
        when(traderRepository.findLockedById("T001")).thenReturn(Optional.of(new Trader("T001", "Test Trader")));
        when(orderRepository.countByTraderIdAndStatus("T001", OrderStatus.PENDING)).thenReturn(3L);

        OrderRequest request = new OrderRequest("T001", "AAPL", Sector.TECH, 1, OrderSide.BUY);

        assertThatThrownBy(() -> orderService.placeOrder(request))
                .isInstanceOf(PendingOrderLimitExceededException.class);
    }

    @Test
    void rejectsSellOrderWhenSharesAreInsufficient() {
        when(traderRepository.findLockedById("T001")).thenReturn(Optional.of(new Trader("T001", "Test Trader")));
        when(orderRepository.countByTraderIdAndStatus("T001", OrderStatus.PENDING)).thenReturn(0L);
        when(holdingRepository.findByTraderIdAndStock("T001", "AAPL"))
                .thenReturn(Optional.of(new Holding("T001", "AAPL", Sector.TECH, 2)));

        OrderRequest request = new OrderRequest("T001", "AAPL", Sector.TECH, 3, OrderSide.SELL);

        assertThatThrownBy(() -> orderService.placeOrder(request))
                .isInstanceOf(InsufficientSharesException.class);
    }
}
