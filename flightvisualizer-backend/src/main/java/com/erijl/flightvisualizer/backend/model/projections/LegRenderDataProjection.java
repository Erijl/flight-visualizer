package com.erijl.flightvisualizer.backend.model.projections;

public interface LegRenderDataProjection {
    String getOriginAirportIataCode();
    String getDestinationAirportIataCode();
    Double getOriginLongitude();
    Double getOriginLatitude();
    Double getDestinationLongitude();
    Double getDestinationLatitude();
}