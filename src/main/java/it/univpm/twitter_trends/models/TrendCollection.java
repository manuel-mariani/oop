package it.univpm.twitter_trends.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.Collection;

/**
 * Model that represents a collection of Trends. It includes the date of the corresponding Trends.
 */
@JsonIgnoreProperties
public class TrendCollection {

    public String dateString;
    public int year;
    public int month;
    public int day;

    public Collection<Trend> trends;

    public TrendCollection(Collection<Trend> trends, String dateString){
        this.trends = trends;
        setDate(dateString);
    }

    public TrendCollection(Trend[] trends){
        this.trends = Arrays.asList(trends);
    }

    public TrendCollection(){
        super();
    }

    public void setDate(String dateString){
        this.dateString = dateString;

        year  = Integer.parseInt(dateString.split("-")[0]);
        month = Integer.parseInt(dateString.split("-")[1]);
        day   = Integer.parseInt(dateString.split("-")[2]);
    }
}
