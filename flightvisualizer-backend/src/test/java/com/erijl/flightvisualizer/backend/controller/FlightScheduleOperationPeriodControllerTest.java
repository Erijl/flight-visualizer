package com.erijl.flightvisualizer.backend.controller;

import com.erijl.flightvisualizer.backend.service.FlightScheduleOperationPeriodService;
import com.erijl.flightvisualizer.protos.objects.FlightDateFrequencies;
import com.erijl.flightvisualizer.protos.objects.FlightDateFrequency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlightScheduleOperationPeriodControllerTest {

    @Mock
    private FlightScheduleOperationPeriodService flightScheduleOperationPeriodService;

    @InjectMocks
    private FlightScheduleOperationPeriodController flightScheduleOperationPeriodController;

    @Test
    public void testGetFlightDateFrequency_success() {
        List<FlightDateFrequency> mockFrequencies = new ArrayList<>();
        mockFrequencies.add(FlightDateFrequency.newBuilder().setFrequency(5).build());
        mockFrequencies.add(FlightDateFrequency.newBuilder().setFrequency(8).build());
        FlightDateFrequencies expectedResponse = FlightDateFrequencies.newBuilder()
                .addAllFrequencies(mockFrequencies).build();

        when(flightScheduleOperationPeriodService.getFlightDateFrequency()).thenReturn(mockFrequencies);

        ResponseEntity<FlightDateFrequencies> response =
                flightScheduleOperationPeriodController.getDistinctFlightScheduleLegsForRendering();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    public void testGetFlightDateFrequency_emptyResult() {
        when(flightScheduleOperationPeriodService.getFlightDateFrequency()).thenReturn(new ArrayList<>());

        ResponseEntity<FlightDateFrequencies> response =
                flightScheduleOperationPeriodController.getDistinctFlightScheduleLegsForRendering();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FlightDateFrequencies.getDefaultInstance(), response.getBody());
    }

    @Test
    public void testGetFlightDateFrequency_illegalArgumentException() {
        when(flightScheduleOperationPeriodService.getFlightDateFrequency()).thenThrow(IllegalArgumentException.class);

        ResponseEntity<FlightDateFrequencies> response =
                flightScheduleOperationPeriodController.getDistinctFlightScheduleLegsForRendering();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(FlightDateFrequencies.getDefaultInstance(), response.getBody());
    }

    @Test
    public void testGetFlightDateFrequency_generalException() {
        when(flightScheduleOperationPeriodService.getFlightDateFrequency()).thenThrow(RuntimeException.class);

        ResponseEntity<FlightDateFrequencies> response =
                flightScheduleOperationPeriodController.getDistinctFlightScheduleLegsForRendering();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FlightDateFrequencies.getDefaultInstance(), response.getBody());
    }
}
