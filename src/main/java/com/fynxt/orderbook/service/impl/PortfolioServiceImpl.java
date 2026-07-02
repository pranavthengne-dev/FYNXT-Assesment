package com.fynxt.orderbook.service.impl;

import com.fynxt.orderbook.domain.model.Holding;
import com.fynxt.orderbook.domain.model.Trader;
import com.fynxt.orderbook.dto.AddHoldingRequest;
import com.fynxt.orderbook.dto.PortfolioResponse;
import com.fynxt.orderbook.repository.HoldingRepository;
import com.fynxt.orderbook.repository.TraderRepository;
import com.fynxt.orderbook.service.PortfolioService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private static final Logger log = LoggerFactory.getLogger(PortfolioServiceImpl.class);

    private final HoldingRepository holdingRepository;
    private final TraderRepository traderRepository;

    public PortfolioServiceImpl(HoldingRepository holdingRepository, TraderRepository traderRepository) {
        this.holdingRepository = holdingRepository;
        this.traderRepository = traderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioResponse getPortfolio(String traderId) {
        log.info("Entry class=PortfolioServiceImpl method=getPortfolio traderId={}", traderId);
        log.debug("Using class=PortfolioServiceImpl method=getPortfolio internalMethod=findHoldings");
        PortfolioResponse response = PortfolioResponse.from(traderId, findHoldings(traderId));
        log.info("Exit class=PortfolioServiceImpl method=getPortfolio traderId={} positions={}",
                traderId, response.positions().size());
        return response;
    }

    @Override
    @Transactional
    public PortfolioResponse addToPortfolio(String traderId, AddHoldingRequest request) {
        log.info("Entry class=PortfolioServiceImpl method=addToPortfolio traderId={} stock={} quantity={}",
                traderId, request.stock(), request.quantity());
        log.debug("Using class=PortfolioServiceImpl method=addToPortfolio internalMethod=ensureTraderExists");
        ensureTraderExists(traderId);
        log.debug("Using class=PortfolioServiceImpl method=addToPortfolio repository=HoldingRepository.findByTraderIdAndStock");
        Holding holding = holdingRepository.findByTraderIdAndStock(traderId, request.stock())
                .orElseGet(() -> new Holding(traderId, request.stock(), request.sector(), 0));
        holding.add(request.quantity());
        holdingRepository.save(holding);
        log.debug("Using class=PortfolioServiceImpl method=addToPortfolio internalMethod=findHoldings");
        PortfolioResponse response = PortfolioResponse.from(traderId, findHoldings(traderId));
        log.info("Exit class=PortfolioServiceImpl method=addToPortfolio traderId={} stock={} quantity={}",
                traderId, request.stock(), holding.getQuantity());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Holding> findHoldings(String traderId) {
        log.debug("Entry class=PortfolioServiceImpl method=findHoldings traderId={}", traderId);
        List<Holding> holdings = holdingRepository.findByTraderId(traderId);
        log.debug("Exit class=PortfolioServiceImpl method=findHoldings traderId={} holdings={}",
                traderId, holdings.size());
        return holdings;
    }

    private void ensureTraderExists(String traderId) {
        log.debug("Entry class=PortfolioServiceImpl method=ensureTraderExists traderId={}", traderId);
        if (!traderRepository.existsById(traderId)) {
            traderRepository.save(new Trader(traderId, traderId));
            log.debug("Created trader class=PortfolioServiceImpl method=ensureTraderExists traderId={}", traderId);
        }
        log.debug("Exit class=PortfolioServiceImpl method=ensureTraderExists traderId={}", traderId);
    }
}
