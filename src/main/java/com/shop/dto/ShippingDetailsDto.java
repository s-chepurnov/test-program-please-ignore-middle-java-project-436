package com.shop.dto;

import com.shop.domain.ShippingDetails;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ShippingDetailsDto(
        @NotBlank(message = "shipping.method: must not be blank")
        @Pattern(regexp = "delivery|pickup", message = "shipping.method: must be delivery or pickup")
        String method,

        @NotBlank(message = "shipping.recipientName: must not be blank")
        String recipientName,

        @NotBlank(message = "shipping.phone: must not be blank")
        String phone,

        String address
) {
    public ShippingDetails toDomain() {
        return new ShippingDetails(method, recipientName, phone, address);
    }

    public static ShippingDetailsDto from(ShippingDetails shipping) {
        return new ShippingDetailsDto(
                shipping.method(),
                shipping.recipientName(),
                shipping.phone(),
                shipping.address()
        );
    }
}
