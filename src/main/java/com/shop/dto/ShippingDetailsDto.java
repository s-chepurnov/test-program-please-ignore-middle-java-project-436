package com.shop.dto;

import com.shop.domain.ShippingDetails;

public record ShippingDetailsDto(
        String method,
        String recipientName,
        String phone,
        String address
) {
    public static ShippingDetailsDto from(ShippingDetails shipping) {
        return new ShippingDetailsDto(
                shipping.method(),
                shipping.recipientName(),
                shipping.phone(),
                shipping.address()
        );
    }
}
