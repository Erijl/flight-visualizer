package com.erijl.flightvisualizer.backend.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FlightAggregate {

    @SerializedName("airline")
    private String airline;

    @SerializedName("flightNumber")
    private int flightNumber;

    @SerializedName("suffix")
    private String suffix;

    @SerializedName("periodOfOperationUTC")
    private PeriodOfOperation periodOfOperationUTC;

    @SerializedName("periodOfOperationLT")
    private PeriodOfOperation periodOfOperationLT;

    @SerializedName("legs")
    private List<Leg> legs;

    @SerializedName("dataElements")
    private List<DataElement> dataElements;
}
