package com.erijl.flightvisualizer.backend.model.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FlightScheduleResponse {

    @SerializedName("airline")
    private String airline;

    @SerializedName("flightNumber")
    private int flightNumber;

    @SerializedName("suffix")
    private String suffix;

    @SerializedName("periodOfOperationUTC")
    private PeriodOfOperationResponse periodOfOperationResponseUTC;

    @SerializedName("periodOfOperationLT")
    private PeriodOfOperationResponse periodOfOperationResponseLT;

    @SerializedName("legs")
    private List<LegResponse> legResponses;

    @SerializedName("dataElements")
    private List<DataElementResponse> dataElementResponses;
}
