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

    /**
     * Build a {@link LegRender} object from a {@link LegRenderDataProjection} projection
     *
     * @param legRenderDataProjection the leg projection
     * @return the LegRender object
     */
    public static LegRender buildLegRender(LegRenderDataProjection legRenderDataProjection) {
        return LegRender.newBuilder()
                .setLegId(legRenderDataProjection.getLegId())
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

    /**
     * Build a list of {@link LegRender} objects from a list of {@link LegRenderDataProjection} projections
     *
     * @param legRenderDataProjections the list of leg projections
     * @return the list of LegRender objects
     */
    public static List<LegRender> buildLegRenderList(List<LegRenderDataProjection> legRenderDataProjections) {
        return legRenderDataProjections.stream()
                .map(LegRenderBuilder::buildLegRender)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
