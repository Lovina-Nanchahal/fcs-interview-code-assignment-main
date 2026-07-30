package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.jboss.logging.Logger;

@Path("store")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class StoreResource {

  private static final Logger LOGGER = Logger.getLogger(StoreResource.class);

  @Inject
  StoreService storeService;

  @GET
  public List<Store> get() {
    LOGGER.info("GET /stores");
    return storeService.getAll();
  }

  @GET
  @Path("{id}")
  public Store getSingle(@PathParam("id") Long id) {
    LOGGER.infof("GET /stores/%s", id);
    return storeService.get(id);
  }

  @POST
  public Response create(Store store) {
    LOGGER.infof("POST /stores (name=%s)", store != null ? store.name : null);

    Store created = storeService.create(store);

    LOGGER.infof("Store created with id=%s", created.id);

    return Response.status(201).entity(created).build();
  }

  @PUT
  @Path("{id}")
  public Store update(@PathParam("id") Long id, Store updatedStore) {
    LOGGER.infof("PUT /stores/%s", id);

    Store updated = storeService.update(id, updatedStore);

    LOGGER.infof("Store updated id=%s", id);

    return updated;
  }

  @PATCH
  @Path("{id}")
  public Store patch(@PathParam("id") Long id, Store updatedStore) {

    LOGGER.infof("PATCH /stores/%s", id);

    Store patched = storeService.patch(id, updatedStore);

    LOGGER.infof("Store patched id=%s", id);

    return patched;
  }

  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") Long id) {
    LOGGER.infof("DELETE /stores/%s", id);

    storeService.delete(id);

    LOGGER.infof("Store deleted id=%s", id);

    return Response.noContent().build();
  }
}
