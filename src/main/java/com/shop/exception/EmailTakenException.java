package com.shop.exception;

import org.springframework.http.HttpStatus;

public class EmailTakenException extends ApiException {
  public EmailTakenException(String message) {
    super(HttpStatus.CONFLICT, "/problems/email-taken", message);
  }
}
