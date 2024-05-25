package com.erijl.flightvisualizer.backend.controller;

import com.erijl.flightvisualizer.backend.service.FlightScheduleLegService;
import com.erijl.flightvisualizer.protos.dtos.SandboxModeResponseObject;
import com.erijl.flightvisualizer.protos.filter.CombinedFilterRequest;
import com.erijl.flightvisualizer.protos.filter.SpecificRouteFilterRequest;
import com.erijl.flightvisualizer.protos.objects.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FlightScheduleLegControllerTest {

    @Mock
    private FlightScheduleLegService flightScheduleLegService;

    @InjectMocks
    private FlightScheduleLegController flightScheduleLegController;

    @Test
    public void testGetDistinctFlightScheduleLegsForRendering_success() {
        CombinedFilterRequest request = CombinedFilterRequest.newBuilder().build();
        SandboxModeResponseObject response = SandboxModeResponseObject.newBuilder().build();

        when(flightScheduleLegService.getDistinctFlightScheduleLegsForRendering(request)).thenReturn(response);

        ResponseEntity<SandboxModeResponseObject> result = flightScheduleLegController.getDistinctFlightScheduleLegsForRendering(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    public void testGetDistinctFlightScheduleLegsForRendering_illegalArgumentException() {
        CombinedFilterRequest request = CombinedFilterRequest.newBuilder().build();

        when(flightScheduleLegService.getDistinctFlightScheduleLegsForRendering(request))
                .thenThrow(new IllegalArgumentException("Invalid filter"));

        ResponseEntity<SandboxModeResponseObject> result = flightScheduleLegController.getDistinctFlightScheduleLegsForRendering(request);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(SandboxModeResponseObject.getDefaultInstance(), result.getBody());
    }

    @Test
    public void testGetDistinctFlightScheduleLegsForRendering_generalException() {
        CombinedFilterRequest request = CombinedFilterRequest.newBuilder().build();

        when(flightScheduleLegService.getDistinctFlightScheduleLegsForRendering(request)).thenThrow(RuntimeException.class);

        ResponseEntity<SandboxModeResponseObject> response = flightScheduleLegController.getDistinctFlightScheduleLegsForRendering(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(SandboxModeResponseObject.getDefaultInstance(), response.getBody());
    }

    @Test
    public void testGetRouteDetail_success() {
        SpecificRouteFilterRequest request = SpecificRouteFilterRequest.newBuilder().build();
        DetailedLegInformations response = DetailedLegInformations.newBuilder().build();

        when(flightScheduleLegService.getLegsForRouteDetailed(request)).thenReturn(response);

        ResponseEntity<DetailedLegInformations> result = flightScheduleLegController.getRouteDetail(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    public void testGetRouteDetail_illegalArgumentException() {
        SpecificRouteFilterRequest request = SpecificRouteFilterRequest.newBuilder().build();

        when(flightScheduleLegService.getLegsForRouteDetailed(request))
                .thenThrow(new IllegalArgumentException("Invalid route filter"));

        ResponseEntity<DetailedLegInformations> result = flightScheduleLegController.getRouteDetail(request);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(DetailedLegInformations.getDefaultInstance(), result.getBody());
    }



    @Test
    public void testGetRouteDetail_generalException() {
        SpecificRouteFilterRequest request = SpecificRouteFilterRequest.newBuilder().build();

        when(flightScheduleLegService.getLegsForRouteDetailed(request)).thenThrow(RuntimeException.class);

        ResponseEntity<DetailedLegInformations> response = flightScheduleLegController.getRouteDetail(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(DetailedLegInformations.getDefaultInstance(), response.getBody());
    }
}