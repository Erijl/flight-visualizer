package com.erijl.flightvisualizer.backend.service;

import com.erijl.flightvisualizer.backend.builder.FlightDateFrequencyBuilder;
import com.erijl.flightvisualizer.backend.model.projections.FlightDateFrequencyProjection;
import com.erijl.flightvisualizer.backend.model.repository.FlightScheduleOperationPeriodRepository;
import com.erijl.flightvisualizer.protos.objects.FlightDateFrequency;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightScheduleOperationPeriodService {

    private final FlightScheduleOperationPeriodRepository flightScheduleOperationPeriodRepository;

    public FlightScheduleOperationPeriodService(FlightScheduleOperationPeriodRepository flightScheduleOperationPeriodRepository) {
        this.flightScheduleOperationPeriodRepository = flightScheduleOperationPeriodRepository;
    }

    public List<FlightDateFrequency> getFlightDateFrequency() {
        List<FlightDateFrequencyProjection> flightDateFrequencyProjectionList = flightScheduleOperationPeriodRepository.getFlightDateFrequency();
        System.out.println(flightDateFrequencyProjectionList.get(0).getStartDateUtc());

        return FlightDateFrequencyBuilder.buildFLightDateFrequencyList(flightDateFrequencyProjectionList);
    }
}
