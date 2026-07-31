package com.fulfilment.application.monolith.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class StockMismatchException extends WebApplicationException {
    public StockMismatchException(int oldStock, int newStock) {
        super("Stock mismatch. old=" + oldStock + ", new=" + newStock, Response.Status.CONFLICT);
    }

}
