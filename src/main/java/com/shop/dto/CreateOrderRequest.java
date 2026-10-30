package com.shop.dto;

import java.util.List;

public record CreateOrderRequest(
        List<ItemRequest> items,
        ShippingDetailsDto shipping
) {}
