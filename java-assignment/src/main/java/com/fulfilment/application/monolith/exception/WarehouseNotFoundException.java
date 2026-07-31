package com.fulfilment.application.monolith.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class WarehouseNotFoundException extends WebApplicationException {
    public WarehouseNotFoundException(String businessUnitCode) {
        super("Warehouse not found: " + businessUnitCode, Response.Status.NOT_FOUND);
    }
}
