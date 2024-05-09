package com.erijl.flightvisualizer.backend.service;

import com.erijl.flightvisualizer.backend.model.dto.AirportResponse;
import com.erijl.flightvisualizer.backend.manager.AuthManager;
import com.erijl.flightvisualizer.backend.model.entities.Airport;
import com.erijl.flightvisualizer.backend.model.repository.AirportRepository;
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
public class AirportService {

    @Value("${flight.visualizer.api.url}")
    private String baseUrl;

    private final Gson gson = new GsonBuilder().create();

    private final AirportRepository airportRepository;

    private final RestUtil restUtil;

    private final AuthManager authManager;

    public AirportService(AirportRepository airportRepository, RestUtil restUtil, AuthManager authManager) {
        this.airportRepository = airportRepository;
        this.restUtil = restUtil;
        this.authManager = authManager;
    }

    public Iterable<Airport> getAllAirports() {
        return this.airportRepository.findAll();
    }

    @Cacheable(value = "airport", key = "#iataAirportCode", unless = "#result == null")
    public void ensureAirportExists(String iataAirportCode) {
        Optional<Airport> airport = this.airportRepository.findById(iataAirportCode);

        if (airport.isEmpty()) {
            this.requestAndInsertAirport(iataAirportCode);
        }
    }

    @Cacheable(value = "airport", key = "#iataAirportCode", unless = "#result == null")
    public Airport getAirportById(String iataAirportCode) {
        return this.airportRepository.findById(iataAirportCode).orElse(null);
    }

    private void requestAndInsertAirport(String iataAirportCode) {
        String requestUrl = new UrlBuilder(this.baseUrl)
                .airport()
                .filterForAirport(iataAirportCode)
                .getUrl();

        try {
            ResponseEntity<String> response = this.restUtil.exchangeRequest(
                    requestUrl, HttpMethod.GET, this.restUtil.getStandardHeaders(this.authManager.getBearerAccessToken()));

            AirportResponse airportResponse = this.gson.fromJson(response.getBody(), AirportResponse.class);
            AirportResponse.Airport tempAirport = airportResponse.getAirportResource().getAirports().getAirport();
            AirportResponse.Coordinate tempCoordinate = tempAirport.getPosition().getCoordinate();

            this.airportRepository.save(
                    new Airport(
                            tempAirport.getAirportCode(),
                            tempAirport.getNames().getName().getValue(),
                            tempCoordinate.getLongitude(),
                            tempCoordinate.getLatitude(),
                            tempAirport.getCityCode(),
                            tempAirport.getCountryCode(),
                            tempAirport.getLocationType(),
                            tempAirport.getUtcOffset(),
                            tempAirport.getTimeZoneId()
                    ));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                this.airportRepository.save(
                        new Airport(
                                iataAirportCode
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
