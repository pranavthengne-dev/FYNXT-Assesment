package com.fynxt.orderbook.dto;

import com.fynxt.orderbook.domain.model.enums.OrderSide;
import com.fynxt.orderbook.domain.model.enums.Sector;
import com.fynxt.orderbook.validation.StockSectorRequest;
import com.fynxt.orderbook.validation.ValidStockSector;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@ValidStockSector
public record OrderRequest(
        @NotBlank String traderId,
        @NotBlank @Pattern(regexp = "^[A-Z]{1,10}$", message = "must be an uppercase stock symbol") String stock,
        @NotNull Sector sector,
        @Min(1) int quantity,
        @NotNull OrderSide side
) implements StockSectorRequest {
}
