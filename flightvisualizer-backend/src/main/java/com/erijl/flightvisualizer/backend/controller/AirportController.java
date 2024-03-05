package com.erijl.flightvisualizer.backend.controller;

import com.erijl.flightvisualizer.backend.model.Airport;
import com.erijl.flightvisualizer.backend.service.AirportService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AirportController {

    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @RequestMapping("/airports")
    public Iterable<Airport> airports() {
        return this.airportService.getAllAirports();
    }
}
