package com.fulfilment.application.monolith.stores;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StoreLegacySyncListenerTest {

    @Mock
    LegacyStoreManagerGateway legacyStoreManagerGateway;

    @InjectMocks
    StoreLegacySyncListener listener;

    @Test
    void shouldSyncWhenStoreCreated() {

        Store store = new Store();
        store.id = 1L;
        store.name = "TEST";

        StoreEvent event = new StoreEvent(store, StoreEvent.Type.CREATED);

        listener.onStoreEvent(event);

        verify(legacyStoreManagerGateway).createStoreOnLegacySystem(store);
    }

    @Test
    void shouldSyncWhenStoreUpdated() {

        Store store = new Store();
        store.id = 1L;

        StoreEvent event = new StoreEvent(store, StoreEvent.Type.UPDATED);

        listener.onStoreEvent(event);

        verify(legacyStoreManagerGateway).updateStoreOnLegacySystem(store);
    }

}