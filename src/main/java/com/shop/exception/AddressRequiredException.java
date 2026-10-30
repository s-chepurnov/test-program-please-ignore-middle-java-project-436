package com.shop.exception;

import org.springframework.http.HttpStatus;

public class AddressRequiredException extends ApiException {
  public AddressRequiredException(String message) {
    super(HttpStatus.BAD_REQUEST, "/problems/address-required", message);
  }
}
