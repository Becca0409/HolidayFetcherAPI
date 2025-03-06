package com.publicholidays.holiday_fetcher_api.service;

import com.publicholidays.holiday_fetcher_api.dto.response.HolidayDTO;
import java.util.List;

public interface HolidayFetcherService {

    public List<HolidayDTO> getLastThreeCelebratedHolidays(String country, int year);

    public int getNumberOfHolidaysNotOnWeekends(String country, int year);

    public List<HolidayDTO> getHolidaysInCommon(String country1, String country2, int year);
}
