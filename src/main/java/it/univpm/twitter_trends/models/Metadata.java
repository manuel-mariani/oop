package it.univpm.twitter_trends.models;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Annotation that allows a class' property to have a brief string description.
 * The meaning is used in the generation of the metadata for the relative class
 * @see Metadata#get(Class)
 */
@Retention(RetentionPolicy.RUNTIME)
@interface Meaning {
    String value() default "";
}

/** Class that contains the static methods to generate the metadata for a class */
public class Metadata {
    /**
     * Generates the metadata for a class. The returned HashMap is structured as follows:
     *   "className" : name of the class
     *   "fields" : list of the fields contained in the class. The structure of a field item in the list is:
     *     "field": name of the field
     *     "meaning": the Meaning of the field
     *     "type" : the Java type of the field. If the field is a sub class declared in the class, its metadata, using
     *     this same structure ("classname", "fields") will be added in the key "type"
     *
     * @param className the class' name from which to generate the metadata
     * @return          a HashMap with the keys "className":String and"fields":List<HashMap<String, Object>
     */
    public static HashMap<String, Object> get(Class className) {
        // Get fields and nested classes in the class
        Field[] fields = className.getFields();
        Class[] classes = className.getClasses();
        LinkedList<HashMap> meta = new LinkedList<>();

        for (Field f : fields){
            // Check if field is also nested
            boolean isNestedClass = false;
            for (Class c : classes){
                if (c.getTypeName().equals(f.getType().getName()))
                    isNestedClass = true;
            }

            // Init the details of the field, starting with its name
            HashMap<String, Object> entry = new HashMap<>();
            entry.put("field", f.getName());

            // Get the field annotation
            Meaning ann = f.getAnnotation(Meaning.class);
            if (ann != null)
                entry.put("meaning", ann.value());

            // If field is a nested class, add the class details to the entry,
            // Else just add the type
            if (isNestedClass)
                entry.put("type", Metadata.get(f.getType()));
            else
                entry.put("type", f.getType());

            meta.add(entry);
        }

        // Map of the final item "classname" : {details list}
        HashMap<String, Object> item = new HashMap<>();
        item.put("class:", className.getName());
        item.put("fields:", meta);

        return item;
    }

    /**
     * Returns the Metadata of a class, without the fields' type. Note that this method only returns the metadata of the
     * passed class, without the nested classes metadata. The structure of a returned HashMap in the list
     * List<Hashmap<String,String>> is:
     *   "field":   name of the field
     *   "meaning": meaning of the field
     * This is used in the home page to generate the table's header
     * @see Metadata#get(Class)
     * @param className the class' name from which to generate the metadata
     * @return          the metadata of class (no nested subclasses) without the fields' type
     */
    public static List<HashMap<String, String>> getMetadataNoType(Class className){
        List<HashMap<String, String>> meta = new LinkedList<>();
        for (Field f : className.getFields()){
            // Create map of the field's metadata and put the field name in it
            HashMap<String, String> map = new HashMap<>();
            map.put("field", f.getName());
            // Get meaning if present
            Meaning ann = f.getAnnotation(Meaning.class);
            if (ann != null)
                map.put("meaning", ann.value());
            // Add the current field metadata to the final list
            meta.add(map);
        }
        return meta;
    }
}
