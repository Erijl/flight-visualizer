package com.erijl.flightvisualizer.backend.model.api;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AirlineResponse {

    @SerializedName("AirlineResource")
    private AirlineResource airlineResource;

    @Getter
    @Setter
    public static class AirlineResource {
        @SerializedName("Airlines")
        private Airlines airlines;
    }

    @Getter
    @Setter
    public static class Airlines {
        @SerializedName("Airline")
        private Airline airline;
    }

    @Getter
    @Setter
    public static class Airline {
        @SerializedName("AirlineID")
        private String airlineID;
        @SerializedName("AirlineID_ICAO")
        private String airlineID_ICAO;
        @SerializedName("Names")
        private Names names;
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