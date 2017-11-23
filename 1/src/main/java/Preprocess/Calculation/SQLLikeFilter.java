package Preprocess.Calculation;

import Preprocess.JSONPathTraverse;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.JsonNode;

import java.util.Stack;

/**
 * Created by wilhelmus on 21/06/17.
 */
public class SQLLikeFilter {

    private NumberUtils utils;
    private JSONPathTraverse jsonPath;

    public SQLLikeFilter(){
        jsonPath = new JSONPathTraverse();
        utils = new NumberUtils();
    }

    private int isHigherPrec(String str){
        str = str.replaceAll("\\s+","");
        switch(str){
            case "!":
            case "NOT":
                return 6;
            case ")":
            case "(":
                return 5;
            case "*":
            case "/":
            case "div":
            case "mod":
                return 4;
            case "+":
            case "-":
                return 3;
            case "==":
            case "!=":
            case ">=":
            case ">":
            case "<=":
            case "<":
                return 2;
            case "OR":
            case "XOR":
            case "AND":
            default:{
                return 1;
            }
        }

    }

    public String explodeOperation(String exp){
        exp = exp.replaceAll("\\+"," + ");
        exp = exp.replaceAll("-"," - ");
        exp = exp.replaceAll("\\*"," * ");
        exp = exp.replaceAll("/"," / ");
        exp = exp.replaceAll("\\("," ( ");
        exp = exp.replaceAll("\\)"," ) ");
        exp = exp.replaceAll("<"," < ");
        exp = exp.replaceAll(">"," > ");
        exp = exp.replaceAll("="," = ");
        exp = exp.replaceAll("!"," ! ");
        exp = exp.replaceAll("\\|"," | ");
        exp = exp.replaceAll("\\s+"," ");
        return exp;
    }

    public String parseExpression(String exp){
        exp = exp.replaceAll("\\{", " {");
        exp = exp.replaceAll("}", "} ");
        String full[] = exp.split("\\s+");
        String app = "";
        for(String a:full){
            if(a!=null) {
                if (a.length() > 1) {
                    if (a.charAt(0) == '"' || a.charAt(0) == '{') {
                        app += a;
                    } else {
                        app += explodeOperation(a);
                    }
                }
            }
        }
        exp = app;
        int count = 0;
        String ret = "";
        for(int i = 0; i < exp.length();i++){
            if(count > 0)count--;
            else{
                if((exp.charAt(i)=='<')&&((i+2)<exp.length())){
                    if(exp.charAt(i+2)=='='){
                        ret+="<=";
                        count=2;
                    }else ret+=exp.charAt(i);
                }else if((exp.charAt(i)=='>')&&((i+2)<exp.length())){
                    if(exp.charAt(i+2)=='='){
                        ret+=">=";
                        count=2;
                    }else ret+=exp.charAt(i);
                }else if((exp.charAt(i)=='!')&&((i+2)<exp.length())){
                    if(exp.charAt(i+2)=='='){
                        ret+="!=";
                        count=2;
                    }else ret+=exp.charAt(i);
                }else if((exp.charAt(i)=='=')&&((i+2)<exp.length())) {
                    if (exp.charAt(i + 2) == '=') {
                        ret += "==";
                        count = 2;
                    } else ret += exp.charAt(i);
                }else{
                    ret+=exp.charAt(i);
                }
            }
        }
        return ret;
    }

    private boolean isOperand(String str)
    {
        str = str.replaceAll("\\s+","");
        boolean ret = true;
        if(str.equals("+")) ret = false;
        if(str.equals("-")) ret = false;
        if(str.equals("*")) ret = false;
        if(str.equals("/")) ret = false;
        if(str.equals("mod")) ret = false;
        if(str.equals("div")) ret = false;
        if(str.equals("!=")) ret = false;
        if(str.equals("!")) ret = false;
        if(str.equals("<=")) ret = false;
        if(str.equals(">=")) ret = false;
        if(str.equals("==")) ret = false;
        if(str.equals(">")) ret = false;
        if(str.equals("<")) ret = false;
        if(str.equals("OR")) ret = false;
        if(str.equals("AND")) ret = false;
        if(str.equals("XOR")) ret = false;
        if(str.equals("NOT")) ret = false;
        return ret;
    }

    public String convertToPostfix(String expr)
    {
        String ret ="";
        Stack<String> temp = new Stack<>();
        String parsed = parseExpression(expr);
        if(parsed.charAt(0)==' ')
            parsed = parsed.substring(1);
        String a[] = parsed.split("\\s+");
        for(String s : a){
            switch(s){
                case "(":{
                    temp.push(s);
                    break;
                }
                case ")":{
                    if(!temp.empty()) {
                        while (!temp.peek().equals("(")) {
                            ret += temp.pop() + " ";
                        }
                        temp.pop();
                    }
                    break;
                }
                default:{
                    if(isOperand(s)){
                        ret+= s + " ";
                    }else{
                        while (!temp.empty() && !temp.peek().equals("(") && isHigherPrec(s) <= isHigherPrec(temp.peek()) ){
                            ret+= temp.pop() + " ";
                        } // end while
                        temp.push(s);
                    }
                }
            }
        }
        while(!temp.empty()){
            ret+= temp.pop() + " ";
        }
        return ret;
    }

    public boolean solve(String exp,JsonNode node){
        Stack<String> operand = new Stack<>();
        String postfix = convertToPostfix(exp);
        if(postfix.charAt(0)==' ')
            postfix = postfix.substring(1);
        String arr[] = postfix.split("\\s+");
        for(String s : arr){
            if(isOperand(s)){
                operand.push(s);
            }//case unary operator
            else if(s.equals("!") || s.equals("NOT")){
                if(operand.empty()) return false;
                String temp = operand.pop();
                if(temp.contains("{")){
                    temp = temp.replaceAll("\\{","").replaceAll("}","");
                    JsonNode a = jsonPath.solve(temp,node);
                    if(a==null) return false;
                    temp = a.toString();
                }
                operand.push(notOperator(temp));
            }//case binary operator
            else{
                if(operand.empty()) return false;
                String op2 = operand.pop();
                if(op2.contains("{")){
                    op2 = op2.replaceAll("\\{","").replaceAll("}","");
                    JsonNode a = jsonPath.solve(op2,node);
                    if(a==null) return false;
                    op2 = a.toString();
                }
                if(operand.empty()) return false;
                String op1 = operand.pop();
                if(op1.contains("{")){
                    op1 = op1.replaceAll("\\{","").replaceAll("}","");
                    JsonNode a = jsonPath.solve(op1,node);
                    if(a==null) return false;
                    op1 = a.toString();

                }
//                System.out.println("op1 " + op1);
//                System.out.println("op2 " + op2);
                String temp =binaryOperator(op1,op2,s);
//                System.out.println(temp);
                if(temp.equals("error")) return false;
                operand.push(temp);
            }
        }
        if(operand.empty()) return false;
        String result = operand.pop();
        if(result.contains("{")){
            result = result.replaceAll("\\{","").replaceAll("}","");
            JsonNode a = jsonPath.solve(result,node);
            if(a==null) return false;
            result = a.toString();
            System.out.println(a);
        }
        return stringToBoolean(result);
    }

    private boolean stringToBoolean(String exp){
        if(exp.equals("false") || exp.equals("0"))
            return false;
        else
            return true;
    }
    private String notOperator(String exp){
        if(exp.equals("false") || exp.equals("0"))
            return "true";
        else
            return "false";
    }

    private String binaryOperator(String op1,String op2, String operator){
        Double d1 = null;
        Double d2 = null;
        boolean compareNumber = false;
        switch(operator){
            case "*":
            case "/":
            case "div":
            case "mod":
            case "+":
            case "-":
            case ">=":
            case ">":
            case "<=":
            case "<":
                if((utils.isNumber(op1)&&utils.isNumber(op2))) {
                    d1 = Double.valueOf(op1);
                    d2 = Double.valueOf(op2);
                }else{
                    return "error";
                }
                break;
            default:
                if((utils.isNumber(op1)&&utils.isNumber(op2))) {
                    d1 = Double.valueOf(op1);
                    d2 = Double.valueOf(op2);
                    compareNumber = true;
                }
                //do nothing
        }
        switch(operator){
            case "*":
                return String.valueOf(d1*d2);
            case "/":
                return String.valueOf(d1/d2);
            case "div":
                return String.valueOf(d1.intValue() / d2.intValue());
            case "mod":
                return String.valueOf(d1.intValue() % d2.intValue());
            case "+":
                return String.valueOf(d1+d2);
            case "-":
                return String.valueOf(d1-d2);
            case "==":
                if(compareNumber){
                    if(d1.equals(d2))
                        return "true";
                    else return "false";
                }else{
                    if(op1.equals(op2))
                        return "true";
                    else return "false";
                }
            case "!=":
                if(compareNumber){
                    if(!d1.equals(d2))
                        return "true";
                    else return "false";
                }else{
                    if(!op1.equals(op2))
                        return "true";
                    else return "false";
                }
            case ">=":
                if(d1 >= d2)
                    return "true";
                else return "false";
            case ">":
                if(d1 > d2)
                    return "true";
                else return "false";
            case "<=":
                if(d1 <= d2)
                    return "true";
                else return "false";
            case "<":
                if(d1 < d2)
                    return "true";
                else return "false";
            case "OR":
                if(stringToBoolean(op1)||stringToBoolean(op2))
                    return "true";
                else return "false";
            case "XOR":
                if((stringToBoolean(op1)||stringToBoolean(op2))&&!(stringToBoolean(op1)&&(stringToBoolean(op2))))
                    return "true";
                else return "false";
            case "AND":
                if(stringToBoolean(op1)&&stringToBoolean(op2))
                    return "true";
                else return "false";
            default:{
                return "error";
            }
        }
    }

    public boolean compareNumber(String num1,String num2,String equation){
        boolean ret = false;
        if(utils.isNumber(num1)&&utils.isNumber(num2)) {
            Double d1 = Double.valueOf(num1);
            Double d2 = Double.valueOf(num2);
            switch (equation) {
                case "<": {
                    ret = d1 < d2;
                    break;
                }
                case "<=": {
                    ret = d1 <= d2;
                    break;
                }
                case ">=": {
                    ret = d1>=d2;
                    break;
                }
                case ">": {
                    ret = d1>d2;
                    break;
                }
                case "==": {
                    ret = d1==d2;
                    break;
                }
                case "!=": {
                    ret = d1!=d2;
                    break;
                }
            }
        }
        return ret;
    }

    public boolean compareNumber(double d1, double d2,String equation){
        boolean ret = false;
        switch (equation) {
            case "<": {
                ret = d1 < d2;
                break;
            }
            case "<=": {
                ret = d1 <= d2;
                break;
            }
            case ">=": {
                ret = d1>=d2;
                break;
            }
            case ">": {
                ret = d1>d2;
                break;
            }
            case "==": {
                ret = d1==d2;
                break;
            }
            case "!=": {
                ret = d1!=d2;
                break;
            }
        }
        return ret;
    }


}
