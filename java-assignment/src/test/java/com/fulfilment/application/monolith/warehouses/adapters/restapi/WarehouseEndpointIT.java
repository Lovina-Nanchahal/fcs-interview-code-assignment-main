package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
public class WarehouseEndpointIT {

  @Test
  public void testSimpleListWarehouses() {

    given()
            .when()
            .get("/warehouse")
            .then()
            .statusCode(200)
            .body(
                    containsString("MWH.001"),
                    containsString("MWH.012"),
                    containsString("MWH.023"));
  }


  @Test
  void shouldGetWarehouseByBusinessUnitCode() {

    given()
            .when()
            .get("/warehouse/MWH.001")
            .then()
            .statusCode(200)
            .body("businessUnitCode", equalTo("MWH.001"));
  }


  @Test
  void shouldReturn404WhenWarehouseDoesNotExist() {

    given()
            .when()
            .get("/warehouse/UNKNOWN")
            .then()
            .statusCode(404);
  }


  @Test
  void shouldCreateWarehouse() {

    String request = """
        {
          "businessUnitCode":"MWH.999",
          "location":"EINDHOVEN-001",
          "capacity":20,
          "stock":10
        }
        """;

    given()
            .contentType("application/json")
            .body(request)
            .when()
            .post("/warehouse")
            .then()
            .statusCode(200)
            .body("businessUnitCode", equalTo("MWH.999"));
  }


  @Test
  void shouldCreateAndRetrieveWarehouse() {

    String request = """
        {
          "businessUnitCode":"MWH.998",
          "location":"HELMOND-001",
          "capacity":20,
          "stock":10
        }
        """;

    given()
            .contentType("application/json")
            .body(request)
            .post("/warehouse")
            .then()
            .statusCode(200);

    given()
            .get("/warehouse/MWH.998")
            .then()
            .statusCode(200)
            .body("businessUnitCode", equalTo("MWH.998"));
  }


  @Test
  void shouldArchiveWarehouse() {

    String request = """
        {
          "businessUnitCode":"MWH.ARCHIVE",
          "location":"ZWOLLE-002",
          "capacity":20,
          "stock":10
        }
        """;

    given()
            .contentType("application/json")
            .body(request)
            .post("/warehouse")
            .then()
            .statusCode(200);

    given()
            .delete("/warehouse/MWH.ARCHIVE")
            .then()
            .statusCode(204);

    given()
            .get("/warehouse/MWH.ARCHIVE")
            .then()
            .statusCode(404);
  }


  @Test
  void shouldReturn404WhenArchivingUnknownWarehouse() {

    given()
            .delete("/warehouse/UNKNOWN")
            .then()
            .statusCode(404);
  }


  @Test
  void shouldReplaceWarehouse() {

    String replacement = """
        {
          "businessUnitCode":"MWH.001",
          "location":"AMSTERDAM-002",
          "capacity":70,
          "stock":10
        }
        """;

    given()
            .contentType("application/json")
            .body(replacement)
            .post("/warehouse/MWH.001/replacement")
            .then()
            .statusCode(200)
            .body(
                    "businessUnitCode", equalTo("MWH.001"),
                    "capacity", equalTo(70),
                    "stock", equalTo(10));
  }


  @Test
  void shouldReturn404WhenReplacingUnknownWarehouse() {

    String replacement = """
        {
          "businessUnitCode":"UNKNOWN",
          "location":"ZWOLLE-001",
          "capacity":40,
          "stock":10
        }
        """;

    given()
            .contentType("application/json")
            .body(replacement)
            .post("/warehouse/UNKNOWN/replacement")
            .then()
            .statusCode(404);
  }


  @Test
  void shouldRejectDuplicateBusinessUnitCode() {

    String request = """
        {
          "businessUnitCode":"MWH.DUPLICATE",
          "location":"ZWOLLE-002",
          "capacity":20,
          "stock":10
        }
        """;

    given()
            .contentType("application/json")
            .body(request)
            .post("/warehouse")
            .then()
            .statusCode(200);

    given()
            .contentType("application/json")
            .body(request)
            .post("/warehouse")
            .then()
            .statusCode(409);
  }


  @Test
  void shouldRejectWarehouseCreationWhenMaximumWarehousesReached() {

    String firstWarehouse = """
        {
          "businessUnitCode":"MWH.VETSBY.001",
          "location":"VETSBY-001",
          "capacity":20,
          "stock":10
        }
        """;

    String secondWarehouse = """
        {
          "businessUnitCode":"MWH.VETSBY.002",
          "location":"VETSBY-001",
          "capacity":20,
          "stock":10
        }
        """;

    given()
            .contentType("application/json")
            .body(firstWarehouse)
            .post("/warehouse")
            .then()
            .statusCode(200);

    given()
            .contentType("application/json")
            .body(secondWarehouse)
            .post("/warehouse")
            .then()
            .statusCode(409);
  }


  @Test
  void shouldRejectStockGreaterThanCapacity() {

    String request = """
        {
          "businessUnitCode":"MWH.INVALID.STOCK",
          "location":"ZWOLLE-002",
          "capacity":20,
          "stock":50
        }
        """;

    given()
            .contentType("application/json")
            .body(request)
            .post("/warehouse")
            .then()
            .statusCode(409);
  }


  @Test
  void shouldRejectCapacityGreaterThanLocationLimit() {

    String request = """
        {
          "businessUnitCode":"MWH.INVALID.CAPACITY",
          "location":"ZWOLLE-002",
          "capacity":1000,
          "stock":10
        }
        """;

    given()
            .contentType("application/json")
            .body(request)
            .post("/warehouse")
            .then()
            .statusCode(409);
  }


  @Test
  void shouldRejectWarehouseCreationWithInvalidLocation() {

    String request = """
        {
          "businessUnitCode":"MWH.INVALID.LOCATION",
          "location":"UNKNOWN_LOCATION",
          "capacity":20,
          "stock":10
        }
        """;

    given()
            .contentType("application/json")
            .body(request)
            .post("/warehouse")
            .then()
            .statusCode(404);
  }


  @Test
  void shouldRejectWarehouseCreationWhenBusinessUnitCodeIsMissing() {

    String request = """
        {
          "location":"VETSBY-001",
          "capacity":20,
          "stock":10
        }
        """;

    given()
            .contentType("application/json")
            .body(request)
            .post("/warehouse")
            .then()
            .statusCode(400);
  }


  @Test
  void shouldRejectWarehouseCreationWhenLocationIsMissing() {

    String request = """
        {
          "businessUnitCode":"MWH.NO.LOCATION",
          "capacity":20,
          "stock":10
        }
        """;

    given()
            .contentType("application/json")
            .body(request)
            .post("/warehouse")
            .then()
            .statusCode(400);
  }


  @Test
  void shouldRejectWarehouseCreationWhenCapacityIsZero() {

    String request = """
        {
          "businessUnitCode":"MWH.ZERO.CAPACITY",
          "location":"VETSBY-001",
          "capacity":0,
          "stock":0
        }
        """;

    given()
            .contentType("application/json")
            .body(request)
            .post("/warehouse")
            .then()
            .statusCode(400);
  }


  @Test
  void shouldRejectWarehouseCreationWhenStockIsNegative() {

    String request = """
        {
          "businessUnitCode":"MWH.NEGATIVE.STOCK",
          "location":"VETSBY-001",
          "capacity":20,
          "stock":-1
        }
        """;

    given()
            .contentType("application/json")
            .body(request)
            .post("/warehouse")
            .then()
            .statusCode(400);
  }
}