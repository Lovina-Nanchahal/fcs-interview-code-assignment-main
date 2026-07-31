package com.fulfilment.application.monolith.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class StoreNotFoundException extends WebApplicationException {
    public StoreNotFoundException(Long storeId) {
        super("Store not found: " + storeId, Response.Status.NOT_FOUND);
    }
}
