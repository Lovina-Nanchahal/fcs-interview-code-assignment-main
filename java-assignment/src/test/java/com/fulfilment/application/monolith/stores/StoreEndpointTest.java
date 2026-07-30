package com.fulfilment.application.monolith.stores;

import io.quarkus.test.junit.QuarkusTest;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class StoreEndpointTest {

    private static final String PATH = "/store";

    @Test
    void shouldListStores() {

        given()
                .when().get(PATH)
                .then()
                .statusCode(200)
                .body(notNullValue());
    }

    @Test
    void shouldGetStoreById() {

        long id =
                given()
                        .when().get(PATH)
                        .then()
                        .extract()
                        .jsonPath()
                        .getLong("[0].id");

        given()
                .when().get(PATH + "/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo((int) id));
    }

    @Test
    void shouldCreateStore() {

        String request = """
                {
                  "name": "TEST_STORE",
                  "quantityProductsInStock": 10
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post(PATH)
                .then()
                .statusCode(201)
                .body("name", equalTo("TEST_STORE"));
    }

    @Test
    void shouldUpdateStore() {

        long id =
                given()
                        .when().get(PATH)
                        .then()
                        .extract()
                        .jsonPath()
                        .getLong("[0].id");

        String request = """
                {
                  "name": "UPDATED_STORE",
                  "quantityProductsInStock": 99
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().put(PATH + "/" + id)
                .then()
                .statusCode(200)
                .body("name", equalTo("UPDATED_STORE"));
    }

    @Test
    void shouldPatchStore() {

        long id =
                given()
                        .when().get(PATH)
                        .then()
                        .extract()
                        .jsonPath()
                        .getLong("[0].id");

        String request = """
                {
                  "name": "PATCHED_STORE"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().patch(PATH + "/" + id)
                .then()
                .statusCode(200)
                .body("name", equalTo("PATCHED_STORE"));
    }

    @Test
    void shouldDeleteStore() {

        long id =
                given()
                        .when().get(PATH)
                        .then()
                        .extract()
                        .jsonPath()
                        .getLong("[0].id");

        given()
                .when().delete(PATH + "/" + id)
                .then()
                .statusCode(204);
    }

    @Test
    void shouldReturn404WhenStoreNotFound() {

        given()
                .when().get(PATH + "/999999")
                .then()
                .statusCode(404);
    }
}