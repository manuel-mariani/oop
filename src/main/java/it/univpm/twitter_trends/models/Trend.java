package it.univpm.twitter_trends.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class Trend {
    @Meaning("Paese di provenienza")
    public String country;

    @Meaning("Codice identificativo paese")
    public String countryCode;

    @Meaning("Nome luogo")
    public String name;

    @Meaning("")
    public int parentid;

    @Meaning("Tipologia di località (code - name)")
    public PlaceType placeType;

    @Meaning("URL del luogo sulle API Yahoo Where")
    public String url;

    @Meaning("Where On Earth IDentifier, id della posizione")
    public int woeid;

    public class PlaceType {
        @Meaning("Codice tipologia luogo")
        public int code;
        @Meaning("Nome tipologia")
        public String name;
    }

}


