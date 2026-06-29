package com.fynxt.orderbook.service;

import com.fynxt.orderbook.domain.model.Holding;
import com.fynxt.orderbook.domain.model.Trader;
import com.fynxt.orderbook.dto.AddHoldingRequest;
import com.fynxt.orderbook.dto.PortfolioResponse;
import com.fynxt.orderbook.repository.HoldingRepository;
import com.fynxt.orderbook.repository.TraderRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PortfolioService {

    private final HoldingRepository holdingRepository;
    private final TraderRepository traderRepository;

    public PortfolioService(HoldingRepository holdingRepository, TraderRepository traderRepository) {
        this.holdingRepository = holdingRepository;
        this.traderRepository = traderRepository;
    }

    @Transactional(readOnly = true)
    public PortfolioResponse getPortfolio(String traderId) {
        return PortfolioResponse.from(traderId, findHoldings(traderId));
    }

    @Transactional
    public PortfolioResponse addToPortfolio(String traderId, AddHoldingRequest request) {
        ensureTraderExists(traderId);
        Holding holding = holdingRepository.findByTraderIdAndStock(traderId, request.stock())
                .orElseGet(() -> new Holding(traderId, request.stock(), request.sector(), 0));
        holding.add(request.quantity());
        holdingRepository.save(holding);
        return PortfolioResponse.from(traderId, findHoldings(traderId));
    }

    @Transactional(readOnly = true)
    List<Holding> findHoldings(String traderId) {
        return holdingRepository.findByTraderId(traderId);
    }

    private void ensureTraderExists(String traderId) {
        if (!traderRepository.existsById(traderId)) {
            traderRepository.save(new Trader(traderId, traderId));
        }
    }
}
