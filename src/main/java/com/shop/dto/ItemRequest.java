package com.shop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemRequest(
        @NotNull(message = "items.productId: must not be null")
        Integer productId,

        @Min(value = 1, message = "items.quantity: must be at least 1")
        int quantity
) {}
