package com.erijl.flightvisualizer.backend.controller;

import com.erijl.flightvisualizer.backend.dto.FlightScheduleLegDto;
import com.erijl.flightvisualizer.backend.dto.FlightScheduleLegWithDistance;
import com.erijl.flightvisualizer.backend.repository.FlightScheduleLegRepository;
import com.erijl.flightvisualizer.backend.service.FlightScheduleLegService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FlightScheduleLegController {


    private final FlightScheduleLegService flightScheduleLegService;

    public FlightScheduleLegController(FlightScheduleLegService flightScheduleLegService) {
        this.flightScheduleLegService = flightScheduleLegService;
    }

    @RequestMapping("/flightScheduleLegs")
    public Iterable<FlightScheduleLegDto> getFlightScheduleLegs() {
        return flightScheduleLegService.getFlightScheduleLegs();
    }

    @RequestMapping("/flightScheduleLeg/distance")
    public List<FlightScheduleLegWithDistance> getFlightScheduleLegsWithDistance() {
        return flightScheduleLegService.getFlightScheduleLegsWithDistance();
    }
}
