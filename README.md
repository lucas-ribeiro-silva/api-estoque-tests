# API Test Automation Suite (api-estoque-tests)

This is a companion project to the [api-estoque](https://github.com/lucas-ribeiro-silva/api-estoque) (Order and Stock Management API).

This is a **Quality Assurance (QA) / SDET** project, built from scratch, with the sole purpose of running automated end-to-end (E2E) tests against the live `api-estoque`. It validates that the API not only functions correctly but that its core business logic is robust and reliable.

---

## 🚀 Key Features

* **Separation of Concerns:** Demonstrates the professional practice of keeping test code (this project) completely separate from the application code (the API).
* **End-to-End Logic Validation:** This suite does not just check simple endpoints. It tests the entire business flow of creating a customer, creating a product, placing an order, and verifying the side effects.
* **Business-Critical Rollback Validation:** The most important test (`OrderLogicTests`) programmatically proves that the API's `@Transactional` logic works. It confirms that if an order fails (e.g., insufficient stock), the database is *not* left in a corrupt state (i.e., the stock is not deducted).
* **BDD-Style Syntax:** Uses RestAssured's clean `given() / when() / then()` syntax for highly readable tests.
* **Dynamic Payloads:** Uses POJOs for clean, reusable request bodies.

---

## 🛠️ Technology Stack

* **Java 17**
* **Maven** (Build and dependency management)
* **JUnit 5 (Jupiter)** (Test runner and assertion framework)
* **RestAssured** (The core library for testing REST APIs in Java)
* **Lombok** (For clean POJO payloads)

---

## 🚦 How to Run These Tests

### Prerequisites

**1. The `api-estoque` Server MUST Be Running!**
This test suite makes *real* HTTP calls to the `api-estoque` project.

> Before running these tests, you must:
> 1.  Navigate to the `api-estoque` project folder.
> 2.  Run `docker-compose up -d` to start the PostgreSQL database.
> 3.  Run the Spring Boot application (e.g., `.\mvnw.cmd spring-boot:run` or by running `ApiEstoqueApplication.java` in your IDE).
> 4.  The API must be available at `http://localhost:8080`.

### Running the Tests

Once the API is running, you can run this test project:

* **From IntelliJ (Recommended):**
    1.  Open the `OrderLogicTests.java` or `ProductApiTests.java` file.
    2.  Click the green "Play" (▶️) icon next to the class name.
    3.  Select "Run '...Tests'".

* **From the Command Line:**
    1.  Open a **new terminal** in the `api-estoque-tests` root folder.
    2.  Run the Maven test command:
        ```bash
        # Run all tests
        .\mvnw.cmd test
        
        # Run only the main logic tests
        .\mvnw.cmd test -Dtest=OrderLogicTests
        ```

---

## 🔬 Test Scenarios Overview

This suite contains two main test classes:

### 1. `ProductApiTests.java` (Smoke Test)
* `testListAllProductsSuccessfully`: A simple "smoke test" that performs a `GET /api/products` to ensure the API is alive and responding with a `Status 200 OK` and a `JSON` content type.

### 2. `OrderLogicTests.java` (End-to-End Logic Test)
This is the main E2E test, running three scenarios in a specific order:

1.  **`@Order(1) - testCreatePrerequisites`:**
    * Creates a new `Customer` (via `POST /api/customers`).
    * Creates a new `Product` with an initial stock of 50 (via `POST /api/products`).
    * Extracts and saves the `customerId` and `productId` for the next tests.

2.  **`@Order(2) - testCreateValidOrderAndCheckStock`:**
    * Simulates a valid order for 10 units of the product.
    * **Asserts:** The API returns `Status 201 Created`.
    * **Asserts:** Performs a `GET /api/products/{id}` and confirms the `stockQuantity` has been correctly reduced from 50 to 40.

3.  **`@Order(3) - testCreateInvalidOrderAndCheckRollback`:**
    * Simulates an *invalid* order for 999 units (more than the 40 available).
    * **Asserts:** The API correctly returns `Status 400 Bad Request`.
    * **Asserts:** The JSON error response contains the message `"Insufficient stock"`.
    * **Asserts (The Critical Test):** Performs another `GET /api/products/{id}` and confirms the `stockQuantity` **is still 40**, proving the transactional rollback worked.

---

## 👨‍💻 Author

* **Lucas Ribeiro Silva**
* [LinkedIn](https://linkedin.com/in/dev-lucasribeirosilva)
* [GitHub](https://github.com/lucas-ribeiro-silva)