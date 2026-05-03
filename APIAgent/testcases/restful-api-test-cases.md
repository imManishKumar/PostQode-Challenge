# Restful API Test Cases

## Overview
This document contains functional API test cases derived from `Restful-API.postman_collection.json`.

- **Base URL:** `https://api.restful-api.dev`
- **Resource:** `/objects`

---

## TC-01: List all objects
- **API Name:** List Objects
- **Method:** `GET`
- **Endpoint:** `/objects`
- **Objective:** Verify that the API returns the list of available objects.
- **Preconditions:** API service is available.
- **Test Steps:**
  1. Send a `GET` request to `/objects`.
  2. Capture the response.
- **Expected Results:**
  - Status code should be `200`.
  - Response body should be a JSON array.
  - Array size should be greater than `0`.
  - Each object should contain an `id` field.

---

## TC-02: Get a specific object by valid ID
- **API Name:** Get Specific Object
- **Method:** `GET`
- **Endpoint:** `/objects/{id}`
- **Objective:** Verify that the API returns object details for a valid object ID.
- **Preconditions:** A valid object ID exists.
- **Test Data:** `id = 1`
- **Test Steps:**
  1. Send a `GET` request to `/objects/1`.
  2. Capture the response.
- **Expected Results:**
  - Status code should be `200`.
  - Response body should contain `id`.
  - Response body should contain `name`.
  - Returned `id` should match the requested ID.

---

## TC-03: Get a specific object by invalid ID
- **API Name:** Get Specific Object
- **Method:** `GET`
- **Endpoint:** `/objects/{id}`
- **Objective:** Verify error handling for a non-existing object ID.
- **Preconditions:** Invalid/non-existing object ID is available.
- **Test Data:** `id = 9999999`
- **Test Steps:**
  1. Send a `GET` request to `/objects/9999999`.
  2. Capture the response.
- **Expected Results:**
  - Status code should be `404`.
  - Response body should contain an error message or indicate that the object was not found.

---

## TC-04: Create a new object with valid payload
- **API Name:** Create Object
- **Method:** `POST`
- **Endpoint:** `/objects`
- **Objective:** Verify that a new object can be created successfully.
- **Preconditions:** API service is available.
- **Test Data:**
```json
{
  "name": "Apple MacBook Pro M5",
  "data": {
    "year": 2026,
    "price": 1849.99,
    "CPU model": "M5",
    "Hard disk size": "1 TB"
  }
}
```
- **Test Steps:**
  1. Send a `POST` request to `/objects` with valid JSON payload.
  2. Capture the response.
- **Expected Results:**
  - Status code should be `200` or `201`.
  - Response body should contain generated `id`.
  - Response body should contain the same `name` as the request.
  - Response body should contain `createdAt`.

---

## TC-05: Create a new object with missing name
- **API Name:** Create Object
- **Method:** `POST`
- **Endpoint:** `/objects`
- **Objective:** Verify validation behavior when mandatory fields are missing.
- **Preconditions:** API service is available.
- **Test Data:**
```json
{
  "data": {
    "year": 2026,
    "price": 1849.99
  }
}
```
- **Test Steps:**
  1. Send a `POST` request to `/objects` with payload missing `name`.
  2. Capture the response.
- **Expected Results:**
  - Status code should indicate validation failure (`400` expected if validation exists).
  - Response should contain an error message.

> Note: If the API accepts this payload, log the behavior as an observation because the public demo API may not enforce strict validation.

---

## TC-06: Update an existing object fully with valid payload
- **API Name:** Update Object
- **Method:** `PUT`
- **Endpoint:** `/objects/{id}`
- **Objective:** Verify that an existing object can be updated successfully with a full payload.
- **Preconditions:** A valid object ID exists or is created during test execution.
- **Test Steps:**
  1. Create a new object and capture the returned `id`.
  2. Send a `PUT` request to `/objects/{id}` with full update payload.
  3. Capture the response.
- **Expected Results:**
  - Status code should be `200`.
  - Response body should contain the updated `name`.
  - Response body should contain updated `data`.
  - Response body should contain `updatedAt`.

---

## TC-07: Update an existing object with invalid ID
- **API Name:** Update Object
- **Method:** `PUT`
- **Endpoint:** `/objects/{id}`
- **Objective:** Verify error handling when trying to update a non-existing object.
- **Preconditions:** Invalid/non-existing object ID is available.
- **Test Data:** `id = 9999999`
- **Test Steps:**
  1. Send a `PUT` request to `/objects/9999999`.
  2. Capture the response.
- **Expected Results:**
  - Status code should indicate failure (`404` expected if resource does not exist).
  - Response should contain an error message.

---

## TC-08: Partially update an existing object
- **API Name:** Partial Update
- **Method:** `PATCH`
- **Endpoint:** `/objects/{id}`
- **Objective:** Verify that a single field can be updated successfully.
- **Preconditions:** A valid object ID exists or is created during test execution.
- **Test Steps:**
  1. Create a new object and capture the returned `id`.
  2. Send a `PATCH` request to `/objects/{id}` with partial payload.
  3. Capture the response.
- **Expected Results:**
  - Status code should be `200`.
  - Response body should contain updated `name`.
  - Response body should contain `updatedAt`.

---

## TC-09: Partially update an object with invalid ID
- **API Name:** Partial Update
- **Method:** `PATCH`
- **Endpoint:** `/objects/{id}`
- **Objective:** Verify error handling for partial update on a non-existing object.
- **Preconditions:** Invalid/non-existing object ID is available.
- **Test Data:** `id = 9999999`
- **Test Steps:**
  1. Send a `PATCH` request to `/objects/9999999`.
  2. Capture the response.
- **Expected Results:**
  - Status code should indicate failure (`404` expected).
  - Response should contain an error message.

---

## Traceability Matrix

| Test Case ID | API | Method | Endpoint |
|---|---|---|---|
| TC-01 | List Objects | GET | /objects |
| TC-02 | Get Specific Object | GET | /objects/{id} |
| TC-03 | Get Specific Object Invalid ID | GET | /objects/{id} |
| TC-04 | Create Object | POST | /objects |
| TC-05 | Create Object Missing Name | POST | /objects |
| TC-06 | Update Object | PUT | /objects/{id} |
| TC-07 | Update Object Invalid ID | PUT | /objects/{id} |
| TC-08 | Partial Update | PATCH | /objects/{id} |
| TC-09 | Partial Update Invalid ID | PATCH | /objects/{id} |

## Notes
- The API is a public demo API. Some negative scenarios may behave differently from strictly validated enterprise APIs.
- For update scenarios, object creation should be done dynamically during automation to avoid dependency on static IDs.
