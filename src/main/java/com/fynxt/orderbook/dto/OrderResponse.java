package com.fynxt.orderbook.dto;

import com.fynxt.orderbook.domain.model.Order;
import com.fynxt.orderbook.domain.model.enums.OrderSide;
import com.fynxt.orderbook.domain.model.enums.OrderStatus;
import com.fynxt.orderbook.domain.model.enums.Sector;
import java.time.Instant;

public record OrderResponse(
        Long id,
        String traderId,
        String stock,
        Sector sector,
        int quantity,
        OrderSide side,
        OrderStatus status,
        Instant createdAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(order.getId(), order.getTraderId(), order.getStock(), order.getSector(),
                order.getQuantity(), order.getSide(), order.getStatus(), order.getCreatedAt());
    }
}
