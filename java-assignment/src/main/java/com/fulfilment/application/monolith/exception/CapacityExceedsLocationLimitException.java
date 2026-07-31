package com.fulfilment.application.monolith.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class CapacityExceedsLocationLimitException extends WebApplicationException {
    public CapacityExceedsLocationLimitException(int warehouseCapacity, int locationCapacity) {
        super(String.format("Warehouse capacity (%d) exceeds location limit (%d)", warehouseCapacity, locationCapacity) , Response.Status.CONFLICT);
    }
}
