package it.univpm.twitter_trends.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/*
            "country": "Sweden",
            "countryCode": "SE",
            "name": "Sweden",
            "parentid": 1,
            "placeType": {
            "code": 12,
            "name": "Country"
            },
            "url": "http://where.yahooapis.com/v1/place/23424954",
            "woeid": 23424954

 */
@JsonIgnoreProperties
public class Trend {
    public String country;
    public String countryCode;
    public String name;
    public int parentid;
    public PlaceType placeType;
    public String url;
    public int woeid;

    public class PlaceType {
        public int code;
        public String name;
    }

}


