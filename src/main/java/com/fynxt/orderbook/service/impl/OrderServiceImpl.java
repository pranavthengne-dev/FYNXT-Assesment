package com.fynxt.orderbook.service.impl;

import com.fynxt.orderbook.config.OrderBookProperties;
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
import com.fynxt.orderbook.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderBookProperties properties;
    private final OrderRepository orderRepository;
    private final HoldingRepository holdingRepository;
    private final TraderRepository traderRepository;

    public OrderServiceImpl(
            OrderBookProperties properties,
            OrderRepository orderRepository,
            HoldingRepository holdingRepository,
            TraderRepository traderRepository
    ) {
        this.properties = properties;
        this.orderRepository = orderRepository;
        this.holdingRepository = holdingRepository;
        this.traderRepository = traderRepository;
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        log.info("Entry class=OrderServiceImpl method=placeOrder traderId={} stock={} side={}",
                request.traderId(), request.stock(), request.side());
        log.debug("Using class=OrderServiceImpl method=placeOrder internalMethod=createPendingOrder");
        Order order = createPendingOrder(
                request.traderId(),
                request.stock(),
                request.sector(),
                request.quantity(),
                request.side()
        );
        OrderResponse response = OrderResponse.from(order);
        log.info("Exit class=OrderServiceImpl method=placeOrder orderId={} status={}",
                response.id(), response.status());
        return response;
    }

    @Override
    @Transactional
    public OrderResponse fillOrder(Long orderId) {
        log.info("Entry class=OrderServiceImpl method=fillOrder orderId={}", orderId);
        log.debug("Using class=OrderServiceImpl method=fillOrder repository=OrderRepository.findById");
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order " + orderId + " not found"));
        order.fill();
        log.debug("Using class=OrderServiceImpl method=fillOrder internalMethod=applyFilledOrderToPortfolio");
        applyFilledOrderToPortfolio(order);
        Order savedOrder = orderRepository.save(order);
        OrderResponse response = OrderResponse.from(savedOrder);
        log.info("Exit class=OrderServiceImpl method=fillOrder orderId={} status={}",
                response.id(), response.status());
        return response;
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        log.info("Entry class=OrderServiceImpl method=cancelOrder orderId={}", orderId);
        log.debug("Using class=OrderServiceImpl method=cancelOrder repository=OrderRepository.findById");
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order " + orderId + " not found"));
        order.cancel();
        Order savedOrder = orderRepository.save(order);
        OrderResponse response = OrderResponse.from(savedOrder);
        log.info("Exit class=OrderServiceImpl method=cancelOrder orderId={} status={}",
                response.id(), response.status());
        return response;
    }

    private Order createPendingOrder(String traderId, String stock, Sector sector, int quantity, OrderSide side) {
        log.debug("Entry class=OrderServiceImpl method=createPendingOrder traderId={} stock={} side={}",
                traderId, stock, side);
        log.debug("Using class=OrderServiceImpl method=createPendingOrder internalMethod=ensureTraderExists");
        ensureTraderExists(traderId);
        log.debug("Using class=OrderServiceImpl method=createPendingOrder repository=TraderRepository.findLockedById");
        traderRepository.findLockedById(traderId)
                .orElseThrow(() -> new EntityNotFoundException("Trader " + traderId + " not found"));
        long pendingCount = orderRepository.countByTraderIdAndStatus(traderId, OrderStatus.PENDING);
        if (pendingCount >= properties.getMaxPendingOrders()) {
            throw new PendingOrderLimitExceededException(
                    "Trader " + traderId + " already has " + properties.getMaxPendingOrders() + " pending orders"
            );
        }
        if (side == OrderSide.SELL) {
            log.debug("Using class=OrderServiceImpl method=createPendingOrder internalMethod=ensureSharesAvailable");
            ensureSharesAvailable(traderId, stock, quantity);
        }
        Order order = orderRepository.save(new Order(traderId, stock, sector, quantity, side));
        log.debug("Exit class=OrderServiceImpl method=createPendingOrder orderId={} status={}",
                order.getId(), order.getStatus());
        return order;
    }

    private void ensureTraderExists(String traderId) {
        log.debug("Entry class=OrderServiceImpl method=ensureTraderExists traderId={}", traderId);
        if (!traderRepository.existsById(traderId)) {
            traderRepository.save(new Trader(traderId, traderId));
            log.debug("Created trader class=OrderServiceImpl method=ensureTraderExists traderId={}", traderId);
        }
        log.debug("Exit class=OrderServiceImpl method=ensureTraderExists traderId={}", traderId);
    }

    private void ensureSharesAvailable(String traderId, String stock, int quantity) {
        log.debug("Entry class=OrderServiceImpl method=ensureSharesAvailable traderId={} stock={} quantity={}",
                traderId, stock, quantity);
        Holding holding = holdingRepository.findByTraderIdAndStock(traderId, stock)
                .orElseThrow(() -> new InsufficientSharesException("Trader " + traderId + " does not hold " + stock));
        if (holding.getQuantity() < quantity) {
            throw new InsufficientSharesException("Trader " + traderId + " has insufficient shares of " + stock);
        }
        log.debug("Exit class=OrderServiceImpl method=ensureSharesAvailable traderId={} stock={} availableQuantity={}",
                traderId, stock, holding.getQuantity());
    }

    private void applyFilledOrderToPortfolio(Order order) {
        log.debug("Entry class=OrderServiceImpl method=applyFilledOrderToPortfolio orderId={} traderId={} side={}",
                order.getId(), order.getTraderId(), order.getSide());
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
        log.debug("Exit class=OrderServiceImpl method=applyFilledOrderToPortfolio orderId={} stock={} quantity={}",
                order.getId(), holding.getStock(), holding.getQuantity());
    }
}
