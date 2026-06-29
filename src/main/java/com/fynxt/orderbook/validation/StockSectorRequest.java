package com.fynxt.orderbook.validation;

import com.fynxt.orderbook.domain.model.enums.Sector;

public interface StockSectorRequest {

    String stock();

    Sector sector();
}
