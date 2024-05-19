package com.erijl.flightvisualizer.backend.model.projections;

public interface FlightDateFrequencyProjection {
    String getStartDateUtc();
    Integer getFlightCount();
}
