package com.erijl.flightvisualizer.backend.builder;

import com.erijl.flightvisualizer.backend.model.entities.Airport;
import com.erijl.flightvisualizer.protos.objects.AirportDetails;
import com.erijl.flightvisualizer.protos.objects.Coordinate;

public class AirportDetailsBuilder {

    private AirportDetailsBuilder() {
    }

    /**
     * Build an {@link AirportDetails} object from an {@link Airport} entity
     *
     * @param airport the airport entity
     * @return the AirportDetails object
     */
    public static AirportDetails buildAirportDetails(Airport airport) {
        return AirportDetails.newBuilder()
                .setIataCode(airport.getIataAirportCode())
                .setName(airport.getAirportName())
                .setCoordinate(
                        Coordinate.newBuilder()
                                .setLongitude(airport.getLongitude().doubleValue())
                                .setLatitude(airport.getLatitude().doubleValue())
                                .build()
                )
                .setIataCityCode(airport.getIataCityCode())
                .setIsoCountryCode(airport.getIsoCountryCode())
                .setOffsetUtc(airport.getOffsetUtc())
                .setTimezoneId(airport.getTimezoneId())
                .build();
    }
}
