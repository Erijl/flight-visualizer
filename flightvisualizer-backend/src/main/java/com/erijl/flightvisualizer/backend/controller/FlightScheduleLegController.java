package com.erijl.flightvisualizer.backend.controller;

import com.erijl.flightvisualizer.backend.model.dto.FlightScheduleLegDto;
import com.erijl.flightvisualizer.backend.model.dto.FlightScheduleLegWithDistance;
import com.erijl.flightvisualizer.backend.service.FlightScheduleLegService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FlightScheduleLegController {


    private final FlightScheduleLegService flightScheduleLegService;

    public FlightScheduleLegController(FlightScheduleLegService flightScheduleLegService) {
        this.flightScheduleLegService = flightScheduleLegService;
    }

    @RequestMapping("/flightScheduleLegs")
    public Iterable<FlightScheduleLegDto> getFlightScheduleLegs(@RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate) {
        return flightScheduleLegService.getFlightScheduleLegs(startDate, endDate);
    }

    @RequestMapping("/flightScheduleLeg/distance")
    public List<FlightScheduleLegWithDistance> getFlightScheduleLegsWithDistance(@RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate) {
        return flightScheduleLegService.getFlightScheduleLegsWithDistance(startDate, endDate);
    }
}
