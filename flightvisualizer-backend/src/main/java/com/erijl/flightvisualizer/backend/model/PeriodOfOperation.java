package com.erijl.flightvisualizer.backend.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PeriodOfOperation {

    @SerializedName("startDate")
    private Date startDate;

    @SerializedName("endDate")
    private Date endDate;

    @SerializedName("daysOfOperation")
    private String daysOfOperation;
}
