package com.fynxt.orderbook.controller;

import com.fynxt.orderbook.dto.AddHoldingRequest;
import com.fynxt.orderbook.dto.OverlapResponse;
import com.fynxt.orderbook.dto.PortfolioResponse;
import com.fynxt.orderbook.service.OverlapService;
import com.fynxt.orderbook.service.PortfolioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portfolio")
@Validated
public class PortfolioController {

    private static final Logger log = LoggerFactory.getLogger(PortfolioController.class);

    private final PortfolioService portfolioService;
    private final OverlapService overlapService;

    public PortfolioController(PortfolioService portfolioService, OverlapService overlapService) {
        this.portfolioService = portfolioService;
        this.overlapService = overlapService;
    }

    @GetMapping("/{traderId}")
    public ResponseEntity<PortfolioResponse> getPortfolio(@PathVariable @NotBlank String traderId) {
        log.info("Entry class=PortfolioController method=getPortfolio traderId={}", traderId);
        log.debug("Using class=PortfolioController method=getPortfolio dependency=PortfolioService.getPortfolio");
        PortfolioResponse response = portfolioService.getPortfolio(traderId);
        log.info("Exit class=PortfolioController method=getPortfolio traderId={} positions={}",
                traderId, response.positions().size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{traderId}/overlap")
    public ResponseEntity<OverlapResponse> getOverlap(@PathVariable @NotBlank String traderId) {
        log.info("Entry class=PortfolioController method=getOverlap traderId={}", traderId);
        log.debug("Using class=PortfolioController method=getOverlap dependency=OverlapService.calculateOverlap");
        OverlapResponse response = overlapService.calculateOverlap(traderId);
        log.info("Exit class=PortfolioController method=getOverlap traderId={} dominantBasket={} riskFlag={}",
                traderId, response.dominantBasket(), response.riskFlag());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{traderId}/add")
    public ResponseEntity<PortfolioResponse> addHolding(
            @PathVariable @NotBlank String traderId,
            @Valid @RequestBody AddHoldingRequest request
    ) {
        log.info("Entry class=PortfolioController method=addHolding traderId={} stock={} quantity={}",
                traderId, request.stock(), request.quantity());
        log.debug("Using class=PortfolioController method=addHolding dependency=PortfolioService.addToPortfolio");
        PortfolioResponse response = portfolioService.addToPortfolio(traderId, request);
        log.info("Exit class=PortfolioController method=addHolding traderId={} positions={}",
                traderId, response.positions().size());
        return ResponseEntity.ok(response);
    }
}
