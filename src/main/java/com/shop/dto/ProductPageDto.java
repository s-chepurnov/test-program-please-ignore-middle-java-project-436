package com.shop.dto;

import java.util.List;

public record ProductPageDto(
        List<ProductDto> items,
        PageMetaDto meta
) {}
