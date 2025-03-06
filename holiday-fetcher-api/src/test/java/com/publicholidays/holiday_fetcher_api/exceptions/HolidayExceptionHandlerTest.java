package com.publicholidays.holiday_fetcher_api.exception;

import com.publicholidays.holiday_fetcher_api.controller.HolidayFetcherController;
import com.publicholidays.holiday_fetcher_api.service.HolidayFetcherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class HolidayExceptionHandlerTest {

    @Mock
    private HolidayFetcherService holidayFetcherService;

    @InjectMocks
    private HolidayFetcherController holidayFetcherController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(holidayFetcherController)
                .setControllerAdvice(new HolidayExceptionHandler())
                .build();
    }

    @Test
    public void testHandleHolidayNotFoundException() throws Exception {
        when(holidayFetcherService.getLastThreeCelebratedHolidays("invalidCountry", 2024))
                .thenThrow(new HolidayServiceExceptions.HolidayNotFoundException("Holiday not found"));

        mockMvc.perform(get("/holidays/last")
                        .param("country", "invalidCountry")
                        .param("year", "2024")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Expect 404 NOT FOUND
                .andExpect(jsonPath("$.message").value("Holiday not found"))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.NOT_FOUND.getReasonPhrase()));
    }

    @Test
    public void testHandleBadRequestException() throws Exception {
        when(holidayFetcherService.getLastThreeCelebratedHolidays("XX", 2024))
                .thenThrow(new HolidayServiceExceptions.BadRequestException("Invalid country code"));

        mockMvc.perform(get("/holidays/last")
                        .param("country", "XX")
                        .param("year", "2024")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid country code"))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.getReasonPhrase()));
    }

    @Test
    public void testHandleUnknownErrorException() throws Exception {
        when(holidayFetcherService.getLastThreeCelebratedHolidays("validCountry", 2024))
                .thenThrow(new HolidayServiceExceptions.UnknownErrorException("Unexpected error occurred"));

        mockMvc.perform(get("/holidays/last")
                        .param("country", "validCountry")
                        .param("year", "2024")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // Expect 500 INTERNAL SERVER ERROR
                .andExpect(jsonPath("$.message").value("Unexpected error occurred"))
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
    }
}
