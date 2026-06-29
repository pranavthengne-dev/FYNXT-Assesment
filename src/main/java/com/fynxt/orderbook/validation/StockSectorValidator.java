package com.fynxt.orderbook.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StockSectorValidator implements ConstraintValidator<ValidStockSector, StockSectorRequest> {

    @Override
    public boolean isValid(StockSectorRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }
        return StockSectorRules.matches(request.stock(), request.sector());
    }
}
