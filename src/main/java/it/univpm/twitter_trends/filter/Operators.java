package it.univpm.twitter_trends.filter;

import java.text.NumberFormat;
import java.util.List;


abstract class Operator{
    abstract boolean isTrue(Object target) throws Exception;

    static int compare(Object o1, Object o2) throws Exception {
        // Init and null target exception avoidance
        String s2 = (String) o2;
        if (o1 == null)  o1 = "";
        // If o1 is string, compare the two strings
        if (o1 instanceof String)
            return s2.equals(o1) ? 0 : -1;
        // If o1 is number, compare the two numbers,
        // assuming o2 is a string containing only numerics (else throws exception)
        if (o1 instanceof Number){
            Number n1 = (Number) o1;
            Number n2 = NumberFormat.getInstance().parse(s2);
            return Double.compare(n1.doubleValue(), n2.doubleValue());
        }
        // Exception management
        if (o2 == null) throw new Exception("Comparison argument null");
        throw new Exception("Comparison error, check if all fields are comparable");
    }

    static Object getFieldValue(Object target, String fieldName) throws Exception{
        // Split fieldName by '.'
        String[] subFieldsNames = fieldName.split("\\.");
        Object subFieldValue = target;
        try {
            // Iterate through subfields
            for (String sfn : subFieldsNames)
                subFieldValue = subFieldValue.getClass().getField(sfn).get(subFieldValue);
        } catch (Exception ignored){
            throw new Exception("Field name \"" + fieldName + "\" not found");
        }
        return subFieldValue;
    }
}

class ComparisonOP extends Operator {
    String type;
    String fieldName;
    Object argument;

    ComparisonOP(String type, String fieldName, Object argument){
        this.type      = type;
        this.fieldName = fieldName;
        this.argument  = argument;
    }

    public boolean isTrue(Object target) throws Exception {
        Object fieldValue = getFieldValue(target, fieldName);
        if (type.equals("LT" ))  return compare(fieldValue, argument) <  0;
        if (type.equals("LTE"))  return compare(fieldValue, argument) <= 0;
        if (type.equals("EQ" ))  return compare(fieldValue, argument) == 0;
        if (type.equals("GTE"))  return compare(fieldValue, argument) >= 0;
        if (type.equals("GT" ))  return compare(fieldValue, argument) >  0;
        throw new Exception("Operator type not found");
    }
}

class LogicalOP extends Operator {
    String type;
    List<Operator> arguments;

    LogicalOP(String type, List<Operator> arguments){
        this.type      = type;
        this.arguments = arguments;
    }

    public boolean isTrue(Object target) throws Exception {
        if (type.equals("AND")) {
            for (Operator op : arguments){
                if (!op.isTrue(target))  return false;}
            return true;
        }
        if (type.equals("OR" )){
            for (Operator op : arguments){
                if (op.isTrue(target))  return true;}
            return false;
        }
        if (type.equals("NOT"))  return !arguments.get(0).isTrue(target);
        throw new Exception("Operator type not found");
    }
}

class SetOP extends Operator {
    String type;
    String fieldName;
    Object[] arguments;

    SetOP(String type, String fieldName, Object[] arguments) {
        this.type      = type;
        this.fieldName = fieldName;
        this.arguments = arguments;
    }

    public boolean isTrue(Object target) throws Exception {
        Object fieldValue = getFieldValue(target, fieldName);
        if (type.equals("IN" )){
            for (Object arg : arguments){
                if(compare(fieldValue, arg) == 0) return true;}
            return false;
        }
        if (type.equals("NIN")){
            for (Object arg : arguments){
                if(compare(fieldValue, arg) == 0) return false;}
            return true;
        }
        if (type.equals("BT" )){
            return  compare(fieldValue, arguments[0]) >= 0 &&
                    compare(fieldValue, arguments[1]) <= 0;
        }
        throw new Exception("Operator type not found");
    }
}
