package com.erijl.flightvisualizer.backend.controller;

import com.erijl.flightvisualizer.backend.model.FlightSchedule;
import com.erijl.flightvisualizer.backend.service.FlightScheduleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlightScheduleController {

    private final FlightScheduleService flightScheduleService;

    public FlightScheduleController(FlightScheduleService flightScheduleService) {
        this.flightScheduleService = flightScheduleService;
    }

    @GetMapping("/flightschedule")
    public FlightSchedule getFlightScheduleById(@RequestParam("id") int id) {
        return flightScheduleService.getFlightScheduleById(id);
    }
}
