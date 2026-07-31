package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;


class ArchiveWarehouseUseCaseTest {

    private WarehouseStore warehouseStore;

    private ArchiveWarehouseUseCase useCase;


    @BeforeEach
    void setup() {

        warehouseStore = mock(WarehouseStore.class);

        useCase = new ArchiveWarehouseUseCase(
                warehouseStore);
    }


    @Test
    void shouldArchiveWarehouseSuccessfully() {

        Warehouse warehouse = new Warehouse();

        warehouse.businessUnitCode = "MWH.001";
        warehouse.location = "ZWOLLE-001";
        warehouse.capacity = 100;
        warehouse.stock = 20;


        useCase.archive(warehouse);


        assertNotNull(warehouse.archivedAt);


        verify(warehouseStore)
                .update(warehouse);
    }
}