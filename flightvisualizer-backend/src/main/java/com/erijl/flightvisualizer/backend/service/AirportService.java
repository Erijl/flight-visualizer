package com.erijl.flightvisualizer.backend.service;

import com.erijl.flightvisualizer.backend.builder.AirportDetailsBuilder;
import com.erijl.flightvisualizer.backend.builder.AirportRenderBuilder;
import com.erijl.flightvisualizer.backend.model.api.AirportResponse;
import com.erijl.flightvisualizer.backend.manager.AuthManager;
import com.erijl.flightvisualizer.backend.model.entities.Airline;
import com.erijl.flightvisualizer.backend.model.entities.Airport;
import com.erijl.flightvisualizer.backend.model.projections.AirportRenderDataProjection;
import com.erijl.flightvisualizer.backend.model.repository.AirportRepository;
import com.erijl.flightvisualizer.backend.util.RestUtil;
import com.erijl.flightvisualizer.backend.util.UrlBuilder;
import com.erijl.flightvisualizer.backend.validators.AirportRenderValidator;
import com.erijl.flightvisualizer.protos.enums.AirportDisplayType;
import com.erijl.flightvisualizer.protos.filter.GeneralFilter;
import com.erijl.flightvisualizer.protos.objects.AirportDetails;
import com.erijl.flightvisualizer.protos.objects.AirportRender;
import com.erijl.flightvisualizer.protos.objects.LegRender;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    public List<AirportRender> getAllAirportsWithFilter(GeneralFilter generalFilter, List<LegRender> legRenders) {

        if(generalFilter.getAirportDisplayType() == AirportDisplayType.AIRPORTDISPLAYTYPE_NONE) {
            return new ArrayList<>();
        }

        List<AirportRenderDataProjection> airportProjections = this.airportRepository.findAllAirportRenders();

        if(generalFilter.getAirportDisplayType() == AirportDisplayType.AIRPORTDISPLAYTYPE_WITHROUTES) {
            HashSet<String> airportCodes = new HashSet<>();
            for (LegRender legRender : legRenders) {
                airportCodes.add(legRender.getOriginAirportIataCode());
                airportCodes.add(legRender.getDestinationAirportIataCode());
            }
            airportProjections = airportProjections.stream()
                    .filter(airportProjection -> airportCodes.contains(airportProjection.getIataCode()))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return AirportRenderBuilder.buildAirportRenderList(airportProjections);
    }

    public AirportDetails getAirportDetails(AirportRender airportRender) {
        AirportRenderValidator.validate(airportRender);

        Airport airport = this.getAirportById(airportRender.getIataCode());
        return AirportDetailsBuilder.buildAirportDetails(airport);
    }

    public Map<String, Airport> getAirportsById(Set<String> airportCodes) {
        Iterable<Airport> airports = airportRepository.findAllById(airportCodes);
        Map<String, Airport> airportMap = new HashMap<>();
        airports.forEach(airport -> airportMap.put(airport.getIataAirportCode(), airport));
        return airportMap;
    }


    public void ensureAirportsExist(Set<String> airportCodes) {
        Iterable<Airport> existingAirports = airportRepository.findAllById(airportCodes);

        Set<String> existingCodes = StreamSupport.stream(existingAirports.spliterator(), false)
                .map(Airport::getIataAirportCode)
                .collect(Collectors.toSet());

        Set<String> missingCodes = new HashSet<>(airportCodes);
        missingCodes.removeAll(existingCodes);

        for (String missingCode : missingCodes) {
            this.requestAndInsertAirport(missingCode);
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
