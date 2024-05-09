package com.erijl.flightvisualizer.backend.service;

import com.erijl.flightvisualizer.backend.model.dto.AircraftResponse;
import com.erijl.flightvisualizer.backend.manager.AuthManager;
import com.erijl.flightvisualizer.backend.model.entities.Aircraft;
import com.erijl.flightvisualizer.backend.model.repository.AircraftRepository;
import com.erijl.flightvisualizer.backend.util.RestUtil;
import com.erijl.flightvisualizer.backend.util.UrlBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

@Service
public class AircraftService {

    @Value("${flight.visualizer.api.url}")
    private String baseUrl;

    private final Gson gson = new GsonBuilder().create();

    private final AircraftRepository aircraftRepository;

    private final RestUtil restUtil;

    private final AuthManager authManager;

    public AircraftService(AircraftRepository aircraftRepository, RestUtil restUtil, AuthManager authManager) {
        this.aircraftRepository = aircraftRepository;
        this.restUtil = restUtil;
        this.authManager = authManager;
    }

    @Cacheable(value = "aircraft", key = "#iataAircraftCode", unless="#result == null")
    public void ensureAircraftExists(String iataAircraftCode) {
        Optional<Aircraft> aircraft = this.aircraftRepository.findById(iataAircraftCode);

        if(aircraft.isEmpty()) {
            this.requestAndInsertAircraft(iataAircraftCode);
        }
    }

    @Cacheable(value = "aircraft", key = "#iataAircraftCode", unless="#result == null")
    public Aircraft getAircraftById(String iataAircraftCode) {
        return this.aircraftRepository.findById(iataAircraftCode).orElse(null);
    }

    private void requestAndInsertAircraft(String iataAircraftCode) {
        String requestUrl = new UrlBuilder(this.baseUrl)
                .aircraft()
                .filterForAircraft(iataAircraftCode)
                .getUrl();

        try {
            ResponseEntity<String> response = this.restUtil.exchangeRequest(
                    requestUrl, HttpMethod.GET, this.restUtil.getStandardHeaders(this.authManager.getBearerAccessToken()));

            AircraftResponse aircraftResponse = this.gson.fromJson(response.getBody(), AircraftResponse.class);
            AircraftResponse.AircraftSummary tempAircraft = aircraftResponse.getAircraftResource().getAircraftSummaries().getAircraftSummary();

            this.aircraftRepository.save(
                    new Aircraft(
                            tempAircraft.getAircraftCode(),
                            tempAircraft.getNames().getName().getValue()
                    ));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                this.aircraftRepository.save(
                        new Aircraft(
                                iataAircraftCode
                        )
                );
            } else {
                //TODO add proper error handling
                System.out.println("Request Failed");
                System.out.println(e.getStatusCode());
            }
        }
    }
}
