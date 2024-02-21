package com.erijl.flightvisualizer.backend.service;

import com.erijl.flightvisualizer.backend.dto.FlightScheduleLegDto;
import com.erijl.flightvisualizer.backend.repository.FlightScheduleLegRepository;
import org.springframework.stereotype.Service;

@Service
public class FlightScheduleLegService {

    private final FlightScheduleLegRepository flightScheduleLegRepository;

    public FlightScheduleLegService(FlightScheduleLegRepository flightScheduleLegRepository) {
        this.flightScheduleLegRepository = flightScheduleLegRepository;
    }

    public Iterable<FlightScheduleLegDto> getFlightScheduleLegs() {
        return flightScheduleLegRepository.findAllWithoutAssociations();
    }
}
