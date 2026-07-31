package com.fulfilment.application.monolith.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class MaximumWarehousesReachedException extends WebApplicationException {
    public MaximumWarehousesReachedException(String location) {
        super("Maximum warehouses reached for location: " + location, Response.Status.CONFLICT);
    }
}
