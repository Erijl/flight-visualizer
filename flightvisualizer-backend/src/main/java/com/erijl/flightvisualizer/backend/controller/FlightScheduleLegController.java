package com.erijl.flightvisualizer.backend.controller;

import com.erijl.flightvisualizer.backend.dto.FlightScheduleLegDto;
import com.erijl.flightvisualizer.backend.repository.FlightScheduleLegRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlightScheduleLegController {

    private final FlightScheduleLegRepository flightScheduleLegRepository;

    public FlightScheduleLegController(FlightScheduleLegRepository flightScheduleLegRepository) {
        this.flightScheduleLegRepository = flightScheduleLegRepository;
    }

    @RequestMapping("/flightScheduleLegs")
    public Iterable<FlightScheduleLegDto> getFlightScheduleLegs() {
        return flightScheduleLegRepository.findAllWithoutAssociations();
    }
}
