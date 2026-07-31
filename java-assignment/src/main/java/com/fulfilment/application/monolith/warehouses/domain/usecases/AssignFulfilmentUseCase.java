package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.exception.*;
import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.StoreRepository;
import com.fulfilment.application.monolith.warehouses.domain.ports.FulfilmentAssignmentResolver;
import com.fulfilment.application.monolith.warehouses.domain.models.FulfilmentAssignment;
import com.fulfilment.application.monolith.warehouses.domain.ports.FulfilmentAssignmentOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AssignFulfilmentUseCase implements FulfilmentAssignmentOperation {
    private static final Logger LOGGER = Logger.getLogger(AssignFulfilmentUseCase.class);

    private final FulfilmentAssignmentResolver resolver;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final WarehouseStore warehouseStore;

    public AssignFulfilmentUseCase(
        FulfilmentAssignmentResolver resolver, 
        StoreRepository storeRepository,
        ProductRepository productRepository,
        WarehouseStore warehouseStore) 
        {
        this.resolver = resolver;
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
        this.warehouseStore = warehouseStore;
    }

    @Override
    public void assign(Long storeId, Long productId, String warehouseBusinessUnitCode) {
        LOGGER.infof("AssignFulfilment invoked storeId=%s productId=%s warehouseBusinessUnitCode=%s", storeId, productId, warehouseBusinessUnitCode);

        if (storeRepository.findById(storeId) == null) {
            throw new StoreNotFoundException(storeId);
        }

        if (productRepository.findById(productId) == null) {
            throw new ProductNotFoundException(productId);
        }

        if (warehouseStore.findByBusinessUnitCode(warehouseBusinessUnitCode) == null) {
            throw new WarehouseNotFoundException(warehouseBusinessUnitCode);
        }

        LOGGER.info("Store, product and warehouse validated");

        List<FulfilmentAssignment> assignments = resolver.getAssignments();

        // Prevent duplicates
        boolean exists =
                assignments.stream()
                        .anyMatch(a ->
                                a.storeId().equals(storeId)
                                    && a.productId().equals(productId)
                                        && a.warehouseBusinessUnitCode().equals(warehouseBusinessUnitCode));

        if (exists) {
            LOGGER.info("Assignment already exists, skipping");
            return;
        }

        // Constraint 1. Each `Product` can be fulfilled by a maximum of 2 different `Warehouses` per `Store`
        long warehousesForProductInStore =
                assignments.stream()
                        .filter(a ->
                                a.storeId().equals(storeId)
                                        && a.productId().equals(productId))
                        .map(FulfilmentAssignment::warehouseBusinessUnitCode)
                        .distinct()
                        .count();
        LOGGER.infof("Warehouses for product-store=%d", warehousesForProductInStore);

        if (warehousesForProductInStore >= 2) {
            LOGGER.warn("Constraint violated: max 2 warehouses per product per store");
            throw new MaximumFulfilmentWarehousesExceededException(storeId, productId);
        }

        // Constraint 2. Each `Store` can be fulfilled by a maximum of 3 different `Warehouses`
        long warehousesForStore =
                assignments.stream()
                        .filter(a -> a.storeId().equals(storeId))
                        .map(FulfilmentAssignment::warehouseBusinessUnitCode)
                        .distinct()
                        .count();
        LOGGER.infof("Warehouses for store=%d", warehousesForStore);

        if (warehousesForStore >= 3) {
            LOGGER.warn("Constraint violated: max 3 warehouses per store");
            throw new MaximumStoreWarehousesExceededException(storeId);
        }

        // Constraint 3. Each `Warehouse` can store maximally 5 types of `Products`
        long productsForWarehouse =
                assignments.stream()
                        .filter(a -> a.warehouseBusinessUnitCode().equals(warehouseBusinessUnitCode))
                        .map(FulfilmentAssignment::productId)
                        .distinct()
                        .count();
        LOGGER.infof("Products for warehouse=%d", productsForWarehouse);

        if (productsForWarehouse >= 5) {
            LOGGER.warn("Constraint violated: max 5 products per warehouse");
            throw new MaximumProductTypesPerWarehouseExceededException(warehouseBusinessUnitCode);
        }

        resolver.add(new FulfilmentAssignment(storeId, productId, warehouseBusinessUnitCode));
        LOGGER.info("Assignment created successfully");
    }
}
