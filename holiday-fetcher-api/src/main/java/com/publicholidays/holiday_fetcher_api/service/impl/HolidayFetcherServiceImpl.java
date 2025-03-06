package com.publicholidays.holiday_fetcher_api.service.impl;

import com.publicholidays.holiday_fetcher_api.exception.HolidayServiceExceptions;
import com.publicholidays.holiday_fetcher_api.dto.response.HolidayDTO;
import com.publicholidays.holiday_fetcher_api.service.HolidayFetcherService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Service
public class HolidayFetcherServiceImpl implements HolidayFetcherService {
    private final RestTemplate restTemplate;

    //Constructor Injection of Rest Template.
    public HolidayFetcherServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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

    //Validation method for country codes.
    private void validateCountryCode(String country) {
        if (!validCountryCodes.contains(country)) {
            throw new HolidayServiceExceptions.BadRequestException("One or both country codes are invalid. Country codes must be composed of two valid characters in capital letters.");
        }
    }

    //Validation method for years: also handles the case in which the year selected is in the future.
    private void validateYear(int year) {
        int currentYear = LocalDate.now().getYear();
        if (year < 1000 || year > 9999) {
            throw new HolidayServiceExceptions.BadRequestException("Invalid year format: " + year);
        }
        if (year > currentYear) {
            throw new HolidayServiceExceptions.BadRequestException("The year cannot be in the future: " + year);
        }
    }

    //Method to retrieve the last 3 holidays given a country and a year.
    public List<HolidayDTO> getLastThreeCelebratedHolidays(String country, int year) {
        validateCountryCode(country);
        validateYear(year);
        String url = "https://date.nager.at/api/v3/publicholidays/" + year + "/" + country;

        try {
            HolidayDTO[] response = restTemplate.getForObject(url, HolidayDTO[].class);
            if (response != null) {
                if (response.length == 0) {
                    throw new HolidayServiceExceptions.HolidayNotFoundException("No holidays found for the given country and year.");
                }

                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

                // Filter holidays by valid date and past dates, then sort them in descending order.
                return Arrays.stream(response)
                        .filter(holiday -> holiday.getDate() != null)
                        .filter(holiday -> {
                            LocalDate holidayDate = LocalDate.parse(holiday.getDate(), formatter);
                            return holidayDate.isBefore(LocalDate.now()); // Only include past holidays.
                        })
                        .sorted((h1, h2) -> {
                            LocalDate date1 = LocalDate.parse(h1.getDate(), formatter);
                            LocalDate date2 = LocalDate.parse(h2.getDate(), formatter);
                            return date2.compareTo(date1); // Sort in descending order by date,
                        })
                        .limit(3) // Limit to the last 3 holidays.
                        .collect(Collectors.toList());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                throw new HolidayServiceExceptions.HolidayNotFoundException("No holidays found for the given country and year.");
            } else if (e.getStatusCode().value() == 400) {
                throw new HolidayServiceExceptions.BadRequestException("Invalid request. Please insert valid data input.");
            } else if (e.getStatusCode().value() == 500) {
                throw new HolidayServiceExceptions.UnknownErrorException("An unexpected error occurred while retrieving holidays.");
            }
        }
        return null;
    }



    public int getNumberOfHolidaysNotOnWeekends (String country, int year){
        validateCountryCode(country);
        validateYear(year);
        String url = "https://date.nager.at/api/v3/publicholidays/" + year + "/" + country;
        try {
            HolidayDTO[] response = restTemplate.getForObject(url, HolidayDTO[].class);
            if (response != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
                return (int) Arrays.stream(response)
                        .filter(holiday -> {
                            LocalDate date = LocalDate.parse(holiday.getDate(), formatter);
                            int dayOfWeek = date.getDayOfWeek().getValue();
                            return dayOfWeek != 6 && dayOfWeek != 7;
                        })
                        .count();
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                throw new HolidayServiceExceptions.HolidayNotFoundException("No holidays found for the given country and year.");
            } else if (e.getStatusCode().value() == 500){
                throw new HolidayServiceExceptions.UnknownErrorException("An unexpected error occurred while retrieving holidays.");
            }
        }
        return 0;
    }

    public List<HolidayDTO> getHolidaysInCommon(String country1, String country2, int year) {
        //The countries and year have already been validated in getHolidaysByCountryAndYear.
        if (Objects.equals(country1, country2)) {
            throw new HolidayServiceExceptions.BadRequestException("You cannot compare the same country!");
        }
        List<HolidayDTO> holidays1;
        List<HolidayDTO> holidays2;

        try {
            holidays1 = getHolidaysByCountryAndYear(country1, year);
            holidays2 = getHolidaysByCountryAndYear(country2, year);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                throw new HolidayServiceExceptions.HolidayNotFoundException("Holidays not found for one or both countries: " + country1 + ", " + country2);
            } else {
                throw new HolidayServiceExceptions.UnknownErrorException("An unexpected error occurred while retrieving holidays.");
            }
        }
        if (holidays1 == null || holidays2 == null) {
            throw new HolidayServiceExceptions.BadRequestException("Holidays list is null for one or both countries: " + country1 + ", " + country2);
        }

        return holidays1.stream()
                .filter(holiday1 -> holidays2
                .stream()
                .anyMatch(holiday2 -> holiday1
                .getDate()
                .equals(holiday2.getDate())))
                .collect(Collectors.toList());

    }
    public List<HolidayDTO> getHolidaysByCountryAndYear (String country, int year){
        validateCountryCode(country);
        validateYear(year);
        String url = "https://date.nager.at/api/v3/publicholidays/" + year + "/" + country;
        try {
            HolidayDTO[] response = restTemplate.getForObject(url, HolidayDTO[].class);
            if (response != null) {
                return Arrays.asList(response);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                throw new HolidayServiceExceptions.HolidayNotFoundException("No holidays found for the given country and year.");
            } else {
                throw new HolidayServiceExceptions.UnknownErrorException("An unexpected error occurred while retrieving holidays.");
            }
        }
        return null;
    }
}

