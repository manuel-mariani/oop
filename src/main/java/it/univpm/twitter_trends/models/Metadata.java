package it.univpm.twitter_trends.models;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@interface Meaning {
    String value() default "";
}

public class Metadata {

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

    public static List<HashMap<String, String>> getMetadataNoType(Class className){
        List<HashMap<String, String>> meta = new LinkedList<>();
        for (Field f : className.getFields()){
            HashMap<String, String> map = new HashMap<>();
            map.put("field", f.getName());
            Meaning ann = f.getAnnotation(Meaning.class);
            if (ann != null)
                map.put("meaning", ann.value());
            meta.add(map);
        }
        return meta;
    }
}
