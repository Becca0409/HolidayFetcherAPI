package com.publicholidays.holiday_fetcher_api.controller;

import com.publicholidays.holiday_fetcher_api.dto.response.HolidayDTO;
import com.publicholidays.holiday_fetcher_api.service.HolidayFetcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/holidays")
public class HolidayFetcherController {
    @Autowired
    private final HolidayFetcherService holidayFetcherService;

    public HolidayFetcherController(HolidayFetcherService holidayFetcherService) {
        this.holidayFetcherService = holidayFetcherService;
    }

    @GetMapping("/last")
    public List<HolidayDTO> getLastThreeCelebratedHolidays(@RequestParam String country, @RequestParam int year) {
        return holidayFetcherService.getLastThreeCelebratedHolidays(country, year);
    }

    @GetMapping("/non-weekends")
    public List<Integer> getNumberOfHolidaysNotOnWeekends(@RequestParam int year, @RequestParam List<String> countries) {
            return countries.stream()
                    .map(country -> holidayFetcherService.getNumberOfHolidaysNotOnWeekends(country, year))
                    .sorted((a, b) -> b - a)
                    .collect(Collectors.toList());

    }

    @GetMapping("/common")
    public List<HolidayDTO> getCommonHolidays(@RequestParam int year, @RequestParam String country1, @RequestParam String country2) {
        return holidayFetcherService.getHolidaysInCommon(country1, country2, year);
    }
}
