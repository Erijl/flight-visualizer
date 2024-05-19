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

    public static AirportRender buildAirportRender(AirportRenderDataProjection airportProjection) {
        return AirportRender.newBuilder()
                .setIataCode(airportProjection.getIataCode())
                .setCoordinate(Coordinate.newBuilder()
                        .setLongitude(airportProjection.getLongitude())
                        .setLatitude(airportProjection.getLatitude())
                        .build()
                ).build();
    }

    public static List<AirportRender> buildAirportRenderList(List<AirportRenderDataProjection> airportProjections) {
        return airportProjections.stream()
                .map(AirportRenderBuilder::buildAirportRender)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
