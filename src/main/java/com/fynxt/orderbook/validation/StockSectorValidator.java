package com.fynxt.orderbook.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StockSectorValidator implements ConstraintValidator<ValidStockSector, StockSectorRequest> {

    private final StockSectorRules stockSectorRules;

    public StockSectorValidator(StockSectorRules stockSectorRules) {
        this.stockSectorRules = stockSectorRules;
    }

    @Override
    public boolean isValid(StockSectorRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }
        return stockSectorRules.matches(request.stock(), request.sector());
    }
}
