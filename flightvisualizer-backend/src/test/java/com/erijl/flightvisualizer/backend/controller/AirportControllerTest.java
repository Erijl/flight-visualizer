package com.erijl.flightvisualizer.backend.controller;

import com.erijl.flightvisualizer.backend.service.AirportService;
import com.erijl.flightvisualizer.protos.objects.AirportDetails;
import com.erijl.flightvisualizer.protos.objects.AirportRender;
import com.erijl.flightvisualizer.protos.objects.Coordinate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AirportControllerTest {

    @Mock
    private AirportService airportService;

    @InjectMocks
    private AirportController airportController;

    @Test
    public void testGetAirportDetail_success() {
        AirportRender airportRender = AirportRender.newBuilder()
                .setIataCode("JFK")
                .setCoordinate(Coordinate.newBuilder().setLatitude(40.6413).setLongitude(-73.7781).build())
                .build();
        AirportDetails airportDetails = AirportDetails.newBuilder()
                .setIataCode("JFK")
                .setName("John F. Kennedy International Airport")
                .setCoordinate(Coordinate.newBuilder().setLatitude(40.6413).setLongitude(-73.7781).build())
                .setIataCityCode("NYC")
                .setIsoCountryCode("US")
                .setOffsetUtc("-5:00")
                .setTimezoneId("America/New_York")
                .build();

        when(airportService.getAirportDetails(airportRender)).thenReturn(airportDetails);

        ResponseEntity<AirportDetails> response = airportController.getAirportDetail(airportRender);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(airportDetails, response.getBody());
    }

    @Test
    public void testGetAirportDetail_illegalArgumentException() {
        AirportRender airportRender = AirportRender.newBuilder().setIataCode("Invalid").build();

        when(airportService.getAirportDetails(airportRender)).thenThrow(new IllegalArgumentException("Invalid airport code"));

        ResponseEntity<AirportDetails> response = airportController.getAirportDetail(airportRender);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(AirportDetails.getDefaultInstance(), response.getBody());
    }

    @Test
    public void testGetAirportDetail_generalException() {
        AirportRender airportRender = AirportRender.newBuilder().setIataCode("JFK").build();

        when(airportService.getAirportDetails(airportRender)).thenThrow(new RuntimeException("Some error"));

        ResponseEntity<AirportDetails> response = airportController.getAirportDetail(airportRender);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(AirportDetails.getDefaultInstance(), response.getBody());
    }
}
