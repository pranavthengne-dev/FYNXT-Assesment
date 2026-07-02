package com.fynxt.orderbook.service;

import com.fynxt.orderbook.dto.OrderRequest;
import com.fynxt.orderbook.dto.OrderResponse;

public interface OrderService {

    OrderResponse placeOrder(OrderRequest request);

    OrderResponse fillOrder(Long orderId);

    OrderResponse cancelOrder(Long orderId);
}
