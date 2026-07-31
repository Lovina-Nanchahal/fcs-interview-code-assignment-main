package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import java.time.LocalDateTime;

@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {
  private static final Logger LOGGER = Logger.getLogger(ArchiveWarehouseUseCase.class);

  private final WarehouseStore warehouseStore;

  public ArchiveWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void archive(Warehouse warehouse) {
    LOGGER.info("ArchiveWarehouseUseCase.archive invoked");
    warehouse.archivedAt = LocalDateTime.now();

    warehouseStore.update(warehouse);
    LOGGER.infof("Warehouse archived successfully businessUnitCode=%s", warehouse.businessUnitCode);
  }
}
