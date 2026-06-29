package com.fynxt.orderbook.service;

import com.fynxt.orderbook.domain.model.Holding;
import com.fynxt.orderbook.domain.model.Order;
import com.fynxt.orderbook.domain.model.Trader;
import com.fynxt.orderbook.domain.model.enums.OrderSide;
import com.fynxt.orderbook.domain.model.enums.OrderStatus;
import com.fynxt.orderbook.domain.model.enums.Sector;
import com.fynxt.orderbook.dto.OrderRequest;
import com.fynxt.orderbook.dto.OrderResponse;
import com.fynxt.orderbook.exception.InsufficientSharesException;
import com.fynxt.orderbook.exception.InvalidOrderStateException;
import com.fynxt.orderbook.exception.PendingOrderLimitExceededException;
import com.fynxt.orderbook.repository.HoldingRepository;
import com.fynxt.orderbook.repository.OrderRepository;
import com.fynxt.orderbook.repository.TraderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final int MAX_PENDING_ORDERS = 3;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final HoldingRepository holdingRepository;
    private final TraderRepository traderRepository;

    public OrderService(OrderRepository orderRepository, HoldingRepository holdingRepository, TraderRepository traderRepository) {
        this.orderRepository = orderRepository;
        this.holdingRepository = holdingRepository;
        this.traderRepository = traderRepository;
    }

    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        Order order = createPendingOrder(
                request.traderId(),
                request.stock(),
                request.sector(),
                request.quantity(),
                request.side()
        );
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse fillOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order " + orderId + " not found"));
        order.fill();
        applyFilledOrderToPortfolio(order);
        Order savedOrder = orderRepository.save(order);
        log.info("Filled order {}", orderId);
        return OrderResponse.from(savedOrder);
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order " + orderId + " not found"));
        order.cancel();
        Order savedOrder = orderRepository.save(order);
        log.info("Cancelled order {}", orderId);
        return OrderResponse.from(savedOrder);
    }

    private Order createPendingOrder(String traderId, String stock, Sector sector, int quantity, OrderSide side) {
        ensureTraderExists(traderId);
        traderRepository.findLockedById(traderId)
                .orElseThrow(() -> new EntityNotFoundException("Trader " + traderId + " not found"));
        long pendingCount = orderRepository.countByTraderIdAndStatus(traderId, OrderStatus.PENDING);
        if (pendingCount >= MAX_PENDING_ORDERS) {
            throw new PendingOrderLimitExceededException("Trader " + traderId + " already has 3 pending orders");
        }
        if (side == OrderSide.SELL) {
            ensureSharesAvailable(traderId, stock, quantity);
        }
        Order order = orderRepository.save(new Order(traderId, stock, sector, quantity, side));
        log.info("Placed {} order {} for trader {} stock {} quantity {}", side, order.getId(), traderId, stock, quantity);
        return order;
    }

    private void ensureTraderExists(String traderId) {
        if (!traderRepository.existsById(traderId)) {
            traderRepository.save(new Trader(traderId, traderId));
        }
    }

    private void ensureSharesAvailable(String traderId, String stock, int quantity) {
        Holding holding = holdingRepository.findByTraderIdAndStock(traderId, stock)
                .orElseThrow(() -> new InsufficientSharesException("Trader " + traderId + " does not hold " + stock));
        if (holding.getQuantity() < quantity) {
            throw new InsufficientSharesException("Trader " + traderId + " has insufficient shares of " + stock);
        }
    }

    private void applyFilledOrderToPortfolio(Order order) {
        Holding holding = holdingRepository.findByTraderIdAndStock(order.getTraderId(), order.getStock())
                .orElseGet(() -> new Holding(order.getTraderId(), order.getStock(), order.getSector(), 0));
        if (order.getSide() == OrderSide.BUY) {
            holding.add(order.getQuantity());
        } else if (holding.getQuantity() >= order.getQuantity()) {
            holding.remove(order.getQuantity());
        } else {
            throw new InvalidOrderStateException("Cannot fill sell order because shares are no longer available");
        }
        holdingRepository.save(holding);
    }
}
