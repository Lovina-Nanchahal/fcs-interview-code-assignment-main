package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.exception.CapacityExceedsLocationLimitException;
import com.fulfilment.application.monolith.exception.MaximumWarehousesReachedException;
import com.fulfilment.application.monolith.exception.StockExceedsCapacityException;
import com.fulfilment.application.monolith.exception.WarehouseAlreadyExistsException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateWarehouseUseCaseTest {

    private WarehouseStore warehouseStore;
    private LocationResolver locationResolver;

    private CreateWarehouseUseCase useCase;


    @BeforeEach
    void setup() {

        warehouseStore = mock(WarehouseStore.class);
        locationResolver = mock(LocationResolver.class);

        useCase = new CreateWarehouseUseCase(
                warehouseStore,
                locationResolver);
    }


    private Warehouse validWarehouse() {

        Warehouse warehouse = new Warehouse();

        warehouse.businessUnitCode = "MWH.TEST";
        warehouse.location = "ZWOLLE-001";
        warehouse.capacity = 20;
        warehouse.stock = 10;

        return warehouse;
    }


    private Location validLocation() {

        return new Location(
                "ZWOLLE-001",
                5,
                100);
    }


    @Test
    void shouldCreateWarehouseSuccessfully() {

        Warehouse warehouse = validWarehouse();

        when(warehouseStore.findByBusinessUnitCode(
                warehouse.businessUnitCode))
                .thenReturn(null);

        when(locationResolver.resolveByIdentifier(
                warehouse.location))
                .thenReturn(validLocation());

        when(warehouseStore.countByLocation(
                warehouse.location))
                .thenReturn(0L);


        useCase.create(warehouse);


        verify(warehouseStore)
                .create(warehouse);
    }


    @Test
    void shouldRejectDuplicateBusinessUnitCode() {

        Warehouse warehouse = validWarehouse();


        when(warehouseStore.findByBusinessUnitCode(
                warehouse.businessUnitCode))
                .thenReturn(warehouse);


        assertThrows(
                WarehouseAlreadyExistsException.class,
                () -> useCase.create(warehouse));


        verify(warehouseStore, never())
                .create(any());
    }


    @Test
    void shouldRejectCapacityAboveLocationLimit() {

        Warehouse warehouse = validWarehouse();

        warehouse.capacity = 200;


        when(warehouseStore.findByBusinessUnitCode(
                warehouse.businessUnitCode))
                .thenReturn(null);

        when(locationResolver.resolveByIdentifier(
                warehouse.location))
                .thenReturn(validLocation());


        assertThrows(
                CapacityExceedsLocationLimitException.class,
                () -> useCase.create(warehouse));


        verify(warehouseStore, never())
                .create(any());
    }


    @Test
    void shouldRejectStockGreaterThanCapacity() {

        Warehouse warehouse = validWarehouse();

        warehouse.stock = 50;


        when(warehouseStore.findByBusinessUnitCode(
                warehouse.businessUnitCode))
                .thenReturn(null);

        when(locationResolver.resolveByIdentifier(
                warehouse.location))
                .thenReturn(validLocation());


        assertThrows(
                StockExceedsCapacityException.class,
                () -> useCase.create(warehouse));


        verify(warehouseStore, never())
                .create(any());
    }


    @Test
    void shouldRejectMaximumWarehousesReached() {

        Warehouse warehouse = validWarehouse();


        Location location = new Location(
                "ZWOLLE-001",
                1,
                100);


        when(warehouseStore.findByBusinessUnitCode(
                warehouse.businessUnitCode))
                .thenReturn(null);

        when(locationResolver.resolveByIdentifier(
                warehouse.location))
                .thenReturn(location);

        when(warehouseStore.countByLocation(
                warehouse.location))
                .thenReturn(1L);


        assertThrows(
                MaximumWarehousesReachedException.class,
                () -> useCase.create(warehouse));


        verify(warehouseStore, never())
                .create(any());
    }


    @Test
    void shouldRejectMissingBusinessUnitCode() {

        Warehouse warehouse = validWarehouse();

        warehouse.businessUnitCode = null;


        assertThrows(
                WebApplicationException.class,
                () -> useCase.create(warehouse));


        verifyNoInteractions(warehouseStore);
    }


    @Test
    void shouldRejectMissingLocation() {

        Warehouse warehouse = validWarehouse();

        warehouse.location = null;


        assertThrows(
                WebApplicationException.class,
                () -> useCase.create(warehouse));


        verifyNoInteractions(warehouseStore);
    }


    @Test
    void shouldRejectZeroCapacity() {

        Warehouse warehouse = validWarehouse();

        warehouse.capacity = 0;


        assertThrows(
                WebApplicationException.class,
                () -> useCase.create(warehouse));


        verifyNoInteractions(warehouseStore);
    }


    @Test
    void shouldRejectNegativeStock() {

        Warehouse warehouse = validWarehouse();

        warehouse.stock = -1;


        assertThrows(
                WebApplicationException.class,
                () -> useCase.create(warehouse));


        verifyNoInteractions(warehouseStore);
    }
}