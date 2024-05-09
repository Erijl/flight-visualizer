package com.erijl.flightvisualizer.backend.model.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
