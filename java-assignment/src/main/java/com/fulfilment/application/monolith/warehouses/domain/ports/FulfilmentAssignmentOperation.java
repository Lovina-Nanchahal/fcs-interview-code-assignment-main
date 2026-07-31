package com.fulfilment.application.monolith.warehouses.domain.ports;

public interface FulfilmentAssignmentOperation {
  void assign(Long storeId, Long productId, String warehouseBusinessUnitCode);
}
