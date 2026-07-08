package com.factuec.shared.exception;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        boolean success,
        String message,
        List<FieldErrorResponse> errors,
        Instant timestamp
) {
    public static ErrorResponse of(String message) {
        return new ErrorResponse(false, message, List.of(), Instant.now());
    }

    public static ErrorResponse of(String message, List<FieldErrorResponse> errors) {
        return new ErrorResponse(false, message, errors, Instant.now());
    }
}
