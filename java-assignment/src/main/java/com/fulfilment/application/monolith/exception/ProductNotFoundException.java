package com.fulfilment.application.monolith.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class ProductNotFoundException extends WebApplicationException {
    public ProductNotFoundException(Long productId) {
        super("Product not found: " + productId, Response.Status.NOT_FOUND);
    }
}
