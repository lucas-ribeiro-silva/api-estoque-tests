package com.lucasribeiro.apitests.payloads;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Gera Getters, Setters, etc.
@NoArgsConstructor
public class CustomerPayload {
    private String name;
    private String email;
}