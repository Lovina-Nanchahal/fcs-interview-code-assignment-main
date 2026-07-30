package com.fulfilment.application.monolith.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class LocationNotFoundException extends WebApplicationException {

    public LocationNotFoundException(String locationIdentifier) {
        super("Location not found: " + locationIdentifier, Response.Status.NOT_FOUND);
    }
}
