package com.erijl.flightvisualizer.backend.model.dto;


import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LegResponse {

    @SerializedName("sequenceNumber")
    private int sequenceNumber;

    @SerializedName("origin")
    private String origin; // Departure airport IATA code

    @SerializedName("destination")
    private String destination; // Arrival airport IATA code

    @SerializedName("serviceType")
    private String serviceType;

    @SerializedName("aircraftOwner")
    private String aircraftOwner;

    @SerializedName("aircraftType")
    private String aircraftType;

    @SerializedName("aircraftConfigurationVersion")
    private String aircraftConfigurationVersion;

    @SerializedName("registration")
    private String registration;

    @SerializedName("op")
    private boolean op; // Signals whether this is an operating or marketing leg

    @SerializedName("aircraftDepartureTimeUTC")
    private int aircraftDepartureTimeUTC;

    @SerializedName("aircraftDepartureTimeDateDiffUTC")
    private int aircraftDepartureTimeDateDiffUTC;

    @SerializedName("aircraftDepartureTimeLT")
    private int aircraftDepartureTimeLT;

    @SerializedName("aircraftDepartureTimeDateDiffLT")
    private int aircraftDepartureTimeDateDiffLT;

    @SerializedName("aircraftDepartureTimeVariation")
    private int aircraftDepartureTimeVariation;

    @SerializedName("aircraftArrivalTimeUTC")
    private int aircraftArrivalTimeUTC;

    @SerializedName("aircraftArrivalTimeDateDiffUTC")
    private int aircraftArrivalTimeDateDiffUTC;

    @SerializedName("aircraftArrivalTimeLT")
    private int aircraftArrivalTimeLT;

    @SerializedName("aircraftArrivalTimeDateDiffLT")
    private int aircraftArrivalTimeDateDiffLT;

    @SerializedName("aircraftArrivalTimeVariation")
    private int aircraftArrivalTimeVariation;
}