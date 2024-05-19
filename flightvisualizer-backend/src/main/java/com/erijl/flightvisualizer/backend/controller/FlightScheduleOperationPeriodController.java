package com.erijl.flightvisualizer.backend.controller;

import com.erijl.flightvisualizer.backend.service.FlightScheduleOperationPeriodService;
import com.erijl.flightvisualizer.protos.objects.FlightDateFrequencies;
import com.erijl.flightvisualizer.protos.objects.FlightDateFrequency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class FlightScheduleOperationPeriodController {

    private final FlightScheduleOperationPeriodService flightScheduleOperationPeriodService;

    public FlightScheduleOperationPeriodController(FlightScheduleOperationPeriodService flightScheduleOperationPeriodService) {
        this.flightScheduleOperationPeriodService = flightScheduleOperationPeriodService;
    }

    @GetMapping(value = "/flightdatefrequency", produces = "application/x-protobuf")
    public ResponseEntity<FlightDateFrequencies> getDistinctFlightScheduleLegsForRendering() {
        try {
            List<FlightDateFrequency> flightDateFrequencies = flightScheduleOperationPeriodService.getFlightDateFrequency();
            return ResponseEntity.ok().body(FlightDateFrequencies.newBuilder().addAllFrequencies(flightDateFrequencies).build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(FlightDateFrequencies.getDefaultInstance());
        } catch (Exception e) {
            log.error("Error processing request", e);
            return ResponseEntity.internalServerError().body(FlightDateFrequencies.getDefaultInstance());
        }
    }
}
