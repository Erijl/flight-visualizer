package com.erijl.flightvisualizer.backend.controller;

import com.erijl.flightvisualizer.backend.service.FlightScheduleLegService;
import com.erijl.flightvisualizer.protos.dtos.SandboxModeResponseObject;
import com.erijl.flightvisualizer.protos.filter.CombinedFilterRequest;
import com.erijl.flightvisualizer.protos.objects.*;
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

    //@Test
    //public void testDistinctFlightScheduleLegsProtobuf() throws Exception {
    //    CombinedFilterRequest combinedFilter = CombinedFilterRequest.newBuilder().build();
//
    //    List<LegRender> legRenders = new ArrayList<>();
    //    SandboxModeResponseObject expectedResponse = SandboxModeResponseObject.newBuilder().addAllLegRenders(legRenders).build();
    //    Mockito.when(flightScheduleLegService.getDistinctFlightScheduleLegsForRendering(combinedFilter))
    //            .thenReturn(legRenders);
//
    //    mockMvc.perform(MockMvcRequestBuilders.post("/flightScheduleLeg/distinct")
    //                    .content(combinedFilter.toByteArray()) // Send Protobuf data
    //                    .contentType("application/x-protobuf")
    //                    .accept("application/x-protobuf"))
    //            .andExpect(MockMvcResultMatchers.status().isOk())
    //            .andExpect(MockMvcResultMatchers.content().contentType("application/x-protobuf"))
    //            .andExpect(result -> {
    //                LegRenders response = LegRenders.parseFrom(result.getResponse().getContentAsByteArray());
    //                assertEquals(expectedResponse, response);
    //            });
    //}
}