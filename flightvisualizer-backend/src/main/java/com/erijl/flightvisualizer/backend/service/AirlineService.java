package com.erijl.flightvisualizer.backend.service;

import com.erijl.flightvisualizer.backend.model.api.AirlineResponse;
import com.erijl.flightvisualizer.backend.manager.AuthManager;
import com.erijl.flightvisualizer.backend.model.entities.Airline;
import com.erijl.flightvisualizer.backend.model.repository.AirlineRepository;
import com.erijl.flightvisualizer.backend.util.RestUtil;
import com.erijl.flightvisualizer.backend.builder.UrlBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    /**
     * Get all requested {@link Airline} objects by their IATA codes
     *
     * @param airlineCodes {@link Set} of IATA code {@link String}s to fetch
     * @return {@link Map} of {@link Airline} objects with their IATA code as key
     */
    public Map<String, Airline> getAirlinesById(Set<String> airlineCodes) {
        Iterable<Airline> airlines = airlineRepository.findAllById(airlineCodes);
        Map<String, Airline> airlineMap = new HashMap<>();
        airlines.forEach(airline -> airlineMap.put(airline.getIataAirlineCode(), airline));
        return airlineMap;
    }

    /**
     * Ensure that all requested {@link Airline} objects exist in the database by first checking the database and
     * else fetching and inserting them if necessary
     *
     * @param airlineCodes {@link Set} of IATA code {@link String}s to check
     */
    public void ensureAirlinesExist(Set<String> airlineCodes) {
        Iterable<Airline> existingAirlines = airlineRepository.findAllById(airlineCodes);

        Set<String> existingCodes = StreamSupport.stream(existingAirlines.spliterator(), false)
                .map(Airline::getIataAirlineCode)
                .collect(Collectors.toSet());

        Set<String> missingCodes = new HashSet<>(airlineCodes);
        missingCodes.removeAll(existingCodes);

        // SZS & SVS had a rebranding, but not in the LH api...
        for (String missingCode : missingCodes.stream().filter(code -> !code.equals("SZS") && !code.equals("SVS")).collect(Collectors.toSet())) {
            this.requestAndInsertAirline(missingCode);
        }
    }

    private void requestAndInsertAirline(String iataAirlineCode) {
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
