package com.erijl.flightvisualizer.backend.builder;

import com.erijl.flightvisualizer.backend.model.projections.LegRenderDataProjection;
import com.erijl.flightvisualizer.protos.objects.DetailedLegInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DetailedLegInformationBuilder {

    private DetailedLegInformationBuilder() {
    }

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

    public static List<DetailedLegInformation> buildDetailedLegInformationList(List<LegRenderDataProjection> airportProjections) {
        return airportProjections.stream()
                .map(DetailedLegInformationBuilder::buildDetailedLegInformation)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
