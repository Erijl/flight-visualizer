package com.erijl.flightvisualizer.backend.builder;

import com.erijl.flightvisualizer.backend.model.projections.AirportRenderDataProjection;
import com.erijl.flightvisualizer.protos.objects.AirportRender;
import com.erijl.flightvisualizer.protos.objects.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AirportRenderBuilder {

    private AirportRenderBuilder() {
    }

    /**
     * Build an {@link AirportRender} object from an {@link AirportRenderDataProjection} projection
     *
     * @param airportProjection the airport projection
     * @return the AirportRender object
     */
    public static AirportRender buildAirportRender(AirportRenderDataProjection airportProjection) {
        return AirportRender.newBuilder()
                .setIataCode(airportProjection.getIataCode())
                .setName(airportProjection.getAirportName())
                .setCoordinate(Coordinate.newBuilder()
                        .setLongitude(airportProjection.getLongitude())
                        .setLatitude(airportProjection.getLatitude())
                        .build()
                ).build();
    }

    /**
     * Build a list of {@link AirportRender} objects from a list of {@link AirportRenderDataProjection} projections
     *
     * @param airportProjections the list of airport projections
     * @return the list of AirportRender objects
     */
    public static List<AirportRender> buildAirportRenderList(List<AirportRenderDataProjection> airportProjections) {
        return airportProjections.stream()
                .map(AirportRenderBuilder::buildAirportRender)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
