package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.exception.CapacityExceedsLocationLimitException;
import com.fulfilment.application.monolith.exception.StockMismatchException;
import com.fulfilment.application.monolith.exception.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {
  private static final Logger LOGGER =
          Logger.getLogger(ReplaceWarehouseUseCase.class);

  private final WarehouseStore warehouseStore;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void replace(Warehouse newWarehouse) {
    String businessUnitCode = newWarehouse.businessUnitCode;

    LOGGER.infof(
            "Replacing warehouse businessUnitCode=%s",
            businessUnitCode);

    Warehouse currentWarehouse =
            warehouseStore.findByBusinessUnitCode(
                    businessUnitCode);

    if (currentWarehouse == null) {
      LOGGER.warnf(
              "Warehouse not found businessUnitCode=%s",
              businessUnitCode);

      throw new WarehouseNotFoundException(
              businessUnitCode);
    }

    // New warehouse must be able to accommodate existing stock
    if (newWarehouse.capacity < currentWarehouse.stock) {
      throw new CapacityExceedsLocationLimitException(
              currentWarehouse.stock,
              newWarehouse.capacity);
    }

    // Stock must remain unchanged during replacement
    if (!newWarehouse.stock.equals(currentWarehouse.stock)) {
      throw new StockMismatchException(
              currentWarehouse.stock,
              newWarehouse.stock);
    }

    LOGGER.info("Archiving existing warehouse");

    currentWarehouse.archivedAt = LocalDateTime.now();

    warehouseStore.update(currentWarehouse);

    LOGGER.info("Creating replacement warehouse");

    warehouseStore.create(newWarehouse);

    LOGGER.infof(
            "Warehouse replaced successfully businessUnitCode=%s",
            businessUnitCode);
  }
}
