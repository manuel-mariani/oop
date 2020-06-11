package it.univpm.twitter_trends.filter;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionFilter<T> {
    Collection<T> collection;
    Operator filter;

    public CollectionFilter(String expression) throws Exception{
        filter = Parser.parse(expression);
    }

    public CollectionFilter(Collection<T> collection, String expression) throws Exception{
        this.collection = collection;
        filter = Parser.parse(expression);
    }

    public void setCollection(Collection<T> collection) {
        this.collection = collection;
    }

    public Collection<T> getFiltered() throws Exception {
        ArrayList<T> result = new ArrayList<>();
        for (T item : collection){
            if(filter.isTrue(item))
                result.add(item);
        }
        return result;
    }

}
