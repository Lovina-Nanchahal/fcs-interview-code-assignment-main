package com.fulfilment.application.monolith.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class WarehouseAlreadyExistsException extends WebApplicationException {
    public WarehouseAlreadyExistsException(String businessUnitCode) {
        super("Business unit code already exists: " + businessUnitCode, Response.Status.CONFLICT);
    }
}
