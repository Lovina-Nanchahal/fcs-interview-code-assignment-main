package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.exception.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.jboss.logging.Logger;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  private static final Logger LOGGER = Logger.getLogger(WarehouseResourceImpl.class);

  private final CreateWarehouseOperation createWarehouseOperation;
  private final ReplaceWarehouseOperation replaceWarehouseOperation;
  private final ArchiveWarehouseOperation archiveWarehouseOperation;
  private final WarehouseStore warehouseStore;

  @Inject
  public WarehouseResourceImpl(CreateWarehouseOperation createWarehouseOperation,
                               ReplaceWarehouseOperation replaceWarehouseOperation,
                               ArchiveWarehouseOperation archiveWarehouseOperation,
                               WarehouseStore warehouseStore) {
    this.createWarehouseOperation = createWarehouseOperation;
    this.replaceWarehouseOperation = replaceWarehouseOperation;
    this.archiveWarehouseOperation = archiveWarehouseOperation;
    this.warehouseStore = warehouseStore;
  }

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    LOGGER.info("GET /warehouses - list all");

    return warehouseStore.getAll().stream().map(this::toApiWarehouse).toList();
  }

  @Override
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
    LOGGER.infof("POST /warehouses - create unit code=%s", data.getBusinessUnitCode());

    createWarehouseOperation.create(toDomainWarehouse(data));

    LOGGER.infof("Warehouse created unit code=%s", data.getBusinessUnitCode());

    return data;
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
    LOGGER.infof("GET /warehouses/%s", id);

    var warehouse = warehouseStore.findByBusinessUnitCode(id);

    if (warehouse == null) {
      LOGGER.warnf("Warehouse not found id=%s", id);
      throw new WarehouseNotFoundException(id);
    }

    LOGGER.infof("Warehouse found id=%s", id);

    return toApiWarehouse(warehouse);
  }

  @Override
  public void archiveAWarehouseUnitByID(String id) {
    LOGGER.infof("DELETE /warehouses/%s (archive)", id);

    var warehouse = warehouseStore.findByBusinessUnitCode(id);

    if (warehouse == null) {
      LOGGER.warnf("Warehouse not found for archive id=%s", id);
      throw new WarehouseNotFoundException(id);
    }

    archiveWarehouseOperation.archive(warehouse);

    LOGGER.infof("Warehouse archived id=%s", id);
  }

  @Override
  public Warehouse replaceTheCurrentActiveWarehouse(
          String businessUnitCode, @NotNull Warehouse data) {

    LOGGER.infof("PUT /warehouses/%s", businessUnitCode);

    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse
            warehouse = toDomainWarehouse(data);

    // Ensure the business unit code comes from the URL
    warehouse.businessUnitCode = businessUnitCode;

    replaceWarehouseOperation.replace(warehouse);

    LOGGER.infof("Warehouse replaced businessUnitCode=%s", businessUnitCode);

    return data;
  }

  // ---------------- MAPPERS ----------------

  private Warehouse toApiWarehouse(com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
    var response = new Warehouse();
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);
    return response;
  }

  private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse toDomainWarehouse(Warehouse request) {
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    warehouse.businessUnitCode = request.getBusinessUnitCode();
    warehouse.location = request.getLocation();
    warehouse.capacity = request.getCapacity();
    warehouse.stock = request.getStock();
    return warehouse;
  }
}