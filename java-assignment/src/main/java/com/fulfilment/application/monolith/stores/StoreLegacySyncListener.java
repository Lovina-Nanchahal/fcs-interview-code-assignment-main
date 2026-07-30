package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class StoreLegacySyncListener {

    private static final Logger LOGGER = Logger.getLogger(StoreLegacySyncListener.class);

    @Inject
    LegacyStoreManagerGateway legacy;

    public void onStoreEvent(
            @Observes(during = TransactionPhase.AFTER_SUCCESS) StoreEvent event) {

        LOGGER.infof(
                "Received StoreEvent. type=%s, store=%s",
                event.type(),
                event.store());

        switch (event.type()) {
            case CREATED -> {
                LOGGER.info("Synchronizing store creation to legacy system");

                legacy.createStoreOnLegacySystem(event.store());

                LOGGER.info("Store creation synchronized successfully");
            }

            case UPDATED -> {
                LOGGER.info("Synchronizing store update to legacy system");

                legacy.updateStoreOnLegacySystem(event.store());

                LOGGER.info("Store update synchronized successfully");
            }
        }
    }
}