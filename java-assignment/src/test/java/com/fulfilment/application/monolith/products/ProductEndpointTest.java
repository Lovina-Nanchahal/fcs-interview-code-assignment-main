package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductEndpointTest {

  private final String path = "/product";


  @Test
  public void testListProducts() {

    given()
            .when()
            .get(path)
            .then()
            .statusCode(200)
            .body(
                    containsString("TONSTAD"),
                    containsString("KALLAX"),
                    containsString("BESTÅ")
            );
  }


  @Test
  public void testDeleteProduct() {

    String json = """
                {
                  "name": "DELETE_TEST_PRODUCT",
                  "description": "delete test",
                  "price": 10,
                  "stock": 5
                }
                """;

    Integer id =
            given()
                    .contentType(ContentType.JSON)
                    .body(json)
                    .when()
                    .post(path)
                    .then()
                    .statusCode(201)
                    .extract()
                    .jsonPath()
                    .getInt("id");


    given()
            .when()
            .delete(path + "/" + id)
            .then()
            .statusCode(204);


    given()
            .when()
            .get(path + "/" + id)
            .then()
            .statusCode(404);
  }


  @Test
  void shouldGetSingleProduct() {

    String json = """
                {
                  "name": "GET_TEST_PRODUCT",
                  "description": "get test",
                  "price": 10,
                  "stock": 5
                }
                """;


    Integer id =
            given()
                    .contentType(ContentType.JSON)
                    .body(json)
                    .when()
                    .post(path)
                    .then()
                    .statusCode(201)
                    .extract()
                    .jsonPath()
                    .getInt("id");


    given()
            .when()
            .get(path + "/" + id)
            .then()
            .statusCode(200)
            .body("id", equalTo(id));
  }


  @Test
  void shouldReturn404WhenProductDoesNotExist() {

    given()
            .when()
            .get(path + "/999999")
            .then()
            .statusCode(404);
  }


  @Test
  void shouldCreateProduct() {

    String json = """
                {
                  "name": "TEST_PRODUCT",
                  "description": "test",
                  "price": 10.5,
                  "stock": 5
                }
                """;


    given()
            .contentType(ContentType.JSON)
            .body(json)
            .when()
            .post(path)
            .then()
            .statusCode(201)
            .body("name", equalTo("TEST_PRODUCT"));
  }


  @Test
  void shouldRejectCreateWhenIdProvided() {

    String json = """
                {
                  "id": 1,
                  "name": "INVALID_PRODUCT",
                  "description": "test",
                  "price": 10.5,
                  "stock": 5
                }
                """;


    given()
            .contentType(ContentType.JSON)
            .body(json)
            .when()
            .post(path)
            .then()
            .statusCode(422);
  }


  @Test
  void shouldUpdateProduct() {

    String createJson = """
                {
                  "name": "UPDATE_TEST_PRODUCT",
                  "description": "original",
                  "price": 10,
                  "stock": 5
                }
                """;


    Integer id =
            given()
                    .contentType(ContentType.JSON)
                    .body(createJson)
                    .when()
                    .post(path)
                    .then()
                    .statusCode(201)
                    .extract()
                    .jsonPath()
                    .getInt("id");


    String updateJson = """
                {
                  "name": "UPDATED_PRODUCT",
                  "description": "updated description",
                  "price": 99.99,
                  "stock": 20
                }
                """;


    given()
            .contentType(ContentType.JSON)
            .body(updateJson)
            .when()
            .put(path + "/" + id)
            .then()
            .statusCode(200)
            .body("name", equalTo("UPDATED_PRODUCT"));
  }


  @Test
  void shouldRejectUpdateWhenNameMissing() {

    String createJson = """
                {
                  "name": "UPDATE_VALIDATION_PRODUCT",
                  "description": "test",
                  "price": 10,
                  "stock": 5
                }
                """;


    Integer id =
            given()
                    .contentType(ContentType.JSON)
                    .body(createJson)
                    .when()
                    .post(path)
                    .then()
                    .statusCode(201)
                    .extract()
                    .jsonPath()
                    .getInt("id");


    String updateJson = """
                {
                  "description": "missing name",
                  "price": 10,
                  "stock": 5
                }
                """;


    given()
            .contentType(ContentType.JSON)
            .body(updateJson)
            .when()
            .put(path + "/" + id)
            .then()
            .statusCode(422);
  }


  @Test
  void shouldReturn404WhenUpdatingUnknownProduct() {

    String json = """
                {
                  "name": "UNKNOWN_PRODUCT",
                  "description": "test",
                  "price": 10,
                  "stock": 5
                }
                """;


    given()
            .contentType(ContentType.JSON)
            .body(json)
            .when()
            .put(path + "/999999")
            .then()
            .statusCode(404);
  }


  @Test
  void shouldReturn404WhenDeletingUnknownProduct() {

    given()
            .when()
            .delete(path + "/999999")
            .then()
            .statusCode(404);
  }
}