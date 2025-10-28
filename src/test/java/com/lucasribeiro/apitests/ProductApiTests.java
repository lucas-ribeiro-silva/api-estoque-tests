package com.lucasribeiro.apitests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

// Importa os métodos estáticos do RestAssured para a sintaxe BDD
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*; // Importa os matchers

public class ProductApiTests {

    // @BeforeAll é um método do JUnit que roda UMA VEZ antes de TODOS os testes
    @BeforeAll
    public static void setup() {
        // Define a URL base para todos os testes desta classe
        RestAssured.baseURI = "http://localhost:8080/api";
    }

    @Test // Marca este método como um teste JUnit
    @DisplayName("Deve listar todos os produtos com sucesso (Status 200)")
    public void testListAllProductsSuccessfully() {

        // A sintaxe BDD (Given/When/Then) do RestAssured:

        given() // Dado que (configuração)
                .log().all() // Loga (imprime no console) a requisição
                .when() // Quando (a ação)
                .get("/products") // Faz um GET para http://localhost:8080/api/products
                .then() // Então (a validação)
                .log().all() // Loga a resposta
                .assertThat() // Valide que:
                .statusCode(200) // O status code da resposta é 200 (OK)
                .contentType(ContentType.JSON); // O tipo de conteúdo é JSON
    }
}