package it.univpm.twitter_trends.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class Trend {
    @Meaning("Country of origin")
    public String country;

    @Meaning("Country identifier")
    public String countryCode;

    @Meaning("Name of location")
    public String name;

    @Meaning("")
    public int parentid;

    @Meaning("Location type (.code - .name)")
    public PlaceType placeType;

    @Meaning("Location's URL on Yahoo Where API")
    public String url;

    @Meaning("Where On Earth IDentifier")
    public int woeid;

    public class PlaceType {
        @Meaning("Location type code")
        public int code;
        @Meaning("Location type name")
        public String name;
    }

}


