package it.univpm.twitter_trends.filter;

import java.util.ArrayList;
import java.util.Collection;

/** Class that filter a collection of Type by a filter expression
 * @param <T> the Type of the items in the collection
 */
public class CollectionFilter<T> {
    Collection<T> collection;
    Operator filter;

    /**
     * Constructor that builds the collection filter for the specified collection and the filter from an expression,
     * @throws Exception  if the expression has errors, an exception with the details will be thrown
     */
    public CollectionFilter(Collection<T> collection, String expression) throws Exception{
        this.collection = collection;
        filter = Parser.parse(expression);
    }

    public void setCollection(Collection<T> collection) {
        this.collection = collection;
    }

    /** Returns the filtered collection */
    public Collection<T> getFiltered() throws Exception {
        ArrayList<T> result = new ArrayList<>();
        // Check if each item matches the expression and if so add them to the output collection
        for (T item : collection){
            if(filter.isTrue(item))
                result.add(item);
        }
        return result;
    }

}
