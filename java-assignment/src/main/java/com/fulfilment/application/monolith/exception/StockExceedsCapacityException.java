package com.fulfilment.application.monolith.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class StockExceedsCapacityException extends WebApplicationException {

    public StockExceedsCapacityException(int stock, int capacity) {

        super(String.format("Warehouse stock (%d) cannot exceed capacity (%d)", stock, capacity), Response.Status.CONFLICT);
    }
}
