package it.univpm.twitter_trends.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class TrendCollection {

    public LocalDateTime dateTime = LocalDateTime.now();
//    public LocalDate date = dateTime.toLocalDate();
//    public LocalTime time = dateTime.toLocalTime();

    public Collection<Trend> trends = new ArrayList<>();

    public TrendCollection(Collection<Trend> trends){
        this.trends = trends;
    }

    public TrendCollection(Trend[] trends){
        this.trends = Arrays.asList(trends);
    }


//
//    /*
//
//    ---------  COLLECTION METHODS  ---------
//
//    */
//
//    @Override
//    public int size() {
//        return trends.size();
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return trends.isEmpty();
//    }
//
//    @Override
//    public boolean contains(Object o) {
//        return trends.contains(o);
//    }
//
//    @Override
//    public Iterator iterator() {
//        return trends.iterator();
//    }
//
//    @Override
//    public Object[] toArray() {
//        return trends.toArray();
//    }
//
//    @Override
//    public boolean add(Object o) {
//        return trends.add((Trend) o);
//    }
//
//    @Override
//    public boolean remove(Object o) {
//        return trends.remove(o);
//    }
//
//    @Override
//    public boolean addAll(Collection c) {
//        return trends.addAll(c);
//    }
//
//    @Override
//    public void clear() {
//        trends.clear();
//    }
//
//    @Override
//    public boolean retainAll(Collection c) {
//        return trends.retainAll(c);
//    }
//
//    @Override
//    public boolean removeAll(Collection c) {
//        return trends.removeAll(c);
//    }
//
//    @Override
//    public boolean containsAll(Collection c) {
//        return trends.containsAll(c);
//    }
//
//    @Override
//    public Object[] toArray(Object[] a) {
//        return trends.toArray(a);
//    }
}
