package com.shop.controller;

import com.shop.exception.ApiException;
import com.shop.exception.UnavailableItemsException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ProblemDetail handleApiException(ApiException ex) {
        var problem = ProblemDetail.forStatus(ex.getStatus());
        problem.setType(URI.create(ex.getType()));
        problem.setTitle(getTitle(ex.getType()));
        problem.setDetail(ex.getMessage());

        if (ex instanceof UnavailableItemsException unavailable) {
            problem.setProperty("unavailableProductIds", unavailable.getUnavailableProductIds());
        }

        return problem;
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ProblemDetail handleSpringErrors(ErrorResponseException ex) {
        if (ex.getStatusCode().value() == 404) {
            var problem = ProblemDetail.forStatus(ex.getStatusCode());
            problem.setType(URI.create("/problems/not-found"));
            problem.setTitle("Resource not found");
            return problem;
        }
        return ex.getBody();
    }

    private String getTitle(String type) {
        return switch (type) {
            case "/problems/validation-error" -> "Validation error";
            case "/problems/address-required" -> "Address required";
            case "/problems/unavailable-items" -> "Unavailable items";
            case "/problems/unauthorized" -> "Unauthorized";
            case "/problems/invalid-credentials" -> "Invalid credentials";
            case "/problems/not-found" -> "Resource not found";
            case "/problems/email-taken" -> "Email taken";
            case "/problems/internal-error" -> "Internal server error";
            default -> "Error";
        };
    }
}
