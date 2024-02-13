package com.erijl.flightvisualizer.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @RequestMapping("/sample")
    public String sample() {
        System.out.println("SampleController.sample");

        return "sample response";
    }
}
