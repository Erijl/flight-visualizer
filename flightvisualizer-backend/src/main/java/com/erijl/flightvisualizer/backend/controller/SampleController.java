package com.erijl.flightvisualizer.backend.controller;

import com.erijl.flightvisualizer.backend.manager.AuthManager;
import com.erijl.flightvisualizer.backend.model.Airline;
import com.erijl.flightvisualizer.backend.repository.AirlineRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class SampleController {

    AuthManager authManager;

    AirlineRepository airlineRepository;

    public SampleController(AuthManager authManager, AirlineRepository airlineRepository) {
        this.authManager = authManager;
        this.airlineRepository = airlineRepository;
    }

    @RequestMapping("/sample")
    public String sample() {
        String temp = this.authManager.getBearerAccessToken();
        System.out.println(temp);

        return "sample response";
    }

    @RequestMapping("/airline")
    public Optional<Airline> airline() {
        return this.airlineRepository.findById("AASDSD");
    }
}
