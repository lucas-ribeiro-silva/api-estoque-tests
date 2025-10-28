package com.lucasribeiro.apitests.payloads;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductPayload {
    // Apenas os campos que enviamos na criação
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
}