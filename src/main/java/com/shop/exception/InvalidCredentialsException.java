package com.shop.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends ApiException {
  public InvalidCredentialsException(String message) {
    super(HttpStatus.UNAUTHORIZED, "/problems/invalid-credentials", message);
  }
}
