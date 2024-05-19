package com.erijl.flightvisualizer.backend.builder;

import com.erijl.flightvisualizer.backend.model.projections.FlightDateFrequencyProjection;
import com.erijl.flightvisualizer.backend.util.CustomTimeUtil;
import com.erijl.flightvisualizer.protos.objects.FlightDateFrequency;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlightDateFrequencyBuilder {

    private FlightDateFrequencyBuilder() {
    }

    public static FlightDateFrequency buildFlightDateFrequency(FlightDateFrequencyProjection flightDateFrequencyProjection) {
        return FlightDateFrequency.newBuilder()
                .setDate(CustomTimeUtil.convertLocalDateToProtoTimestamp(flightDateFrequencyProjection.getStartDateUtc().plusDays(1))) //don't ask, db is in weird timezone
                .setFrequency(flightDateFrequencyProjection.getFlightCount())
                .build();
    }

    public static List<FlightDateFrequency> buildFLightDateFrequencyList(List<FlightDateFrequencyProjection> flightDateFrequencyProjectionList) {
        return flightDateFrequencyProjectionList.stream()
                .map(FlightDateFrequencyBuilder::buildFlightDateFrequency)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
