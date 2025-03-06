package com.publicholidays.holiday_fetcher_api.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HolidayDTOTest {

    @Test
    public void testConstructorAndGetters() {

        String date = "2023-01-01";
        String localName = "New Year";


        HolidayDTO holidayDTO = new HolidayDTO(date, localName);


        assertEquals(date, holidayDTO.getDate());
        assertEquals(localName, holidayDTO.getLocalName());
    }

    @Test
    public void testSetters() {
        HolidayDTO holidayDTO = new HolidayDTO("2023-01-01", "New Year");

        holidayDTO.setDate("2024-12-25");
        holidayDTO.setLocalName("Christmas");

        assertEquals("2024-12-25", holidayDTO.getDate());
        assertEquals("Christmas", holidayDTO.getLocalName());
    }

}
