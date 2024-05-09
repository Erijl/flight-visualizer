package com.erijl.flightvisualizer.backend.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class FlightDateFrequencyDto {

    private Date startDateUtc;
    private Long count;

    public FlightDateFrequencyDto(Date startDateUtc, Long count) {
        this.startDateUtc = startDateUtc;
        this.count = count;
    }
}
