package com.shop.exception;

import org.springframework.http.HttpStatus;
import java.util.List;

public class UnavailableItemsException extends ApiException {
    private final List<String> unavailableProductIds;

    public UnavailableItemsException(String message, List<String> unavailableProductIds) {
        super(HttpStatus.BAD_REQUEST, "/problems/unavailable-items", message);
        this.unavailableProductIds = unavailableProductIds;
    }

    public List<String> getUnavailableProductIds() {
        return unavailableProductIds;
    }
}
