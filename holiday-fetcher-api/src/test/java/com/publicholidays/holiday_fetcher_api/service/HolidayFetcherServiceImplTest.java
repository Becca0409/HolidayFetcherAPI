package com.publicholidays.holiday_fetcher_api.service;

import com.publicholidays.holiday_fetcher_api.dto.response.HolidayDTO;
import com.publicholidays.holiday_fetcher_api.exception.HolidayServiceExceptions;
import com.publicholidays.holiday_fetcher_api.service.impl.HolidayFetcherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HolidayFetcherServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HolidayFetcherServiceImpl holidayFetcherServiceImpl;

    @BeforeEach
    void setUp() {
        assertNotNull(restTemplate);
        assertNotNull(holidayFetcherServiceImpl);
    }
    private final Set<String> validCountryCodes = Set.of(
            "AD", "AL", "AM", "AR", "AT", "AU", "AX", "BA", "BB", "BE", "BG", "BJ", "BO", "BR", "BS", "BW", "BY", "BZ",
            "CA", "CH", "CL", "CN", "CO", "CR", "CU", "CY", "CZ", "DE", "DK", "DO", "EC", "EE", "EG", "ES", "FI", "FO",
            "FR", "GA", "GB", "GD", "GE", "GG", "GI", "GL", "GM", "GR", "GT", "GU", "GY", "HK", "HN", "HR", "HT", "HU",
            "ID", "IE", "IL", "IM", "IN", "IS", "IT", "JE", "JM", "JP", "KZ", "LB", "LI", "LK", "LT", "LU", "LV", "MA",
            "MC", "MD", "ME", "MG", "MK", "MN", "MO", "MS", "MT", "MU", "MV", "MX", "MY", "MZ", "NA", "NE", "NG", "NI",
            "NL", "NO", "NZ", "PA", "PE", "PG", "PH", "PK", "PL", "PR", "PT", "PY", "RO", "RS", "RU", "SG", "SI", "SK",
            "SM", "SR", "SV", "SY", "TH", "TN", "TR", "TT", "UA", "US", "UY", "VA", "VE", "VN", "ZA"
    );



    @Test
    void getLastThreeCelebratedHolidaysSuccess() {
        String country = "US";
        int year = 2023;

        HolidayDTO[] holidays = new HolidayDTO[] {
                new HolidayDTO("2023-01-01", "New Year's Day"),
                new HolidayDTO("2023-02-14", "Valentine's Day"),
                new HolidayDTO("2023-04-01", "April Fool's Day")
        };

        when(restTemplate.getForObject(anyString(), eq(HolidayDTO[].class)))
                .thenReturn(holidays);

        List<HolidayDTO> result = holidayFetcherServiceImpl.getLastThreeCelebratedHolidays(country, year);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("April Fool's Day", result.get(0).getLocalName());
    }

    @Test
    void getLastThreeCelebratedHolidaysInvalidCountryCode() {
        int year = LocalDate.now().getYear();
        String country = "XX";
        assertThrows(HolidayServiceExceptions.BadRequestException.class, () ->
                holidayFetcherServiceImpl.getLastThreeCelebratedHolidays(country, 3),
                "One or both country codes are invalid. Country codes must be composed of two valid characters in capital letters."
        );
    }

    @Test
    public void getLastThreeCelebratedHolidaysInvalidYear() {
        int futureYear = LocalDate.now().getYear() + 1;
        assertThrows(HolidayServiceExceptions.BadRequestException.class, () ->
                        holidayFetcherServiceImpl.getLastThreeCelebratedHolidays("US", futureYear),
                "The year cannot be in the future: " + futureYear
        );
    }

    @Test
    void getLastThreeCelebratedHolidaysNotFound() {
            String country = "US";
            int year = 2024;
            when(restTemplate.getForObject(anyString(), eq(HolidayDTO[].class)))
                    .thenReturn(new HolidayDTO[0]);

            assertThrows(HolidayServiceExceptions.HolidayNotFoundException.class,
                    () -> holidayFetcherServiceImpl.getLastThreeCelebratedHolidays(country, year));
    }


    @Test
    void getLastThreeCelebratedHolidaysBadRequestError() {
        String country = "INVALID";
        int year = 2023;
        String expectedUrl = "https://date.nager.at/api/v3/publicholidays/2023/INVALID";

        lenient().when(restTemplate.getForObject(anyString(), eq(HolidayDTO[].class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(HolidayServiceExceptions.BadRequestException.class,
                () -> holidayFetcherServiceImpl.getLastThreeCelebratedHolidays(country, year));
    }

    @Test
    void getLastThreeCelebratedHolidaysUnknownError() {
        String country = "US";
        int year = 2023;

        when(restTemplate.getForObject(anyString(), eq(HolidayDTO[].class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(HolidayServiceExceptions.UnknownErrorException.class,
                () -> holidayFetcherServiceImpl.getLastThreeCelebratedHolidays(country, year));
    }

    @Test
    void getNumberOfHolidaysNotOnWeekendsValidCountryCodeAndYear() {
        String country = "US";
        int year = 2023;
        HolidayDTO[] holidays = new HolidayDTO[]{
                new HolidayDTO("2023-01-01", "New Year's Day"), // Sunday
                new HolidayDTO("2023-07-04", "Independence Day"), // Tuesday
                new HolidayDTO("2023-12-25", "Christmas Day") // Monday
        };
        when(restTemplate.getForObject(anyString(), eq(HolidayDTO[].class))).thenReturn(holidays);

        int result = holidayFetcherServiceImpl.getNumberOfHolidaysNotOnWeekends(country, year);

        assertEquals(2, result);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(HolidayDTO[].class));
    }

    @Test
    void getNumberOfHolidaysNotOnWeekendsInvalidYear() {
        String country = "US";
        int year = 123;
        assertThrows(HolidayServiceExceptions.BadRequestException.class, () ->
                holidayFetcherServiceImpl.getNumberOfHolidaysNotOnWeekends(country, year)
        );
    }

    @Test
    void getNumberOfHolidaysNotOnWeekendsNotFound() {
            // Given
            String country = "US";
            int year = 2023;

            // Mocking the RestTemplate to throw a 404 error
            Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(HolidayDTO[].class)))
                    .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

            // When & Then
            HolidayServiceExceptions.HolidayNotFoundException exception = assertThrows(
                    HolidayServiceExceptions.HolidayNotFoundException.class,
                    () -> holidayFetcherServiceImpl.getNumberOfHolidaysNotOnWeekends(country, year));

            // Asserting the exception message
            assertEquals("No holidays found for the given country and year.", exception.getMessage());
    }


    @Test
    void getNumberOfHolidaysNotOnWeekendsBadRequest() {
        String country = "US";
        lenient().when(restTemplate.getForObject(anyString(), eq(HolidayDTO[].class))).thenThrow(HolidayServiceExceptions.BadRequestException.class);

        assertThrows(HolidayServiceExceptions.BadRequestException.class, () ->
                holidayFetcherServiceImpl.getLastThreeCelebratedHolidays(country, 3)
        );
    }

    @Test
    void getNumberOfHolidaysNotOnWeekendsUnknownError() {
        String country = "US";
        int year = 2024;
        lenient().when(restTemplate.getForObject(anyString(), eq(HolidayDTO[].class))).thenThrow(HolidayServiceExceptions.UnknownErrorException.class);

        assertThrows(HolidayServiceExceptions.UnknownErrorException.class, () ->
                holidayFetcherServiceImpl.getLastThreeCelebratedHolidays(country, year)
        );
    }

    @Test
    void getHolidaysInCommonValidCountriesAndYear() {
        String country1 = "US";
        String country2 = "CA";
        int year = 2023;
        HolidayDTO[] holidaysUS = new HolidayDTO[]{
                new HolidayDTO("2023-01-01", "New Year's Day"),
                new HolidayDTO("2023-07-04", "Independence Day"),
                new HolidayDTO("2023-12-25", "Christmas Day")
        };
        HolidayDTO[] holidaysCA = new HolidayDTO[]{
                new HolidayDTO("2023-01-01", "New Year's Day"),
                new HolidayDTO("2023-07-01", "Canada Day"),
                new HolidayDTO("2023-12-25", "Christmas Day")
        };
        when(restTemplate.getForObject(anyString(), eq(HolidayDTO[].class)))
                .thenReturn(holidaysUS)
                .thenReturn(holidaysCA);

        List<HolidayDTO> result = holidayFetcherServiceImpl.getHolidaysInCommon(country1, country2, year);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(restTemplate, times(2)).getForObject(anyString(), eq(HolidayDTO[].class));
    }


    @Test
    void getHolidaysInCommonInvalidCountry() {
        String country1 = "US";
        String country2 = "XX";
        int year = 2023;
        assertThrows(HolidayServiceExceptions.BadRequestException.class, () ->
                holidayFetcherServiceImpl.getHolidaysInCommon(country1, country2, year)
        );
    }

    @Test
    void getHolidaysInCommonSameCountry() {
        String country1 = "US";
        String country2 = "US";
        int year = 2023;
        assertThrows(HolidayServiceExceptions.BadRequestException.class, () ->
                holidayFetcherServiceImpl.getHolidaysInCommon(country1, country2, year)
        );
    }

    @Test
    void getHolidaysInCommonNotFound() {
                // Given
                String country1 = "US";
                String country2 = "CA";
                int year = 2023;

                // Mock the behavior of getHolidaysByCountryAndYear to throw a 404 error
                Mockito.when(holidayFetcherServiceImpl.getHolidaysByCountryAndYear("US", year))
                        .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

                // When & Then
                HolidayServiceExceptions.HolidayNotFoundException exception = assertThrows(
                        HolidayServiceExceptions.HolidayNotFoundException.class,
                        () -> holidayFetcherServiceImpl.getHolidaysInCommon(country1, country2, year));

                // Assert that the exception message is as expected
                assertEquals("No holidays found for the given country and year.", exception.getMessage());

}

    @Test
    void getHolidaysInCommonBadRequest() {
        String country1 = "US";
        String country2 = "IT";
        int year = 123;
        lenient().when(restTemplate.getForObject(anyString(), eq(HolidayDTO[].class))).thenThrow(HolidayServiceExceptions.BadRequestException.class);

        assertThrows(HolidayServiceExceptions.BadRequestException.class, () ->
                holidayFetcherServiceImpl.getHolidaysInCommon(country1, country2, year)
        );
    }

    @Test
    void getHolidaysInCommonUnknownError() {
        String country1 = "US";
        String country2 = "CA";
        int year = 2023;

        Mockito.when(holidayFetcherServiceImpl.getHolidaysByCountryAndYear("US", year))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(HolidayServiceExceptions.UnknownErrorException.class,
                () -> holidayFetcherServiceImpl.getHolidaysInCommon(country1, country2, year));
    }
}
