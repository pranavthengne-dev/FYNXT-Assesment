package com.fynxt.orderbook.service;

import com.fynxt.orderbook.dto.OverlapResponse;

public interface OverlapService {

    OverlapResponse calculateOverlap(String traderId);
}
