package com.erijl.flightvisualizer.backend.controller;

import com.erijl.flightvisualizer.backend.model.dtos.FlightScheduleLegDto;
import com.erijl.flightvisualizer.backend.model.dtos.FlightScheduleLegWithDistance;
import com.erijl.flightvisualizer.backend.service.FlightScheduleLegService;
import com.erijl.flightvisualizer.protos.filter.RouteFilter;
import com.erijl.flightvisualizer.protos.objects.LegRender;
import com.erijl.flightvisualizer.protos.objects.LegRenders;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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

    @PostMapping(value = "/flightScheduleLeg/distinct", produces = "application/x-protobuf", consumes = "application/x-protobuf")
    public LegRenders getDistinctFlightScheduleLegsForRendering(@RequestBody RouteFilter routeFilter) {
        System.out.println("Raw request body: " + Arrays.toString(routeFilter.toByteArray())); // Log raw bytes
        List<LegRender> legRenders = flightScheduleLegService.getDistinctFlightScheduleLegsForRendering(routeFilter);
        return LegRenders.newBuilder().addAllLegs(legRenders).build();
    }
}
