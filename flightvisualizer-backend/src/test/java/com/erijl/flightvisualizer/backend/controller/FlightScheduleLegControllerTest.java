package com.erijl.flightvisualizer.backend.controller;

import com.erijl.flightvisualizer.backend.service.FlightScheduleLegService;
import com.erijl.flightvisualizer.protos.enums.RouteFilterType;
import com.erijl.flightvisualizer.protos.filter.RouteFilter;
import com.erijl.flightvisualizer.protos.objects.LegRender;
import com.erijl.flightvisualizer.protos.objects.LegRenders;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class FlightScheduleLegControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlightScheduleLegController controller;

    @MockBean
    private FlightScheduleLegService flightScheduleLegService;

    @Test
    public void testDistinctFlightScheduleLegsProtobuf() throws Exception {
        // Create a sample RouteFilter
        RouteFilter routeFilter = RouteFilter.newBuilder()
                .setRouteFilterType(RouteFilterType.DISTANCE)
                .setStart(10)
                .setEnd(1200)
                .build();

        // Mock the service behavior (return some sample data)
        List<LegRender> legRenders = new ArrayList<>(); // Populate with sample LegRender objects
        LegRenders expectedResponse = LegRenders.newBuilder().addAllLegs(legRenders).build();
        Mockito.when(flightScheduleLegService.getDistinctFlightScheduleLegsForRendering(routeFilter))
                .thenReturn(legRenders);

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/flightScheduleLeg/distinct")
                        .content(routeFilter.toByteArray()) // Send Protobuf data
                        .contentType("application/x-protobuf")
                        .accept("application/x-protobuf"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/x-protobuf"))
                .andExpect(result -> {
                    LegRenders response = LegRenders.parseFrom(result.getResponse().getContentAsByteArray());
                    // Assert that the response matches the expected data
                    assertEquals(expectedResponse, response);
                });
    }
}