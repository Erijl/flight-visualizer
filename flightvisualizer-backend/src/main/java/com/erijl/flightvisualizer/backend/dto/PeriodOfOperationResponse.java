package com.erijl.flightvisualizer.backend.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PeriodOfOperationResponse {

    @SerializedName("startDate")
    private String startDate;

    @SerializedName("endDate")
    private String endDate;

    @SerializedName("daysOfOperation")
    private String daysOfOperation;
}
