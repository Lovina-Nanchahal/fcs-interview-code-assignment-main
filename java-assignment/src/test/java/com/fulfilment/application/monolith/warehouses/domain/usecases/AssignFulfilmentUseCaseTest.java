package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.exception.MaximumFulfilmentWarehousesExceededException;
import com.fulfilment.application.monolith.exception.MaximumProductTypesPerWarehouseExceededException;
import com.fulfilment.application.monolith.exception.MaximumStoreWarehousesExceededException;
import com.fulfilment.application.monolith.exception.ProductNotFoundException;
import com.fulfilment.application.monolith.exception.StoreNotFoundException;
import com.fulfilment.application.monolith.exception.WarehouseNotFoundException;
import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.StoreRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.domain.models.FulfilmentAssignment;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.FulfilmentAssignmentResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AssignFulfilmentUseCaseTest {

    private FulfilmentAssignmentResolver resolver;
    private StoreRepository storeRepository;
    private ProductRepository productRepository;
    private WarehouseStore warehouseStore;

    private AssignFulfilmentUseCase useCase;

    @BeforeEach
    void setUp() {

        resolver = Mockito.mock(FulfilmentAssignmentResolver.class);
        storeRepository = Mockito.mock(StoreRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        warehouseStore = Mockito.mock(WarehouseStore.class);

        useCase = new AssignFulfilmentUseCase(
                resolver,
                storeRepository,
                productRepository,
                warehouseStore);

        when(storeRepository.findById(any())).thenReturn(new Store());
        when(productRepository.findById(any())).thenReturn(new Product());

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "MWH.001";

        when(warehouseStore.findByBusinessUnitCode(any()))
                .thenReturn(warehouse);

        when(resolver.getAssignments())
                .thenReturn(new ArrayList<>());
    }

    @Test
    void shouldAssignFulfilment() {

        useCase.assign(1L, 1L, "MWH.001");

        verify(resolver).add(any(FulfilmentAssignment.class));
    }

    @Test
    void shouldIgnoreDuplicateAssignment() {

        List<FulfilmentAssignment> assignments = List.of(
                new FulfilmentAssignment(1L, 1L, "MWH.001"));

        when(resolver.getAssignments())
                .thenReturn(assignments);

        useCase.assign(1L, 1L, "MWH.001");

        verify(resolver, never()).add(any());
    }

    @Test
    void shouldThrowWhenStoreDoesNotExist() {

        when(storeRepository.findById(1L))
                .thenReturn(null);

        assertThrows(
                StoreNotFoundException.class,
                () -> useCase.assign(1L, 1L, "MWH.001"));
    }

    @Test
    void shouldThrowWhenProductDoesNotExist() {

        when(productRepository.findById(1L))
                .thenReturn(null);

        assertThrows(
                ProductNotFoundException.class,
                () -> useCase.assign(1L, 1L, "MWH.001"));
    }

    @Test
    void shouldThrowWhenWarehouseDoesNotExist() {

        when(warehouseStore.findByBusinessUnitCode("MWH.001"))
                .thenReturn(null);

        assertThrows(
                WarehouseNotFoundException.class,
                () -> useCase.assign(1L, 1L, "MWH.001"));
    }

    @Test
    void shouldThrowWhenMaximumWarehousesPerProductPerStoreExceeded() {

        List<FulfilmentAssignment> assignments = List.of(
                new FulfilmentAssignment(1L, 1L, "MWH.001"),
                new FulfilmentAssignment(1L, 1L, "MWH.002"));

        when(resolver.getAssignments())
                .thenReturn(assignments);

        assertThrows(
                MaximumFulfilmentWarehousesExceededException.class,
                () -> useCase.assign(1L, 1L, "MWH.003"));
    }

    @Test
    void shouldThrowWhenMaximumWarehousesPerStoreExceeded() {

        List<FulfilmentAssignment> assignments = List.of(
                new FulfilmentAssignment(1L, 1L, "MWH.001"),
                new FulfilmentAssignment(1L, 2L, "MWH.002"),
                new FulfilmentAssignment(1L, 3L, "MWH.003"));

        when(resolver.getAssignments())
                .thenReturn(assignments);

        assertThrows(
                MaximumStoreWarehousesExceededException.class,
                () -> useCase.assign(1L, 4L, "MWH.004"));
    }

    @Test
    void shouldThrowWhenMaximumProductTypesPerWarehouseExceeded() {

        List<FulfilmentAssignment> assignments = List.of(
                new FulfilmentAssignment(1L, 1L, "MWH.001"),
                new FulfilmentAssignment(1L, 2L, "MWH.001"),
                new FulfilmentAssignment(1L, 3L, "MWH.001"),
                new FulfilmentAssignment(2L, 4L, "MWH.001"),
                new FulfilmentAssignment(2L, 5L, "MWH.001"));

        when(resolver.getAssignments())
                .thenReturn(assignments);

        assertThrows(
                MaximumProductTypesPerWarehouseExceededException.class,
                () -> useCase.assign(2L, 6L, "MWH.001"));
    }
}