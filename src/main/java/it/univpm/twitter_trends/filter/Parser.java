package it.univpm.twitter_trends.filter;
import java.util.LinkedList;
import java.util.List;

public class Parser {

    static Operator parse(String expression) throws Exception{
        expression = cleanExpression(expression);
        String[] splitCOL = splitFirst(expression, ":");
        String[] splitEQ  = splitFirst(expression, "=");
        String[] s = splitCOL;
        if (splitCOL[0].length() > splitEQ[0].length())
            return new ArithmeticOP("EQ", splitEQ[0], splitEQ[1]);

        if (s.length == 1) throw new Exception("Invalid expression, too few arguments");

        if (!s[0].startsWith("$")) {
            String fieldName = s[0];
            if (!s[1].startsWith("$"))    return new ArithmeticOP("EQ" , fieldName, s[1]);
            if (s[1].startsWith("$lte"))  return new ArithmeticOP("LT" , fieldName, splitFirst(s[1], ":")[1]);
            if (s[1].startsWith("$lt" ))  return new ArithmeticOP("LTE", fieldName, splitFirst(s[1], ":")[1]);
            if (s[1].startsWith("$gte"))  return new ArithmeticOP("GTE", fieldName, splitFirst(s[1], ":")[1]);
            if (s[1].startsWith("$gt" ))  return new ArithmeticOP("GT" , fieldName, splitFirst(s[1], ":")[1]);

            String[] args = strBetween(s[1], "[","]").split(",");
            if (s[1].startsWith("$in" ))  return new SetOP("IN" , fieldName, args);
            if (s[1].startsWith("$nin"))  return new SetOP("NIN", fieldName, args);
            if (s[1].startsWith("$bt" ))  return new SetOP("BT" , fieldName, args);
        }
        else {
            if (s[0].startsWith("$and"))  return new LogicalOP("AND", parseList(s[1]));
            if (s[0].startsWith("$or" ))  return new LogicalOP("OR" , parseList(s[1]));
            if (s[0].startsWith("$not"))  return new LogicalOP("NOT", parseList(s[1]));
        }
        throw new Exception("Operator not found in parsing, check syntax");
    }

    private static List<Operator> parseList(String expression) throws Exception {
        String[] subExpressions = strBetween(expression, "[", "]").split(",");
        List<Operator> operators = new LinkedList<>();
        for (String se : subExpressions){
            operators.add(parse(se));
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
        int posUpp = string.indexOf(upper);
        if (posLow == -1 || posLow == string.length() || posUpp == -1)
            return string;
        return string.substring(++posLow, posUpp);
    }

    private static String cleanExpression(String expression){
        String input = expression;
        StringBuilder output = new StringBuilder();
        // Remove curly brackets
        input = input.replace("{", "");
        input = input.replace("}", "");
        // TODO: Square brackets validation check (number and order)
        // Remove spaces not between quotation marks
        int n = 0;
        for (String s : input.split("\"")){
            if (n++ % 2 == 0)  s = s.replaceAll(" ", "");
            output.append(s);
        }

        return output.toString(); //TODO:!!!
    }
}
