package com.fynxt.orderbook.controller;

import com.fynxt.orderbook.dto.OrderRequest;
import com.fynxt.orderbook.dto.OrderResponse;
import com.fynxt.orderbook.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest request) {
        log.info("Entry class=OrderController method=placeOrder traderId={} stock={} side={}",
                request.traderId(), request.stock(), request.side());
        log.debug("Using class=OrderController method=placeOrder dependency=OrderService.placeOrder");
        OrderResponse response = orderService.placeOrder(request);
        log.info("Exit class=OrderController method=placeOrder orderId={} status={}",
                response.id(), response.status());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/fill")
    public ResponseEntity<OrderResponse> fillOrder(@PathVariable @Positive Long id) {
        log.info("Entry class=OrderController method=fillOrder orderId={}", id);
        log.debug("Using class=OrderController method=fillOrder dependency=OrderService.fillOrder");
        OrderResponse response = orderService.fillOrder(id);
        log.info("Exit class=OrderController method=fillOrder orderId={} status={}",
                response.id(), response.status());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable @Positive Long id) {
        log.info("Entry class=OrderController method=cancelOrder orderId={}", id);
        log.debug("Using class=OrderController method=cancelOrder dependency=OrderService.cancelOrder");
        OrderResponse response = orderService.cancelOrder(id);
        log.info("Exit class=OrderController method=cancelOrder orderId={} status={}",
                response.id(), response.status());
        return ResponseEntity.ok(response);
    }
}
