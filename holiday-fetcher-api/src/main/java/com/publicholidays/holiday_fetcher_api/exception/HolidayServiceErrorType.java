package com.publicholidays.holiday_fetcher_api.exception;

import lombok.Getter;

@Getter
public enum HolidayServiceErrorType {
    NOT_FOUND("No holidays found for the given country and year."),
    BAD_REQUEST("Invalid request. Please insert valid data input."),
    UNKNOWN_ERROR("Unknown error.");

    private final String message;

    HolidayServiceErrorType(String message) {
        this.message = message;
    }

}
