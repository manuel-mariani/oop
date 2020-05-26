package it.univpm.twitter_trends.models;

import it.univpm.twitter_trends.models.Trend;
import jdk.vm.ci.meta.Local;

import java.lang.reflect.Array;
import java.sql.Timestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrendList {
    public LocalDateTime dateTime = LocalDateTime.now();
//    public LocalDate date = dateTime.toLocalDate();
//    public LocalTime time = dateTime.toLocalTime();

    public List<Trend> trends = new ArrayList<>();

    public TrendList(List<Trend> trends){
        this.trends = trends;
    }
    public TrendList(Trend[] trends){
        this.trends = Arrays.asList(trends);
    }

    public TrendList filter(){
        return null;
    }

}
