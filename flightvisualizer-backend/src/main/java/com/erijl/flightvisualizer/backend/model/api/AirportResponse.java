package com.erijl.flightvisualizer.backend.model.api;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AirportResponse {

    @SerializedName("AirportResource")
    private AirportResource airportResource;

    @Getter
    @Setter
    public static class AirportResource {
        @SerializedName("Airports")
        private Airports airports;
    }

    @Getter
    @Setter
    public static class Airports {
        @SerializedName("Airport")
        private Airport airport;
    }

    @Getter
    @Setter
    public static class Airport {
        @SerializedName("AirportCode")
        private String airportCode;
        @SerializedName("Position")
        private Position position;
        @SerializedName("CityCode")
        private String cityCode;
        @SerializedName("CountryCode")
        private String countryCode;
        @SerializedName("LocationType")
        private String locationType;
        @SerializedName("Names")
        private Names names;
        @SerializedName("UtcOffset")
        private String utcOffset;
        @SerializedName("TimeZoneId")
        private String timeZoneId;
    }

    @Getter
    @Setter
    public static class Position {
        @SerializedName("Coordinate")
        private Coordinate coordinate;
    }

    @Getter
    @Setter
    public static class Coordinate {
        @SerializedName("Latitude")
        private BigDecimal latitude;
        @SerializedName("Longitude")
        private BigDecimal longitude;
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