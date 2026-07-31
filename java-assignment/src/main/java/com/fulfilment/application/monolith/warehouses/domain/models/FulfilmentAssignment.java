package com.fulfilment.application.monolith.warehouses.domain.models;

public record FulfilmentAssignment(
        Long storeId,
        Long productId,
        String warehouseBusinessUnitCode
) {}
