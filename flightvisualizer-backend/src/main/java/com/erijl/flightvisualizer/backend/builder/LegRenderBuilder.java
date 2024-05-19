package com.erijl.flightvisualizer.backend.builder;

import com.erijl.flightvisualizer.backend.model.projections.LegRenderDataProjection;
import com.erijl.flightvisualizer.protos.objects.Coordinate;
import com.erijl.flightvisualizer.protos.objects.LegRender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LegRenderBuilder {

    private LegRenderBuilder() {
    }

    public static LegRender buildLegRender(LegRenderDataProjection legRenderDataProjection) {
        return LegRender.newBuilder()
                .setOriginAirportIataCode(legRenderDataProjection.getOriginAirportIataCode())
                .setDestinationAirportIataCode(legRenderDataProjection.getDestinationAirportIataCode())
                .setDurationMinutes(legRenderDataProjection.getDurationMinutes())
                .setDistanceKilometers(legRenderDataProjection.getDistanceKilometers())
                .addCoordinates(0,
                        Coordinate.newBuilder()
                                .setLatitude(legRenderDataProjection.getOriginLatitude())
                                .setLongitude(legRenderDataProjection.getOriginLongitude())
                )
                .addCoordinates(1,
                        Coordinate.newBuilder()
                                .setLatitude(legRenderDataProjection.getDestinationLatitude())
                                .setLongitude(legRenderDataProjection.getDestinationLongitude())
                )
                .setDetails(LegRender.Details.newBuilder()
                        .setOriginAirportName(legRenderDataProjection.getOriginAirportName())
                        .setDestinationAirportName(legRenderDataProjection.getDestinationAirportName())
                        .setDepartureTimeUtc(legRenderDataProjection.getAircraftDepartureTimeUtc())
                        .setArrivalTimeUtc(legRenderDataProjection.getAircraftArrivalTimeUtc())
                        .build()
                ).build();

    }

    public static List<LegRender> buildLegRenderList(List<LegRenderDataProjection> legRenderDataProjections) {
        return legRenderDataProjections.stream()
                .map(LegRenderBuilder::buildLegRender)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
