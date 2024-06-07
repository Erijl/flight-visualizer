package com.erijl.flightvisualizer.backend.service;

import com.erijl.flightvisualizer.backend.model.api.AircraftResponse;
import com.erijl.flightvisualizer.backend.manager.AuthManager;
import com.erijl.flightvisualizer.backend.model.entities.Aircraft;
import com.erijl.flightvisualizer.backend.model.repository.AircraftRepository;
import com.erijl.flightvisualizer.backend.util.RestUtil;
import com.erijl.flightvisualizer.backend.builder.UrlBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
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

    /**
     * Get all requested {@link Aircraft} objects by their IATA codes
     *
     * @param aircraftCodes {@link Set} of IATA code {@link String}s to fetch
     * @return {@link Map} of {@link Aircraft} objects with their IATA code as key
     */
    public Map<String, Aircraft> getAircraftsById(Set<String> aircraftCodes) {
        Iterable<Aircraft> aircrafts = aircraftRepository.findAllById(aircraftCodes);
        Map<String, Aircraft> aircraftMap = new HashMap<>();
        aircrafts.forEach(aircraft -> aircraftMap.put(aircraft.getIataAircraftCode(), aircraft));
        return aircraftMap;
    }

    /**
     * Ensure that all requested {@link Aircraft} objects exist in the database by first checking the database and
     * else fetching and inserting them if necessary
     *
     * @param aircraftCodes {@link Set} of IATA code {@link String}s to check
     */
    public void ensureAircraftsExist(Set<String> aircraftCodes) {
        Iterable<Aircraft> existingAircrafts = aircraftRepository.findAllById(aircraftCodes);

        Set<String> existingCodes = StreamSupport.stream(existingAircrafts.spliterator(), false)
                .map(Aircraft::getIataAircraftCode)
                .collect(Collectors.toSet());

        Set<String> missingCodes = new HashSet<>(aircraftCodes);
        missingCodes.removeAll(existingCodes);

        for (String missingCode : missingCodes) {
            this.requestAndInsertAircraft(missingCode);
        }
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
                log.error("Failed to fetch aircraft with IATA code: " + iataAircraftCode + " ERROR: " + e.getStatusCode());
            }
        }
    }
}
