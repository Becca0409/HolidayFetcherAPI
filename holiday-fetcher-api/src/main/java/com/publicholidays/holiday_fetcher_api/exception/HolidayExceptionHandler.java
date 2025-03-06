package com.publicholidays.holiday_fetcher_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class HolidayExceptionHandler {

    @ExceptionHandler(HolidayServiceExceptions.HolidayNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleHolidayNotFoundException(HolidayServiceExceptions.HolidayNotFoundException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);    }

    @ExceptionHandler(HolidayServiceExceptions.BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(HolidayServiceExceptions.BadRequestException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HolidayServiceExceptions.UnknownErrorException.class)
    public ResponseEntity<Map<String, Object>> handleUnknownErrorException(HolidayServiceExceptions.UnknownErrorException e) {
        return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        return new ResponseEntity<>(errorResponse, status);
    }
}