package com.shop.controller;

import com.shop.exception.ApiException;
import com.shop.exception.UnavailableItemsException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setType(URI.create("/problems/validation-error"));
        problem.setTitle("Validation error");

        var details = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();
        problem.setDetail(String.join("; ", details));

        return problem;
    }

    @ExceptionHandler(NumberFormatException.class)
    public ProblemDetail handleNumberFormat(NumberFormatException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setType(URI.create("/problems/not-found"));
        problem.setTitle("Resource not found");
        problem.setDetail("Invalid ID format: " + ex.getMessage());
        return problem;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setType(URI.create("/problems/not-found"));
        problem.setTitle("Resource not found");
        problem.setDetail("Invalid ID format: " + ex.getValue());
        return problem;
    }

    @ExceptionHandler()
    public ProblemDetail handleNoResourceFound(NoResourceFoundException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setType(URI.create("/problems/not-found"));
        problem.setTitle("Resource not found");
        problem.setDetail(ex.getMessage());
        return problem;
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
