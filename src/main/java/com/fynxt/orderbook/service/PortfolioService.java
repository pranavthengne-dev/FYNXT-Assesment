package com.fynxt.orderbook.service;

import com.fynxt.orderbook.domain.model.Holding;
import com.fynxt.orderbook.dto.AddHoldingRequest;
import com.fynxt.orderbook.dto.PortfolioResponse;
import java.util.List;

public interface PortfolioService {

    PortfolioResponse getPortfolio(String traderId);

    PortfolioResponse addToPortfolio(String traderId, AddHoldingRequest request);

    List<Holding> findHoldings(String traderId);
}
