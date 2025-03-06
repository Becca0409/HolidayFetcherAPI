package com.publicholidays.holiday_fetcher_api.exception;

import lombok.Getter;

@Getter
public class HolidayServiceExceptions extends RuntimeException {
    private final HolidayServiceErrorType holidayServiceErrorType;

    public HolidayServiceExceptions(String message, HolidayServiceErrorType holidayServiceErrorType) {
        super(message);
        this.holidayServiceErrorType = holidayServiceErrorType;
    }

    public static class HolidayNotFoundException extends HolidayServiceExceptions {
        public HolidayNotFoundException(String message) {
            super(message, HolidayServiceErrorType.NOT_FOUND);
        }
    }

    public static class BadRequestException extends HolidayServiceExceptions {
        public BadRequestException(String message) {
            super(message, HolidayServiceErrorType.BAD_REQUEST);
        }
    }

        public static class UnknownErrorException extends HolidayServiceExceptions {
            public UnknownErrorException(String message) {
                super(message, HolidayServiceErrorType.UNKNOWN_ERROR);
            }
        }
    }

