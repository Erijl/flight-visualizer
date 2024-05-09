package com.erijl.flightvisualizer.backend.model.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AircraftResponse {

    @SerializedName("AircraftResource")
    private AircraftResource aircraftResource;

    @Getter
    @Setter
    public static class AircraftResource {
        @SerializedName("AircraftSummaries")
        private AircraftSummaries aircraftSummaries;
    }

    @Getter
    @Setter
    public static class AircraftSummaries {
        @SerializedName("AircraftSummary")
        private AircraftSummary aircraftSummary;
    }

    @Getter
    @Setter
    public static class AircraftSummary {
        @SerializedName("AircraftCode")
        private String aircraftCode;
        @SerializedName("Names")
        private Names names;
        @SerializedName("AirlineEquipCode")
        private String airlineEquipCode;
    }

    @Getter
    @Setter
    public static class Names {
        @SerializedName("Name")
        private Name name;
    }

    @Getter
    @Setter
    public static class Name {
        @SerializedName("@LanguageCode")
        private String languageCode;
        @SerializedName("$")
        private String value;
    }
}