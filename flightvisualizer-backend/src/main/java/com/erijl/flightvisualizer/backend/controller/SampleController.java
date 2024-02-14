package com.erijl.flightvisualizer.backend.controller;

import com.erijl.flightvisualizer.backend.manager.AuthManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    AuthManager authManager;

    public SampleController(AuthManager authManager) {
        this.authManager = authManager;
    }

    @RequestMapping("/sample")
    public String sample() {
        String temp = this.authManager.getBearerAccessToken();
        System.out.println(temp);

        return "sample response";
    }
}
