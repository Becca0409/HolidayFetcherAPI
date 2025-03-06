package com.publicholidays.holiday_fetcher_api.controller;

import com.publicholidays.holiday_fetcher_api.dto.response.HolidayDTO;
import com.publicholidays.holiday_fetcher_api.service.HolidayFetcherService;
import com.publicholidays.holiday_fetcher_api.service.impl.HolidayFetcherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class HolidayFetcherControllerTest {

    @Mock
    private HolidayFetcherService holidayFetcherService;

    private MockMvc mockMvc;

    @InjectMocks
    private HolidayFetcherController holidayFetcherController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(holidayFetcherController).build();
    }

    @Test
    public void testGetLastCelebratedHolidays() throws Exception {
        HolidayDTO holiday1 = new HolidayDTO("2024-12-25", "Christmas Day");
        HolidayDTO holiday2 = new HolidayDTO("2024-11-28", "Thanksgiving Day");
        HolidayDTO holiday3 = new HolidayDTO("2024-11-11", "Veterans Day");
        List<HolidayDTO> holidays = Arrays.asList(holiday1, holiday2, holiday3);

        when(holidayFetcherService.getLastThreeCelebratedHolidays("US", 2024)).thenReturn(holidays);

        mockMvc.perform(get("/holidays/last")
                .param("country", "US")
                .param("year", "2024")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[" +
                        "{'date': '2024-12-25', 'localName': 'Christmas Day'}, " +
                        "{'date': '2024-11-28', 'localName': 'Thanksgiving Day'}, " +
                        "{'date': '2024-11-11', 'localName': 'Veterans Day'}]"));
    }

    @Test
    public void testGetNumberOfHolidaysNotOnWeekends() throws Exception {
        when(holidayFetcherService.getNumberOfHolidaysNotOnWeekends("US", 2023)).thenReturn(5);

        mockMvc.perform(get("/holidays/non-weekends")
                        .param("year", "2023")
                        .param("countries", "US")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[5]"));
    }

    @Test
    public void testGetCommonHolidays() throws Exception {
        HolidayDTO holiday = new HolidayDTO("2023-01-01", "New Year");
        List<HolidayDTO> commonHolidays = Arrays.asList(holiday);

        when(holidayFetcherService.getHolidaysInCommon("US", "CA", 2023)).thenReturn(commonHolidays);

        mockMvc.perform(get("/holidays/common")
                        .param("year", "2023")
                        .param("country1", "US")
                        .param("country2", "CA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{'date': '2023-01-01', 'localName': 'New Year'}]"));
    }
}