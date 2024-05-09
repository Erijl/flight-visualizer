package com.erijl.flightvisualizer.backend.controller;

import com.erijl.flightvisualizer.backend.model.dtos.FlightDateFrequencyDto;
import com.erijl.flightvisualizer.backend.service.FlightScheduleOperationPeriodService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlightScheduleOperationPeriodController {

    private final FlightScheduleOperationPeriodService flightScheduleOperationPeriodService;

    public FlightScheduleOperationPeriodController(FlightScheduleOperationPeriodService flightScheduleOperationPeriodService) {
        this.flightScheduleOperationPeriodService = flightScheduleOperationPeriodService;
    }

    @GetMapping("/flightdatefrequency")
    public Iterable<FlightDateFrequencyDto> getFlightDateFrequency() {
        return flightScheduleOperationPeriodService.getFlightDateFrequency();
    }
}
