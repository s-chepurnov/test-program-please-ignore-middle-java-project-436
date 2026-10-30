package com.shop.exception;

import java.util.List;
import org.springframework.http.HttpStatus;

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
