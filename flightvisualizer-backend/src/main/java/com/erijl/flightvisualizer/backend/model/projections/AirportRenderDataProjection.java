package com.erijl.flightvisualizer.backend.model.projections;

public interface AirportRenderDataProjection {
    String getIataCode();
    String getAirportName();
    Double getLongitude();
    Double getLatitude();
}
