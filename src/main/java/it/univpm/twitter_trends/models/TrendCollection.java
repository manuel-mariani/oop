package it.univpm.twitter_trends.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.Collection;

@JsonIgnoreProperties
public class TrendCollection {

    public String dateString;
    public int year;
    public int month;
    public int day;

    public Collection<Trend> trends;

    public TrendCollection(Collection<Trend> trends, String dateString){
        this.dateString = dateString;
        this.trends = trends;

        year  = Integer.parseInt(dateString.split("-")[0]);
        month = Integer.parseInt(dateString.split("-")[1]);
        day   = Integer.parseInt(dateString.split("-")[2]);
    }

    public TrendCollection(Trend[] trends){
        this.trends = Arrays.asList(trends);
    }

    public TrendCollection(){
        super();
    }
}
