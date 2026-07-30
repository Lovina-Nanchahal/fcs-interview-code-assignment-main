package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import jakarta.enterprise.event.Event;

import java.util.List;
import org.jboss.logging.Logger;

@ApplicationScoped
public class StoreService {

    private static final Logger LOGGER = Logger.getLogger(StoreService.class);

    @Inject
    StoreRepository repository;

    @Inject
    Event<StoreEvent> event;

    public List<Store> getAll() {
        LOGGER.info("Retrieving all stores");

        List<Store> stores = repository.listAllSorted();

        LOGGER.infof("Retrieved %d stores", stores.size());

        return stores;
    }

    public Store get(Long id) {
        LOGGER.infof("Retrieving store id=%s", id);

        return repository.findByIdOptional(id)
                .orElseThrow(() -> {
                    LOGGER.warnf("Store not found. id=%s", id);
                    return new WebApplicationException(
                            "Store with id of " + id + " does not exist.",
                            Status.NOT_FOUND);
                });
    }

    @Transactional
    public Store create(Store store) {
        LOGGER.infof(
                "Creating store. name=%s",
                store != null ? store.name : null);

        if (store == null) {
            LOGGER.warn("Store creation rejected because request body was empty");

            throw new WebApplicationException(
                    "Store request body is required.",
                    Status.BAD_REQUEST);
        }

        if (store.id != null) {
            LOGGER.warnf(
                    "Store creation rejected because id was supplied. id=%s",
                    store.id);

            throw new WebApplicationException(
                    "Id was invalidly set on request.",
                    422);
        }

        repository.persist(store);

        LOGGER.infof(
                "Store persisted. id=%s, name=%s",
                store.id,
                store.name);

        event.fire(new StoreEvent(store, StoreEvent.Type.CREATED));

        LOGGER.infof(
                "CREATED event fired for store id=%s",
                store.id);

        return store;
    }

    @Transactional
    public Store update(Long id, Store updatedStore) {
        LOGGER.infof("Updating store id=%s", id);

        if (updatedStore.name == null) {
            LOGGER.warnf("Store update rejected because name was not provided. id=%s", id);

            throw new WebApplicationException("Store Name was not set on request.", 422);
        }

        Store entity = get(id);

        entity.name = updatedStore.name;
        entity.quantityProductsInStock = updatedStore.quantityProductsInStock;

        LOGGER.infof("Store updated. id=%s, name=%s", entity.id, entity.name);

        event.fire(new StoreEvent(entity, StoreEvent.Type.UPDATED));

        LOGGER.infof("UPDATED event fired for store id=%s", entity.id);

        return entity;
    }

    @Transactional
    public Store patch(Long id, Store updatedStore) {
        LOGGER.infof("Patching store id=%s", id);

        if (updatedStore.name == null) {
            LOGGER.warnf("Store patch rejected because name was not provided. id=%s", id);

            throw new WebApplicationException("Store Name was not set on request.", 422);
        }

        Store entity = get(id);

        if (updatedStore.name != null) {
            LOGGER.infof("Updating name for store id=%s to '%s'", id, updatedStore.name);

            entity.name = updatedStore.name;
        }

        if (updatedStore.quantityProductsInStock != 0) {
            LOGGER.infof("Updating stock quantity for store id=%s to %s", id, updatedStore.quantityProductsInStock);

            entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
        }

        event.fire(new StoreEvent(entity, StoreEvent.Type.UPDATED));

        LOGGER.infof("UPDATED event fired for store id=%s", entity.id);

        return entity;
    }

    @Transactional
    public void delete(Long id) {
        LOGGER.infof("Deleting store id=%s", id);

        Store entity = get(id);

        repository.delete(entity);

        LOGGER.infof("Store deleted successfully. id=%s", id);
    }
}