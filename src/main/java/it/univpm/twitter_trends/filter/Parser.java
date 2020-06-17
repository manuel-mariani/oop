package it.univpm.twitter_trends.filter;

import java.util.LinkedList;
import java.util.List;

/**
 * Class that contains the static metods to generate a tree of Operators from a string expression
 */
public class Parser {

    /**
     * Returns an operator from the passed string expression. The expression must be in the format specified in the
     * readme found at the readme. The string is first cleaned and checked for syntax errors and then parsed.
     * @see <a href="https://github.com/manuel-mariani/oop#filters">Filters usage</a>
     * @param expression the filter's expression
     * @return           the root operator of the operators tree
     * @throws Exception the exception containing a message about the error details
     */
    public static Operator parse(String expression) throws Exception{
        // Clean the expression
        expression = cleanExpression(expression);
        // Call the parser
        return _parse(expression);
    }

    /**
     * Returns the root operator of the operators tree of the specified expression
     * @see #parse(String)
     */
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

    /**
     * Method that parses an expression (typically enclosed by square brackets and separated by commas) returning a
     * List of operators. This is used for the operators that take multiple arguments like the Set and Logical operators
     */
    private static List<Operator> parseList(String expression) throws Exception {
        // Get sub expression between the first and last square brackets
        String strBetweenBrackets = strBetween(expression, "[", "]");

        // Split string by ',' only if they are NOT between square brackets
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
        if (subExp.length() > 0)  subExpressions.add(subExp.toString());

        // Parse the splitted string of sub expressions, forming the returned list
        List<Operator> operators = new LinkedList<>();
        for (String se : subExpressions){
            operators.add(_parse(se));
        }
        return operators;
    }

    /**
     * Returns the string splitted at the FIRST occurrence of the splitter
     * @param string    the string to split
     * @param splitter  the split delimiter
     * @return          the array of sub strings (max size 2)
     */
    private static String[] splitFirst(String string, String splitter){
        int position = string.indexOf(splitter);
        if (position == -1 || position == string.length())
            return new String[]{string};
        return new String[]{
                string.substring(0, position),
                string.substring(position+1)
        };
    }

    /** Returns the string between the first (non inclusive) and the last (non inclusive) delimiter string in a string */
    private static String strBetween(String string, String lower, String upper){
        int posLow = string.indexOf(lower);
        int posUpp = string.lastIndexOf(upper);
        if (posLow == -1 || posLow == string.length() || posUpp == -1)
            return string;
        return string.substring(++posLow, posUpp);
    }

    /**
     * Returns a cleaned expression from a string. The cleaning
     * - removes curly brackets
     * - validates the number and position of square brackets, throwing an exception if it's not valid
     * - removes spaces if they are NOT between quotation marks
     */
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

        return output.toString();
    }
}
