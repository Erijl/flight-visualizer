package com.erijl.flightvisualizer.backend.controller;

import com.erijl.flightvisualizer.backend.service.AirportService;
import com.erijl.flightvisualizer.protos.objects.AirportRender;
import com.erijl.flightvisualizer.protos.objects.AirportRenders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class AirportController {

    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @GetMapping("/airports")
    public ResponseEntity<AirportRenders> airports() {
        try {
            List<AirportRender> airportRenders = this.airportService.getAllAirports();
            AirportRenders response = AirportRenders.newBuilder().addAllAirports(airportRenders).build();
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(AirportRenders.getDefaultInstance());
        } catch (Exception e) {
            log.error("Error processing request", e);
            return ResponseEntity.internalServerError().body(AirportRenders.getDefaultInstance());
        }
    }
}
