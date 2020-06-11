package it.univpm.twitter_trends;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class Filter_LEGACY<T> {
    T object;
    Operator operator;

    Filter_LEGACY(T object){
        this.object = object;
    }
    Filter_LEGACY(T object, String expression){
        this.object = object;
        setExpression(expression);
    }

    void setExpression(String expression){
        try {
            this.operator = parseSingle(expression);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    boolean filter(){
        try {
            return operator.result();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    String cleanExpression(String expression){
        String clean = expression.replaceAll(" ", "");
        return expression; //TODO:!!!
    }


    Operator parseSingle(String expression) throws Exception{

        String[] splitcol = splitFirst(expression, ":");
        String[] spliteq  = splitFirst(expression, "=");
        String[] s = splitcol;
        if (splitcol[0].length() > spliteq[0].length()){
            return new EQ(spliteq[0], spliteq[1]);
        }

        if (!s[0].startsWith("$")) {
            String fieldName = s[0];
            if (s[1].startsWith("$lte"))  return new LTE(fieldName, splitFirst(s[1], ":")[1]);
            if (s[1].startsWith("$lt" ))  return new LT (fieldName, splitFirst(s[1], ":")[1]);
            if (s[1].startsWith("$gte"))  return new GTE(fieldName, splitFirst(s[1], ":")[1]);
            if (s[1].startsWith("$gt" ))  return new GT (fieldName, splitFirst(s[1], ":")[1]);

            String[] arguments = strBetween(s[1], "[","]").split(",");
            if (s[1].startsWith("$in" ))  return new IN (fieldName, Arrays.asList(arguments));
            if (s[1].startsWith("$nin"))  return new NIN(fieldName, Arrays.asList(arguments));
        }
        else {
            if (s[0].startsWith("$and"))  return new AND(parseList(s[1]));
            if (s[0].startsWith("$or" ))  return new OR (parseList(s[1]));
            if (s[0].startsWith("$not"))  return new NOT(parseList(s[1]));
        }
        return null;
    }

    List<Operator> parseList(String expression) throws Exception {
        String[] subExpressions = strBetween(expression, "[", "]").split(",");
        List<Operator> operators = new LinkedList<>();
        for (String se : subExpressions){
            operators.add(parseSingle(se));
        }
        return operators;
    }

    String[] splitFirst(String string, String splitter){
        int position = string.indexOf(splitter);
        if (position == -1 || position == string.length())
            return new String[]{string};
        return new String[]{
                string.substring(0, position),
                string.substring(position+1)
        };
    }

    String strBetween(String string, String lower, String upper){
        int posLow = string.indexOf(lower);
        int posUpp = string.indexOf(upper);
        if (posLow == -1 || posLow == string.length() || posUpp == -1)
            return string;
        return string.substring(posLow, posUpp);
    }

    //--------------------------------
    int compareNumbers(String fn, Object o2) throws Exception{
        Object fieldVal = object.getClass().getField(fn).get(object);
        return compareNumbers(fieldVal, o2);
    }

    int compareNumbers(Object o1, Object o2) throws Exception {
        if (o1 instanceof Number && o2 instanceof Number)
            return compareNumbers((Number) o1, (Number) o2);

        if (o1 instanceof Number && o2 instanceof String )
            return compareNumbers((Number) o1, NumberFormat.getInstance().parse((String) o2));

        throw new Exception("Not numbers in comparison");
    }

    int compareNumbers(Number n1, Number n2){
        return Double.compare(n1.doubleValue(), n2.doubleValue());
    }


    int compareStrings(Object o1, Object o2){
        if (o1 instanceof String && o2 instanceof String)
            if (((String) o1).equalsIgnoreCase((String) o2))    //TODO: Check case??
                return 0;
        return -1;
    }
    // --------------------------------------------------------

    interface Operator{
        boolean result() throws Exception;
    }

    abstract class Operator_0_N implements Operator{
        List<Operator> rightArgs;

        Operator_0_N(List<Operator> rightArgs){
            this.rightArgs = rightArgs;
        }

//        void addRightArg(Object arg) throws Exception {
//            if (arg instanceof Operator)
//                rightArgs.add((Operator) arg);
//            else
//                throw new Exception("Error"); // TODO:
//        }
    }

    abstract class Operator_1_1 implements Operator{
        String fieldName;
        Object rightArg;

        Operator_1_1(String fieldName, Object rightArg){
            this.fieldName = fieldName;
            this.rightArg = rightArg;
        }
    }

    abstract class Operator_1_N implements Operator{
        String fieldName;
        List<Object> rightArgs;

        Operator_1_N(String fieldName, List<Object> rightArgs){
            this.fieldName = fieldName;
            this.rightArgs = rightArgs;
        }
    }

    // 1 to 1
    class EQ extends Operator_1_1{
        EQ(String fieldName, Object rightArg) {super(fieldName, rightArg);}

        public boolean result() throws Exception{
            Object fieldVal =  object.getClass().getField(fieldName).get(object);
            if (compareStrings(fieldVal, rightArg) == 0)
                return true;
            return compareNumbers(fieldVal, rightArg) == 0;
        }
    }

    class GT extends Operator_1_1{
        GT(String fieldName, Object rightArg) {super(fieldName, rightArg);}
        public boolean result() throws Exception {
            return compareNumbers(fieldName, rightArg) > 0;
        }
    }

    class GTE extends Operator_1_1{
        GTE(String fieldName, Object rightArg) {super(fieldName, rightArg);}
        public boolean result() throws Exception {
            return compareNumbers(fieldName, rightArg) >= 0;
        }
    }

    class LT extends Operator_1_1{
        LT(String fieldName, Object rightArg) {super(fieldName, rightArg);}
        public boolean result() throws Exception {
            return compareNumbers(fieldName, rightArg) < 0;
        }
    }

    class LTE extends Operator_1_1{
        LTE(String fieldName, Object rightArg) {super(fieldName, rightArg);}
        public boolean result() throws Exception {
            return compareNumbers(fieldName, rightArg) <= 0;
        }
    }

    // 1 to N
    class IN extends Operator_1_N{
        IN(String fieldName, List<Object> rightArgs) {super(fieldName, rightArgs);}
        public boolean result() throws Exception{
            List<EQ> eqs = new LinkedList<>();
            boolean isIn = false;
            for (Object arg : rightArgs){
                EQ eq = new EQ(fieldName, arg);
                eqs.add(eq);
                isIn = isIn || eq.result();
            }
            return isIn;
        }
    }

    class NIN extends Operator_1_N{
        IN in = new IN(fieldName, rightArgs);
        NIN(String fieldName, List<Object> rightArgs) {super(fieldName, rightArgs);}
        public boolean result() throws Exception{
            return !in.result();
        }
    }

    class BT extends Operator_1_N{
        GTE gte = new GTE(fieldName, rightArgs.get(0));
        LTE lte = new LTE(fieldName, rightArgs.get(1));
        BT(String fieldName, List<Object> rightArgs) {super(fieldName, rightArgs);}
        public boolean result() throws Exception{
            return gte.result() && lte.result();    // TODO: check arg len
        }
    }

    // 0 to N
    class OR extends Operator_0_N{
        OR(List<Operator> rightArgs) {super(rightArgs);}
        public boolean result() throws Exception {
            boolean result = false;
            for(Operator op : rightArgs){
                result = result || op.result(); // TODO: Improve algebra? if result true -> return true
            }
            return result;
        }
    }

    class AND extends Operator_0_N{
        AND(List<Operator> rightArgs) {super(rightArgs);}
        public boolean result() throws Exception {
            boolean result = true;
            for(Operator op : rightArgs){
                result = result && op.result(); // TODO: Improve algebra? if result false -> return false
            }
            return result;
        }
    }

    class NOT extends Operator_0_N{
        NOT(List<Operator> rightArgs) {super(rightArgs);}
        public boolean result() throws Exception {
            return !rightArgs.get(0).result();  // TODO: check arg len
        }
    }

}