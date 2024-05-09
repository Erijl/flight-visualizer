package com.erijl.flightvisualizer.backend.service;

import com.erijl.flightvisualizer.backend.model.dtos.FlightDateFrequencyDto;
import com.erijl.flightvisualizer.backend.model.repository.FlightScheduleOperationPeriodRepository;
import org.springframework.stereotype.Service;

@Service
public class FlightScheduleOperationPeriodService {

    private final FlightScheduleOperationPeriodRepository flightScheduleOperationPeriodRepository;

    public FlightScheduleOperationPeriodService(FlightScheduleOperationPeriodRepository flightScheduleOperationPeriodRepository) {
        this.flightScheduleOperationPeriodRepository = flightScheduleOperationPeriodRepository;
    }

    public Iterable<FlightDateFrequencyDto> getFlightDateFrequency() {
        return flightScheduleOperationPeriodRepository.getFlightDateFrequency();
    }
}
