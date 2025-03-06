package com.publicholidays.holiday_fetcher_api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HolidayDTO {

    @JsonProperty
    private String date;
    @JsonProperty
    private String localName;

    public HolidayDTO(String date, String localName) {
        this.date = date;
        this.localName = localName;
    }
}
