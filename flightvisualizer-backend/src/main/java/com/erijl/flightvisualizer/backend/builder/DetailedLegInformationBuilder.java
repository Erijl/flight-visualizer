package com.erijl.flightvisualizer.backend.builder;

import com.erijl.flightvisualizer.backend.model.projections.LegRenderDataProjection;
import com.erijl.flightvisualizer.protos.objects.DetailedLegInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DetailedLegInformationBuilder {

    private DetailedLegInformationBuilder() {
    }

    /**
     * Build a {@link DetailedLegInformation} object from a {@link LegRenderDataProjection} projection
     *
     * @param airportProjection the leg projection
     * @return the DetailedLegInformation object
     */
    public static DetailedLegInformation buildDetailedLegInformation(LegRenderDataProjection airportProjection) {
        return DetailedLegInformation.newBuilder()
                .setDepartureTimeUtc(airportProjection.getAircraftDepartureTimeUtc())
                .setArrivalTimeUtc(airportProjection.getAircraftArrivalTimeUtc())
                .setAircraftDepartureTimeDateDiffUtc(airportProjection.getAircraftDepartureTimeDateDiffUtc())
                .setAircraftArrivalTimeDateDiffUtc(airportProjection.getAircraftArrivalTimeDateDiffUtc())
                .setOperationPeriodWeekdays(airportProjection.getOperationPeriodWeekdays())
                .setAircraftCode(airportProjection.getAircraftCode())
                .setAircraftName(airportProjection.getAircraftName() != null ? airportProjection.getAircraftName() : "")
                .setOperationDate(airportProjection.getOperationDate())
                .build();
    }

    /**
     * Build a list of {@link DetailedLegInformation} objects from a list of {@link LegRenderDataProjection} projections
     *
     * @param airportProjections the list of leg projections
     * @return the list of DetailedLegInformation objects
     */
    public static List<DetailedLegInformation> buildDetailedLegInformationList(List<LegRenderDataProjection> airportProjections) {
        return airportProjections.stream()
                .map(DetailedLegInformationBuilder::buildDetailedLegInformation)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
