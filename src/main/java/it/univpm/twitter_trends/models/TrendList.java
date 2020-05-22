package it.univpm.twitter_trends.models;

import it.univpm.twitter_trends.models.Trend;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrendList {
    public List<Trend> trends = new ArrayList<>();
    public TrendList(List<Trend> trends){
        this.trends = trends;
    }
    public TrendList(Trend[] trends){
        this.trends = Arrays.asList(trends);
    }
}
