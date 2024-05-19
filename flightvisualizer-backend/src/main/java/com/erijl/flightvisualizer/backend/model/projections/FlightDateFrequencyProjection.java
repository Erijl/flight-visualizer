package com.erijl.flightvisualizer.backend.model.projections;

import java.time.LocalDate;

public interface FlightDateFrequencyProjection {

    LocalDate getStartDateUtc();
    Integer getFlightCount();
}
