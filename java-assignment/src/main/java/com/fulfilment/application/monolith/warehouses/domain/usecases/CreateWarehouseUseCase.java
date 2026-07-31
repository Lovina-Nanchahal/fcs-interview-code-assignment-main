package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.exception.CapacityExceedsLocationLimitException;
import com.fulfilment.application.monolith.exception.MaximumWarehousesReachedException;
import com.fulfilment.application.monolith.exception.StockExceedsCapacityException;
import com.fulfilment.application.monolith.exception.WarehouseAlreadyExistsException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private static final Logger LOGGER = Logger.getLogger(CreateWarehouseUseCase.class);

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void create(Warehouse warehouse) {

    LOGGER.infof("CreateWarehouse invoked businessUnitCode=%s", warehouse != null ? warehouse.businessUnitCode : null);

    if (warehouse == null) {
      LOGGER.warn("Warehouse is null");
      throw new IllegalArgumentException("Warehouse cannot be null");
    }

    validateRequiredFields(warehouse);
    validateBusinessUnitCode(warehouse);

    // Resolve location
    LOGGER.infof("Resolving location=%s", warehouse.location);
    Location location = locationResolver.resolveByIdentifier(warehouse.location);

    LOGGER.infof("Location resolved identifier=%s", location.identification);

    validateLocationConstraints(warehouse, location);

    LOGGER.info("All validations passed, creating warehouse");

    // Persist, if all above validations are ok, create the warehouse
    warehouseStore.create(warehouse);

    LOGGER.infof("Warehouse created successfully businessUnitCode=%s", warehouse.businessUnitCode);
  }

  private void validateRequiredFields(Warehouse warehouse) {

    if (warehouse.businessUnitCode == null || warehouse.businessUnitCode.isBlank()) {
      throw new WebApplicationException(
              "Business unit code is required.",
              Status.BAD_REQUEST);
    }

    if (warehouse.location == null || warehouse.location.isBlank()) {
      throw new WebApplicationException(
              "Location is required.",
              Status.BAD_REQUEST);
    }

    if (warehouse.capacity <= 0) {
      throw new WebApplicationException(
              "Capacity must be greater than zero.",
              Status.BAD_REQUEST);
    }

    if (warehouse.stock < 0) {
      throw new WebApplicationException(
              "Stock cannot be negative.",
              Status.BAD_REQUEST);
    }
  }

  private void validateBusinessUnitCode(Warehouse warehouse) {

    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
      LOGGER.warnf("Duplicate businessUnitCode detected=%s", warehouse.businessUnitCode);

      throw new WarehouseAlreadyExistsException(warehouse.businessUnitCode);
    }
  }

  private void validateLocationConstraints(
          Warehouse warehouse,
          Location location) {

    long countAtLocation = warehouseStore.countByLocation(location.identification);

    LOGGER.infof("Warehouses at location=%s count=%d", location.identification, countAtLocation);

    if (countAtLocation >= location.maxNumberOfWarehouses) {
      LOGGER.warn("Max warehouses per location exceeded");
      throw new MaximumWarehousesReachedException(location.identification);
    }

    if (warehouse.capacity > location.maxCapacity) {
      LOGGER.warn("Capacity exceeds location limit");
      throw new CapacityExceedsLocationLimitException(warehouse.capacity, location.maxCapacity);
    }

    if (warehouse.stock > warehouse.capacity) {
      LOGGER.warn("Stock exceeds warehouse capacity");
      throw new StockExceedsCapacityException(warehouse.stock, warehouse.capacity);
    }
    LOGGER.info("Location constraints validated successfully");
  }
}