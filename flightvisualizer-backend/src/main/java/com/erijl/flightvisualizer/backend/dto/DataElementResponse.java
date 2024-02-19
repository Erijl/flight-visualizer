package com.erijl.flightvisualizer.backend.dto;

import com.google.gson.annotations.SerializedName;

public class DataElementResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("startLegSequenceNumber")
    private int startLegSequenceNumber;

    @SerializedName("endLegSequenceNumber")
    private int endLegSequenceNumber;

    @SerializedName("value")
    private String value;
}
