package com.erijl.flightvisualizer.backend.builder;

import com.erijl.flightvisualizer.backend.model.projections.FlightDateFrequencyProjection;
import com.erijl.flightvisualizer.backend.util.TimeUtil;
import com.erijl.flightvisualizer.protos.objects.FlightDateFrequency;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlightDateFrequencyBuilder {

    private FlightDateFrequencyBuilder() {
    }

    /**
     * Build a {@link FlightDateFrequency} object from a {@link FlightDateFrequencyProjection} projection
     *
     * @param flightDateFrequencyProjection the flight date frequency projection
     * @return the FlightDateFrequency object
     */
    public static FlightDateFrequency buildFlightDateFrequency(FlightDateFrequencyProjection flightDateFrequencyProjection) {
        return FlightDateFrequency.newBuilder()
                .setDate(TimeUtil.convertLocalDateToProtoTimestamp(TimeUtil.convertyyyyMMddStringToUTCLocalDate(flightDateFrequencyProjection.getStartDateUtc())))
                .setFrequency(flightDateFrequencyProjection.getFlightCount())
                .build();
    }

    /**
     * Build a list of {@link FlightDateFrequency} objects from a list of {@link FlightDateFrequencyProjection} projections
     *
     * @param flightDateFrequencyProjectionList the list of flight date frequency projections
     * @return the list of FlightDateFrequency objects
     */
    public static List<FlightDateFrequency> buildFLightDateFrequencyList(List<FlightDateFrequencyProjection> flightDateFrequencyProjectionList) {
        return flightDateFrequencyProjectionList.stream()
                .map(FlightDateFrequencyBuilder::buildFlightDateFrequency)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
