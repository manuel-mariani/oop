package it.univpm.twitter_trends.models;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

// TODO: Better name (Tree?)
public class StringMatrix<T> {
    public Element root;

    public class Element{
        public String value = "null";
        Element nextSibling = null;
        Element nextChild = null;

        Element(){}
        Element(String value){
            this.value = value;
        }

        void addSibling(Element s){
            Element e = this;
            for (;e.nextSibling != null; e = e.nextSibling);
            e.nextSibling = s;
        }

        void addChild(Element c){
            Element e = this;
            for (;e.nextChild != null; e = e.nextChild);
            e.nextChild = c;
        }

        public List<Element> getSiblings(){
            List<Element> siblings = new LinkedList<>();
            for (Element e = this; e != null; e = e.nextSibling)
                siblings.add(e);
            return siblings;
        }
        public List<Element> getChildren(){
            List<Element> children = new LinkedList<>();
            for(Element e = this.nextChild; e.nextChild != null; e = e.nextChild)
                children.add(e);
            return children;
        }
    }

    public StringMatrix(T obj){
        try {
            this.root = generate(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Element generate(Object obj) throws Exception {
        Field[] fields = obj.getClass().getFields();
        Element start = null;

        for(Field f : fields){
            if (f.getClass().getName().startsWith("it.univpm.twitter_trends")
                || Collection.class.isAssignableFrom(f.getClass())){
                if (start == null)
                    start = new Element();
                start.addChild(generate(f.get(obj)));
            }
            else{
                if (start == null)
                    start = new Element(f.get(obj).toString());
                else
                    start.addSibling(new Element(f.get(obj).toString()));
            }
        }
        return start;
    }

}
