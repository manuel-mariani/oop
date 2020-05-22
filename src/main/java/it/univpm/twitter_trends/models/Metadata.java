package it.univpm.twitter_trends.models;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Metadata {
    public static Object get(Class className) {
        Field[] fields = className.getFields();
        Class[] classes = className.getClasses();
        HashMap<Object, Object> meta = new HashMap<>();

        for (Field f : fields){
            boolean isNestedClass = false;
            for (Class c : classes){
                if (c.getTypeName().equals(f.getType().getName()))
                    isNestedClass = true;
            }
            if (isNestedClass)
                meta.put(f.getName(), Metadata.get(f.getType()));
            else
                meta.put(f.getName(), f.getType());
        }
        return meta;
    }
}
