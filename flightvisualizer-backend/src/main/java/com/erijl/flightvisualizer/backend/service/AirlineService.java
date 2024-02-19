package com.erijl.flightvisualizer.backend.service;

import com.erijl.flightvisualizer.backend.dto.AirlineResponse;
import com.erijl.flightvisualizer.backend.manager.AuthManager;
import com.erijl.flightvisualizer.backend.model.Airline;
import com.erijl.flightvisualizer.backend.repository.AirlineRepository;
import com.erijl.flightvisualizer.backend.util.RestUtil;
import com.erijl.flightvisualizer.backend.util.UrlBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AirlineService {

    @Value("${flight.visualizer.api.url}")
    private String baseUrl;

    private final Gson gson = new GsonBuilder().create();

    private final AirlineRepository airlineRepository;

    private final RestUtil restUtil;

    private final AuthManager authManager;


    public AirlineService(AirlineRepository airlineRepository, RestUtil restUtil, AuthManager authManager) {
        this.airlineRepository = airlineRepository;
        this.restUtil = restUtil;
        this.authManager = authManager;
    }

    public void ensureAirlineExists(String iataAirlineCode) {
        Optional<Airline> airline = airlineRepository.findById(iataAirlineCode);

        if(airline.isEmpty()) {
            this.requestAndInsertAirline(iataAirlineCode);
        }
    }

    public void requestAndInsertAirline(String iataAirlineCode) {
        String requestUrl = new UrlBuilder(this.baseUrl)
                .airline()
                .filterForAirline(iataAirlineCode)
                .getUrl();

        ResponseEntity<String> response = this.restUtil.exchangeRequest(
                requestUrl, HttpMethod.GET, this.restUtil.getStandardHeaders(this.authManager.getBearerAccessToken()));
        if (response.getStatusCode() == HttpStatus.OK) {
            AirlineResponse airlineResponse = this.gson.fromJson(response.getBody(), AirlineResponse.class);
            AirlineResponse.Airline tempAirline = airlineResponse.getAirlineResource().getAirlines().getAirline();

            this.airlineRepository.save(
                    new Airline(
                        tempAirline.getAirlineID(),
                        tempAirline.getAirlineID_ICAO(),
                        tempAirline.getNames().getName().getValue()
            ));
        } else {
            //TODO add proper error handling
            System.out.println("Request Failed");
            System.out.println(response.getStatusCode());
        }
    }
}
