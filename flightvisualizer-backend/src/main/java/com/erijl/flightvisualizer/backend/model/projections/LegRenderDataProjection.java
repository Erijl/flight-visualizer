package com.erijl.flightvisualizer.backend.model.projections;

public interface LegRenderDataProjection {
    String getOriginAirportIataCode();
    String getDestinationAirportIataCode();
    Double getOriginLongitude();
    Double getOriginLatitude();
    Double getDestinationLongitude();
    Double getDestinationLatitude();
    String getOriginIsoCountryCode();
    String getDestinationIsoCountryCode();
    String getOriginTimezoneId();
    String getDestinationTimezoneId();
    String getOriginOffsetUtc();
    String getDestinationOffsetUtc();
    Integer getAircraftDepartureTimeDateDiffUtc();
    Integer getAircraftArrivalTimeDateDiffUtc();
    Integer getAircraftArrivalTimeUtc();
    Integer getAircraftDepartureTimeUtc();
    Integer getDurationMinutes();
    Integer getDistanceKilometers();
}