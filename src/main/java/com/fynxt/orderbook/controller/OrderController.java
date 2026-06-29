package com.fynxt.orderbook.controller;

import com.fynxt.orderbook.dto.OrderRequest;
import com.fynxt.orderbook.dto.OrderResponse;
import com.fynxt.orderbook.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponse placeOrder(@Valid @RequestBody OrderRequest request) {
        return orderService.placeOrder(request);
    }

    @PostMapping("/{id}/fill")
    public OrderResponse fillOrder(@PathVariable @Positive Long id) {
        return orderService.fillOrder(id);
    }

    @PostMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable @Positive Long id) {
        return orderService.cancelOrder(id);
    }
}
