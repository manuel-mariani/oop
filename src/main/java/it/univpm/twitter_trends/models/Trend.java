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
    @Metadata.Meaning("Paese di provenienza")
    public String country;

    @Metadata.Meaning("Codice identificativo paese")
    public String countryCode;

    @Metadata.Meaning("Nome luogo")
    public String name;

    @Metadata.Meaning("")
    public int parentid;

    @Metadata.Meaning("Tipologia di località")
    public PlaceType placeType;

    @Metadata.Meaning("URL del luogo sulle API Yahoo Where")
    public String url;

    @Metadata.Meaning("Where On Earth IDentifier, id della posizione")
    public int woeid;

    public class PlaceType {
        @Metadata.Meaning("Codice tipologia luogo")
        public int code;
        @Metadata.Meaning("Nome tipologia")
        public String name;
    }

}


