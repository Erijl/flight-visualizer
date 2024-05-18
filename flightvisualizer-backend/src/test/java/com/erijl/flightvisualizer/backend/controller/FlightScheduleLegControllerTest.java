package com.erijl.flightvisualizer.backend.controller;

import com.erijl.flightvisualizer.backend.service.FlightScheduleLegService;
import com.erijl.flightvisualizer.protos.objects.*;
import com.google.protobuf.Timestamp;
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
        TimeFilter timeFilter = TimeFilter.newBuilder()
                .setTimeRange(TimeRange.newBuilder()
                        .setStart(0)
                        .setEnd(1000))
                .setDateRange(DateRange.newBuilder()
                        .setStart(Timestamp.newBuilder().setSeconds(0).setNanos(0))
                        .setEnd(Timestamp.newBuilder().setSeconds(0).setNanos(0)))
                .build();

        List<LegRender> legRenders = new ArrayList<>();
        LegRenders expectedResponse = LegRenders.newBuilder().addAllLegs(legRenders).build();
        Mockito.when(flightScheduleLegService.getDistinctFlightScheduleLegsForRendering(timeFilter))
                .thenReturn(legRenders);

        mockMvc.perform(MockMvcRequestBuilders.post("/flightScheduleLeg/distinct")
                        .content(timeFilter.toByteArray()) // Send Protobuf data
                        .contentType("application/x-protobuf")
                        .accept("application/x-protobuf"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/x-protobuf"))
                .andExpect(result -> {
                    LegRenders response = LegRenders.parseFrom(result.getResponse().getContentAsByteArray());
                    assertEquals(expectedResponse, response);
                });
    }
}