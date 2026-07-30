package com.fulfilment.application.monolith.stores;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @Mock
    StoreRepository repository;

    @Mock
    jakarta.enterprise.event.Event<StoreEvent> event;

    @InjectMocks
    StoreService service;

    @Test
    void shouldReturnAllStores() {

        List<Store> stores = List.of(new Store(), new Store());

        when(repository.listAllSorted()).thenReturn(stores);

        List<Store> result = service.getAll();

        assertEquals(2, result.size());
        verify(repository).listAllSorted();
    }

    @Test
    void shouldReturnStoreById() {

        Store store = new Store();
        store.id = 1L;

        when(repository.findByIdOptional(1L))
                .thenReturn(Optional.of(store));

        Store result = service.get(1L);

        assertEquals(1L, result.id);
    }

    @Test
    void shouldThrowWhenStoreNotFound() {

        when(repository.findByIdOptional(1L))
                .thenReturn(Optional.empty());

        WebApplicationException ex =
                assertThrows(WebApplicationException.class,
                        () -> service.get(1L));

        assertEquals(Status.NOT_FOUND.getStatusCode(),
                ex.getResponse().getStatus());
    }

    @Test
    void shouldCreateStore() {

        Store store = new Store();
        store.name = "TEST";

        Store result = service.create(store);

        verify(repository).persist(store);
        verify(event).fire(any(StoreEvent.class));

        assertEquals("TEST", result.name);
    }

    @Test
    void shouldThrowWhenIdAlreadySetOnCreate() {

        Store store = new Store();
        store.id = 10L;

        WebApplicationException ex =
                assertThrows(WebApplicationException.class,
                        () -> service.create(store));

        assertEquals(422, ex.getResponse().getStatus());

        verifyNoInteractions(repository);
        verifyNoInteractions(event);
    }

    @Test
    void shouldUpdateStore() {

        Store existing = new Store();
        existing.id = 1L;
        existing.name = "OLD";
        existing.quantityProductsInStock = 5;

        Store updated = new Store();
        updated.name = "NEW";
        updated.quantityProductsInStock = 10;

        when(repository.findByIdOptional(1L))
                .thenReturn(Optional.of(existing));

        Store result = service.update(1L, updated);

        assertEquals("NEW", result.name);
        assertEquals(10, result.quantityProductsInStock);

        verify(event).fire(any(StoreEvent.class));
    }

    @Test
    void shouldThrowWhenUpdateNameIsNull() {

        Store updated = new Store();
        updated.name = null;

        WebApplicationException ex =
                assertThrows(WebApplicationException.class,
                        () -> service.update(1L, updated));

        assertEquals(422, ex.getResponse().getStatus());

        verifyNoInteractions(repository);
    }

    @Test
    void shouldPatchStore() {

        Store existing = new Store();
        existing.id = 1L;
        existing.name = "OLD";
        existing.quantityProductsInStock = 5;

        Store patch = new Store();
        patch.name = "PATCHED";
        patch.quantityProductsInStock = 20;

        when(repository.findByIdOptional(1L))
                .thenReturn(Optional.of(existing));

        Store result = service.patch(1L, patch);

        assertEquals("PATCHED", result.name);
        assertEquals(20, result.quantityProductsInStock);

        verify(event).fire(any(StoreEvent.class));
    }

    @Test
    void shouldDeleteStore() {

        Store store = new Store();
        store.id = 1L;

        when(repository.findByIdOptional(1L))
                .thenReturn(Optional.of(store));

        service.delete(1L);

        verify(repository).delete(store);
    }
}