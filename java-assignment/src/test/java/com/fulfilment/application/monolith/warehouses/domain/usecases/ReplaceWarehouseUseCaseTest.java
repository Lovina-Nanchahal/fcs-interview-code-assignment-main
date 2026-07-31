package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.exception.CapacityExceedsLocationLimitException;
import com.fulfilment.application.monolith.exception.StockMismatchException;
import com.fulfilment.application.monolith.exception.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class ReplaceWarehouseUseCaseTest {

    private WarehouseStore warehouseStore;

    private ReplaceWarehouseUseCase useCase;


    @BeforeEach
    void setup() {

        warehouseStore = mock(WarehouseStore.class);

        useCase = new ReplaceWarehouseUseCase(
                warehouseStore);
    }


    @Test
    void shouldReplaceWarehouseSuccessfully() {

        Warehouse currentWarehouse = new Warehouse();

        currentWarehouse.businessUnitCode = "MWH.001";
        currentWarehouse.location = "ZWOLLE-001";
        currentWarehouse.capacity = 50;
        currentWarehouse.stock = 20;


        Warehouse newWarehouse = new Warehouse();

        newWarehouse.businessUnitCode = "MWH.001";
        newWarehouse.location = "AMSTERDAM-001";
        newWarehouse.capacity = 100;
        newWarehouse.stock = 20;


        when(warehouseStore.findByBusinessUnitCode("MWH.001"))
                .thenReturn(currentWarehouse);


        useCase.replace(newWarehouse);


        verify(warehouseStore)
                .findByBusinessUnitCode("MWH.001");

        verify(warehouseStore)
                .update(currentWarehouse);

        verify(warehouseStore)
                .create(newWarehouse);
    }


    @Test
    void shouldThrowExceptionWhenWarehouseDoesNotExist() {

        Warehouse newWarehouse = new Warehouse();

        newWarehouse.businessUnitCode = "UNKNOWN";


        when(warehouseStore.findByBusinessUnitCode("UNKNOWN"))
                .thenReturn(null);


        assertThrows(
                WarehouseNotFoundException.class,
                () -> useCase.replace(newWarehouse));


        verify(warehouseStore, never())
                .update(any());

        verify(warehouseStore, never())
                .create(any());
    }


    @Test
    void shouldRejectReplacementWhenCapacityIsLessThanExistingStock() {

        Warehouse currentWarehouse = new Warehouse();

        currentWarehouse.businessUnitCode = "MWH.001";
        currentWarehouse.stock = 80;


        Warehouse newWarehouse = new Warehouse();

        newWarehouse.businessUnitCode = "MWH.001";
        newWarehouse.capacity = 50;
        newWarehouse.stock = 80;


        when(warehouseStore.findByBusinessUnitCode("MWH.001"))
                .thenReturn(currentWarehouse);


        assertThrows(
                CapacityExceedsLocationLimitException.class,
                () -> useCase.replace(newWarehouse));


        verify(warehouseStore, never())
                .update(any());

        verify(warehouseStore, never())
                .create(any());
    }


    @Test
    void shouldRejectReplacementWhenStockChanges() {

        Warehouse currentWarehouse = new Warehouse();

        currentWarehouse.businessUnitCode = "MWH.001";
        currentWarehouse.stock = 20;


        Warehouse newWarehouse = new Warehouse();

        newWarehouse.businessUnitCode = "MWH.001";
        newWarehouse.capacity = 100;
        newWarehouse.stock = 30;


        when(warehouseStore.findByBusinessUnitCode("MWH.001"))
                .thenReturn(currentWarehouse);


        assertThrows(
                StockMismatchException.class,
                () -> useCase.replace(newWarehouse));


        verify(warehouseStore, never())
                .update(any());

        verify(warehouseStore, never())
                .create(any());
    }
}