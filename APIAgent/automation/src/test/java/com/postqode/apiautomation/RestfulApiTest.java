package com.postqode.apiautomation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class RestfulApiTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static JsonNode testData;
    private static String baseUrl;
    private static String resourcePath;
    private static String validObjectId;
    private static String invalidObjectId;

    @BeforeAll
    static void setUp() throws IOException {
        Path testDataPath = Path.of("..", "testdata", "restful-api-test-data.json").normalize();
        assertTrue(Files.exists(testDataPath), "Test data file not found: " + testDataPath.toAbsolutePath());

        testData = OBJECT_MAPPER.readTree(Files.readString(testDataPath));
        baseUrl = testData.get("baseUrl").asText();
        resourcePath = testData.get("resourcePath").asText();
        validObjectId = testData.get("validIds").get("existingObjectId").asText();
        invalidObjectId = testData.get("invalidIds").get("nonExistingObjectId").asText();

        RestAssured.baseURI = baseUrl;
    }

    @Test
    @DisplayName("TC-01: List all objects")
    void shouldListAllObjects() {
        int expectedStatus = testData.get("expectedResults").get("listObjectsStatus").asInt();

        Response response = given()
                .when()
                .get(resourcePath)
                .then()
                .extract()
                .response();

        assertEquals(expectedStatus, response.statusCode(), "Unexpected status code for list objects");
        assertTrue(response.getBody().asString().trim().startsWith("["), "Response body should be a JSON array");
        assertTrue(response.jsonPath().getList("$").size() > 0, "Object list should not be empty");
        assertNotNull(response.jsonPath().get("[0].id"), "First object should contain id");
    }

    @Test
    @DisplayName("TC-02: Get a specific object by valid ID")
    void shouldGetSpecificObjectByValidId() {
        int expectedStatus = testData.get("expectedResults").get("getObjectValidStatus").asInt();

        Response response = given()
                .pathParam("id", validObjectId)
                .when()
                .get(resourcePath + "/{id}")
                .then()
                .extract()
                .response();

        assertEquals(expectedStatus, response.statusCode(), "Unexpected status code for get by valid id");
        assertEquals(validObjectId, response.jsonPath().getString("id"), "Returned id should match requested id");
        assertNotNull(response.jsonPath().get("name"), "Response should contain name");
    }

    @Test
    @DisplayName("TC-03: Get a specific object by invalid ID")
    void shouldReturnNotFoundForInvalidObjectId() {
        int expectedStatus = testData.get("expectedResults").get("getObjectInvalidStatus").asInt();

        Response response = given()
                .pathParam("id", invalidObjectId)
                .when()
                .get(resourcePath + "/{id}")
                .then()
                .extract()
                .response();

        assertEquals(expectedStatus, response.statusCode(), "Unexpected status code for invalid object lookup");
        assertTrue(response.getBody().asString().length() > 0, "Error response should not be empty");
    }

    @Test
    @DisplayName("TC-04: Create a new object with valid payload")
    void shouldCreateObjectSuccessfully() throws Exception {
        JsonNode payload = testData.get("payloads").get("createObjectValid");
        JsonNode acceptedStatuses = testData.get("expectedResults").get("createObjectSuccessStatus");

        Response response = given()
                .contentType("application/json")
                .body(OBJECT_MAPPER.writeValueAsString(payload))
                .when()
                .post(resourcePath)
                .then()
                .extract()
                .response();

        assertStatusIn(response.statusCode(), acceptedStatuses, "Unexpected status code for create object");
        assertNotNull(response.jsonPath().get("id"), "Created object response should contain id");
        assertEquals(payload.get("name").asText(), response.jsonPath().getString("name"), "Created object name mismatch");
        assertNotNull(response.jsonPath().get("createdAt"), "Created object response should contain createdAt");
    }

    @Test
    @DisplayName("TC-05: Create a new object with missing name")
    void shouldHandleCreateObjectWithMissingName() throws Exception {
        JsonNode payload = testData.get("payloads").get("createObjectMissingName");
        JsonNode validationStatuses = testData.get("expectedResults").get("createObjectValidationFailureStatus");

        Response response = given()
                .contentType("application/json")
                .body(OBJECT_MAPPER.writeValueAsString(payload))
                .when()
                .post(resourcePath)
                .then()
                .extract()
                .response();

        boolean actualIsExpectedValidationFailure = containsStatus(validationStatuses, response.statusCode());
        boolean apiAcceptedPayload = response.statusCode() == 200 || response.statusCode() == 201;

        assertTrue(
                actualIsExpectedValidationFailure || apiAcceptedPayload,
                "Expected either configured validation failure or accepted payload behavior for public demo API, but got: " + response.statusCode()
        );
        assertTrue(response.getBody().asString().length() > 0, "Response body should not be empty");
    }

    @Test
    @DisplayName("TC-06: Update an existing object fully with valid payload")
    void shouldUpdateObjectSuccessfully() throws Exception {
        String objectId = createObjectAndReturnId();
        JsonNode payload = testData.get("payloads").get("updateObjectValid");
        int expectedStatus = testData.get("expectedResults").get("updateObjectSuccessStatus").asInt();

        Response response = given()
                .contentType("application/json")
                .pathParam("id", objectId)
                .body(OBJECT_MAPPER.writeValueAsString(payload))
                .when()
                .put(resourcePath + "/{id}")
                .then()
                .extract()
                .response();

        assertEquals(expectedStatus, response.statusCode(), "Unexpected status code for update object");
        assertEquals(payload.get("name").asText(), response.jsonPath().getString("name"), "Updated object name mismatch");
        assertNotNull(response.jsonPath().get("updatedAt"), "Updated object response should contain updatedAt");
    }

    @Test
    @DisplayName("TC-07: Update an existing object with invalid ID")
    void shouldFailToUpdateObjectWithInvalidId() throws Exception {
        JsonNode payload = testData.get("payloads").get("updateObjectValid");
        JsonNode expectedStatuses = testData.get("expectedResults").get("updateObjectInvalidStatus");

        Response response = given()
                .contentType("application/json")
                .pathParam("id", invalidObjectId)
                .body(OBJECT_MAPPER.writeValueAsString(payload))
                .when()
                .put(resourcePath + "/{id}")
                .then()
                .extract()
                .response();

        assertStatusIn(response.statusCode(), expectedStatuses, "Unexpected status code for update with invalid id");
        assertTrue(response.getBody().asString().length() > 0, "Error response should not be empty");
    }

    @Test
    @DisplayName("TC-08: Partially update an existing object")
    void shouldPatchObjectSuccessfully() throws Exception {
        String objectId = createObjectAndReturnId();
        JsonNode payload = testData.get("payloads").get("patchObjectValid");
        int expectedStatus = testData.get("expectedResults").get("patchObjectSuccessStatus").asInt();

        Response response = given()
                .contentType("application/json")
                .pathParam("id", objectId)
                .body(OBJECT_MAPPER.writeValueAsString(payload))
                .when()
                .patch(resourcePath + "/{id}")
                .then()
                .extract()
                .response();

        assertEquals(expectedStatus, response.statusCode(), "Unexpected status code for patch object");
        assertEquals(payload.get("name").asText(), response.jsonPath().getString("name"), "Patched object name mismatch");
        assertNotNull(response.jsonPath().get("updatedAt"), "Patched object response should contain updatedAt");
    }

    @Test
    @DisplayName("TC-09: Partially update an object with invalid ID")
    void shouldFailToPatchObjectWithInvalidId() throws Exception {
        JsonNode payload = testData.get("payloads").get("patchObjectValid");
        JsonNode expectedStatuses = testData.get("expectedResults").get("patchObjectInvalidStatus");

        Response response = given()
                .contentType("application/json")
                .pathParam("id", invalidObjectId)
                .body(OBJECT_MAPPER.writeValueAsString(payload))
                .when()
                .patch(resourcePath + "/{id}")
                .then()
                .extract()
                .response();

        assertStatusIn(response.statusCode(), expectedStatuses, "Unexpected status code for patch with invalid id");
        assertTrue(response.getBody().asString().length() > 0, "Error response should not be empty");
    }

    private static String createObjectAndReturnId() throws Exception {
        JsonNode payload = testData.get("payloads").get("createObjectValid");

        Response response = given()
                .contentType("application/json")
                .body(OBJECT_MAPPER.writeValueAsString(payload))
                .when()
                .post(resourcePath)
                .then()
                .extract()
                .response();

        assertTrue(
                response.statusCode() == 200 || response.statusCode() == 201,
                "Object creation failed during test setup. Status code: " + response.statusCode()
        );

        String createdId = response.jsonPath().getString("id");
        assertNotNull(createdId, "Created object id should not be null");
        return createdId;
    }

    private static void assertStatusIn(int actualStatus, JsonNode expectedStatuses, String message) {
        assertTrue(containsStatus(expectedStatuses, actualStatus), message + ". Actual status: " + actualStatus);
    }

    private static boolean containsStatus(JsonNode expectedStatuses, int actualStatus) {
        Iterator<JsonNode> iterator = expectedStatuses.elements();
        while (iterator.hasNext()) {
            if (iterator.next().asInt() == actualStatus) {
                return true;
            }
        }
        return false;
    }
}
