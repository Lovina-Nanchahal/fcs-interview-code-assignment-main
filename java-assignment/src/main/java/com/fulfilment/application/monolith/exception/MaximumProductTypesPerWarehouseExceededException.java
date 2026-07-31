package com.fulfilment.application.monolith.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

public class MaximumProductTypesPerWarehouseExceededException extends WebApplicationException {

    public MaximumProductTypesPerWarehouseExceededException(String warehouseBusinessUnitCode) {
        super(
                "Warehouse '" + warehouseBusinessUnitCode
                        + "' cannot store more than 5 different product types.",
                Status.CONFLICT);
    }
}