package com.erijl.flightvisualizer.backend.controller;

import com.erijl.flightvisualizer.backend.model.dtos.FlightScheduleLegDto;
import com.erijl.flightvisualizer.backend.model.dtos.FlightScheduleLegWithDistance;
import com.erijl.flightvisualizer.backend.service.FlightScheduleLegService;
import com.erijl.flightvisualizer.protos.objects.LegRender;
import com.erijl.flightvisualizer.protos.objects.LegRenders;
import com.erijl.flightvisualizer.protos.objects.TimeFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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

    @PostMapping(value = "/flightScheduleLeg/distinct", produces = "application/x-protobuf", consumes = "application/x-protobuf")
    public ResponseEntity<LegRenders> getDistinctFlightScheduleLegsForRendering(@RequestBody TimeFilter timeFilter) {
        try {
            List<LegRender> legRenders = flightScheduleLegService.getDistinctFlightScheduleLegsForRendering(timeFilter);
            LegRenders response = LegRenders.newBuilder().addAllLegs(legRenders).build();
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(LegRenders.getDefaultInstance());
        } catch (Exception e) {
            log.error("Error processing request", e);
            return ResponseEntity.internalServerError().body(LegRenders.getDefaultInstance());
        }
    }
}
