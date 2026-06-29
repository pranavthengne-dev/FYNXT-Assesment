package com.fynxt.orderbook.controller;

import com.fynxt.orderbook.dto.AddHoldingRequest;
import com.fynxt.orderbook.dto.OverlapResponse;
import com.fynxt.orderbook.dto.PortfolioResponse;
import com.fynxt.orderbook.service.OverlapService;
import com.fynxt.orderbook.service.PortfolioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

    private final PortfolioService portfolioService;
    private final OverlapService overlapService;

    public PortfolioController(PortfolioService portfolioService, OverlapService overlapService) {
        this.portfolioService = portfolioService;
        this.overlapService = overlapService;
    }

    @GetMapping("/{traderId}")
    public PortfolioResponse getPortfolio(@PathVariable @NotBlank String traderId) {
        return portfolioService.getPortfolio(traderId);
    }

    @GetMapping("/{traderId}/overlap")
    public OverlapResponse getOverlap(@PathVariable @NotBlank String traderId) {
        return overlapService.calculateOverlap(traderId);
    }

    @PostMapping("/{traderId}/add")
    public PortfolioResponse addHolding(@PathVariable @NotBlank String traderId, @Valid @RequestBody AddHoldingRequest request) {
        return portfolioService.addToPortfolio(traderId, request);
    }
}
