package it.univpm.twitter_trends.models;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Metadata {

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Meaning {
        String value() default "";
    }

    public static Object get(Class className) {
        Field[] fields = className.getFields();
        Class[] classes = className.getClasses();
        LinkedList<Object> meta = new LinkedList<>();

        for (Field f : fields){

            boolean isNestedClass = false;
            for (Class c : classes){
                if (c.getTypeName().equals(f.getType().getName()))
                    isNestedClass = true;
            }

            HashMap<Object, Object> entry = new HashMap<>();
            entry.put("field", f.getName());

            Meaning ann = f.getAnnotation(Meaning.class);
            if (ann != null)
                entry.put("meaning", ann.value());

            if (isNestedClass)
                entry.put("type", Metadata.get(f.getType()));
            else
                entry.put("type", f.getType());

            meta.add(entry);
        }

        HashMap<Object, Object> item = new HashMap<>();
        item.put("fields:", meta);
        item.put("class:", className.getName());
        
        return item;
    }
}
