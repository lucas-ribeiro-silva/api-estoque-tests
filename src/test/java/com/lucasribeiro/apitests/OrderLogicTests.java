package com.lucasribeiro.apitests;

import com.lucasribeiro.apitests.payloads.*; // Importa nossos POJOs
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*; // Importante para as validações

import java.math.BigDecimal;
import java.util.List;

@TestMethodOrder(OrderAnnotation.class) // Garante que os testes rodem na ordem
public class OrderLogicTests {

    // IDs e dados que passaremos entre os testes
    private static Long customerId;
    private static Long productId;
    private static int initialStock = 50;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080/api";
    }

    @Test
    @Order(1) // 1º Teste a rodar
    @DisplayName("Deve criar Cliente e Produto para o teste de lógica")
    public void testCreatePrerequisites() {

        // 1. Criar Cliente
        CustomerPayload customer = new CustomerPayload();
        customer.setName("Cliente de Teste Automatizado");
        customer.setEmail("qa.teste." + System.currentTimeMillis() + "@email.com"); // Email único

        customerId = given()
                .contentType(ContentType.JSON)
                .body(customer)
                .when()
                .post("/customers")
                .then()
                .log().all()
                .statusCode(201)
                // --- CORREÇÃO AQUI ---
                // Usamos .jsonPath().getLong() para forçar a conversão para Long
                .extract().jsonPath().getLong("id");

        // 2. Criar Produto
        ProductPayload product = new ProductPayload();
        product.setName("Produto de Teste Automatizado");
        product.setDescription("Produto para teste de estoque");
        product.setPrice(new BigDecimal("100.00"));
        product.setStockQuantity(initialStock); // Começa com 50

        productId = given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/products")
                .then()
                .log().all()
                .statusCode(201)
                // --- CORREÇÃO AQUI ---
                .extract().jsonPath().getLong("id");

        System.out.println(">>> Pré-requisitos Criados: Cliente ID: " + customerId + ", Produto ID: " + productId);
    }

    @Test
    @Order(2) // 2º Teste a rodar
    @DisplayName("Deve criar pedido válido e verificar baixa de estoque")
    public void testCreateValidOrderAndCheckStock() {
        int quantityToBuy = 10;
        int expectedStock = initialStock - quantityToBuy; // 50 - 10 = 40

        // 1. Criar Pedido Válido
        OrderItemPayload item = new OrderItemPayload(productId, quantityToBuy);
        OrderRequestPayload orderRequest = new OrderRequestPayload(customerId, List.of(item));

        given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/orders")
                .then()
                .log().all()
                .statusCode(201)
                .body("status", equalTo("PENDING"));

        // 2. Verificar se o estoque baixou
        when()
                .get("/products/" + productId)
                .then()
                .log().all()
                .statusCode(200)
                .body("stockQuantity", equalTo(expectedStock)); // Deve ser 40

        System.out.println(">>> Teste de Estoque Válido OK: Estoque atualizado para: " + expectedStock);
    }

    @Test
    @Order(3) // 3º Teste a rodar
    @DisplayName("Deve falhar ao criar pedido sem estoque e verificar rollback")
    public void testCreateInvalidOrderAndCheckRollback() {
        int currentStock = 40; // Sabemos disso do teste anterior
        int quantityToBuy = 999; // Mais do que o estoque

        // 1. Tentar criar Pedido Inválido
        OrderItemPayload item = new OrderItemPayload(productId, quantityToBuy);
        OrderRequestPayload orderRequest = new OrderRequestPayload(customerId, List.of(item));

        given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/orders")
                .then()
                .log().all()
                .statusCode(400) // Espera 400 BAD REQUEST
                .body("message", containsString("Insufficient stock")); // Valida a msg de erro

        // 2. Verificar se o estoque NÃO mudou (A PROVA DO ROLLBACK)
        when()
                .get("/products/" + productId)
                .then()
                .log().all()
                .statusCode(200)
                .body("stockQuantity", equalTo(currentStock)); // DEVE continuar sendo 40

        System.out.println(">>> Teste de Rollback OK: Estoque NÃO mudou (continua " + currentStock + ")");
    }
}