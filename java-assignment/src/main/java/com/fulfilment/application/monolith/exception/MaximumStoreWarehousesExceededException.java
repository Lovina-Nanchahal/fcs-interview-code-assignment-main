package com.fulfilment.application.monolith.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

public class MaximumStoreWarehousesExceededException extends WebApplicationException {

    public MaximumStoreWarehousesExceededException(Long storeId) {
        super(
                String.format(
                        "Store %d can be fulfilled by a maximum of 3 warehouses.",
                        storeId),
                Status.CONFLICT);
    }
}