package com.shop.dto;

import com.shop.domain.Promo;

public record PromoDto(
        String id,
        String title,
        String text,
        ProductDto product
) {
    public static PromoDto from(Promo promo) {
        return new PromoDto(
                String.valueOf(promo.id()),
                promo.title(),
                promo.text(),
                ProductDto.from(promo.product())
        );
    }
}
