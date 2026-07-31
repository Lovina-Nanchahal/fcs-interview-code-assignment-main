package com.fulfilment.application.monolith.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

public class MaximumFulfilmentWarehousesExceededException extends WebApplicationException {

    public MaximumFulfilmentWarehousesExceededException(Long storeId, Long productId) {
        super(
                String.format(
                        "Product %d can be fulfilled by a maximum of 2 warehouses for store %d.",
                        productId,
                        storeId),
                Status.CONFLICT);
    }
}