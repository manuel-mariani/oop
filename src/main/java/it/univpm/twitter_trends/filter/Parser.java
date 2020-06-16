package it.univpm.twitter_trends.filter;

import java.util.LinkedList;
import java.util.List;

/**
 * Class that generates a tree of Operators from a string expression
 */
public class Parser {

    public static Operator parse(String expression) throws Exception{
        // Clean the expression
        expression = cleanExpression(expression);
        // Call the parser
        return _parse(expression);
    }

    private static Operator _parse(String expression) throws Exception{
        // Get sub expressions, splitting by the first occurrence of ':' and '='
        String[] splitCOL = splitFirst(expression, ":");
        String[] splitEQ  = splitFirst(expression, "=");
        String[] s = splitCOL;

        // If the first operator is '=' (instead of ':'), return an Equal operator
        if (splitEQ[0].length() < splitCOL[0].length())
            return new ComparisonOP("EQ", splitEQ[0], splitEQ[1]);

        // Check sub string array size, if it's 1, throw exception
        if (s.length == 1) throw new Exception("Invalid expression \"" + expression + "\", too few arguments");

        // Check if the first character of the left side of the ':' is a '$'
        // if false, then the sub expression's form is "<fieldname> : <$operator | fieldValue>"
        if (!s[0].startsWith("$")) {
            String fieldName = s[0];
            // Arithmetic operators "<fieldname> : $<arithmetic operator> : <value>"
            if (!s[1].startsWith("$"))    return new ComparisonOP("EQ" , fieldName, s[1]);
            if (s[1].startsWith("$lte"))  return new ComparisonOP("LT" , fieldName, splitFirst(s[1], ":")[1]);
            if (s[1].startsWith("$lt" ))  return new ComparisonOP("LTE", fieldName, splitFirst(s[1], ":")[1]);
            if (s[1].startsWith("$gte"))  return new ComparisonOP("GTE", fieldName, splitFirst(s[1], ":")[1]);
            if (s[1].startsWith("$gt" ))  return new ComparisonOP("GT" , fieldName, splitFirst(s[1], ":")[1]);

            // Set operators "$<set operator> : [<values>, ...]"
            // get arguments inside square brackets
            String[] args = strBetween(s[1], "[","]").split(",");
            if (s[1].startsWith("$in" ))  return new SetOP("IN" , fieldName, args);
            if (s[1].startsWith("$nin"))  return new SetOP("NIN", fieldName, args);
            if (s[1].startsWith("$bt" ))  return new SetOP("BT" , fieldName, args);

            throw new Exception("Operator \"" + s[1] + "\" not in parsing, check syntax");
        }
        // if true, then the sub expression's form is "$<logical operator> : [$<operators>, ...] "
        else {
            // Logical operators
            if (s[0].startsWith("$and"))  return new LogicalOP("AND", parseList(s[1]));
            if (s[0].startsWith("$or" ))  return new LogicalOP("OR" , parseList(s[1]));
            if (s[0].startsWith("$not"))  return new LogicalOP("NOT", parseList(s[1]));
        }
        throw new Exception("Operator \"" + s[0] + "\" not found in parsing, check syntax");
    }

    private static List<Operator> parseList(String expression) throws Exception {
        // Get sub expression between the first and last square brackets
        String strBetweenBrackets = strBetween(expression, "[", "]");

        // Split string by ',' only if they are not between square brackets
        List<String> subExpressions = new LinkedList<>();
        StringBuilder subExp = new StringBuilder();
        int nBrackets = 0;
        for (int i = 0; i < strBetweenBrackets.length(); i++) {
            char c = strBetweenBrackets.charAt(i);
            if (c == ','){
                if (nBrackets > 0) subExp.append(c);
                else {
                    subExpressions.add(subExp.toString());
                    subExp.setLength(0);
                }
            } else {
                if (c == '[')  nBrackets++;
                if (c == ']')  nBrackets--;
                subExp.append(c);
            }
        }
        if (subExp.length() > 0)    subExpressions.add(subExp.toString());

        List<Operator> operators = new LinkedList<>();
        for (String se : subExpressions){
            operators.add(_parse(se));
        }
        return operators;
    }

    private static String[] splitFirst(String string, String splitter){
        int position = string.indexOf(splitter);
        if (position == -1 || position == string.length())
            return new String[]{string};
        return new String[]{
                string.substring(0, position),
                string.substring(position+1)
        };
    }

    private static String strBetween(String string, String lower, String upper){
        int posLow = string.indexOf(lower);
        int posUpp = string.lastIndexOf(upper);
        if (posLow == -1 || posLow == string.length() || posUpp == -1)
            return string;
        return string.substring(++posLow, posUpp);
    }

    private static String cleanExpression(String expression) throws Exception{
        String input = expression;
        StringBuilder output = new StringBuilder();
        // Remove curly brackets
        input = input.replace("{", "");
        input = input.replace("}", "");

        // Square brackets validation check (number and order)
        int nBrackets = 0;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '[') nBrackets++;
            if (c == ']') nBrackets--;
            if (nBrackets < 0) break;
        }
        if (nBrackets != 0)
            throw new Exception("Check square brackets number or order");

        // Remove spaces not between quotation marks
        int n = 0;
        for (String s : input.split("\"")){
            if (n++ % 2 == 0)  s = s.replaceAll(" ", "");
            output.append(s);
        }
        // TODO remove
        System.out.println("clean: " + output.toString());
        return output.toString();
    }
}
