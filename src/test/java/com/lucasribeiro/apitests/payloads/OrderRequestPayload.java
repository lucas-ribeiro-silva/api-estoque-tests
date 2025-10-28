package com.lucasribeiro.apitests.payloads;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // Útil para criar o pedido rapidamente
public class OrderRequestPayload {
    private Long customerId;
    private List<OrderItemPayload> items;
}