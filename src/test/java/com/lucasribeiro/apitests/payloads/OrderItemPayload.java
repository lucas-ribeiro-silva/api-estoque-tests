package com.lucasribeiro.apitests.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // Útil para criar o item rapidamente
public class OrderItemPayload {
    private Long productId;
    private Integer quantity;
}