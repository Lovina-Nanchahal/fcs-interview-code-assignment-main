package com.fulfilment.application.monolith.location;

import com.fulfilment.application.monolith.exception.LocationNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocationGatewayTest {

  @Test
  public void shouldReturnExistingLocation() {
    LocationGateway locationGateway = new LocationGateway();

    Location location = locationGateway.resolveByIdentifier("ZWOLLE-001");

    assertEquals("ZWOLLE-001", location.identification);
    assertEquals(1, location.maxNumberOfWarehouses);
    assertEquals(40, location.maxCapacity);
  }

  @Test
  void shouldThrowExceptionForUnknownLocation() {
    LocationGateway locationGateway = new LocationGateway();

    assertThrows(LocationNotFoundException.class, () -> locationGateway.resolveByIdentifier("UNKNOWN"));
  }
}