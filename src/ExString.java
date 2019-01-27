import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExString {
    private String inputString;
    private HashMap<Integer, Integer> brackets = new HashMap<>();

    public ExString(String inputString){
        this.inputString = inputString;
    }

    public boolean is_valid(){
        inputString = inputString.replaceAll(" ", "");
        inputString = inputString.toLowerCase();
        Pattern p = Pattern.compile("^[-+*/a-z0-9()^.]+$");
        Matcher m = p.matcher(inputString);
        if(!m.matches()){
            return false;
        }
        Stack<Integer> circle = new Stack<>();
        Stack<Integer> square = new Stack<>();
        int size = inputString.length();
        for (int i = 0; i < size; i++){
            switch (inputString.charAt(i)){
                case '(':
                    circle.push(i);
                    break;
                case '[':
                    square.push(i);
                    break;
                case ')':
                    if (!circle.empty()){
                        if(!square.empty() && square.peek() > circle.peek()){
                            return false;
                        }else circle.pop();
                    }else return false;
                    break;
                case ']':
                    if (!square.empty()){
                        if(!circle.empty() && circle.peek() > square.peek()){
                            return false;
                        }else square.pop();
                    }else return false;
            }
        }
        p = Pattern.compile("^[-+*/0-9()^.x]+$");
        String s = inputString.replaceAll("(?:sin|cos|tg|ctg|asin|acos|atan|acot|sh|ch|th|cth|log2|lg|ln|sign|pi|e|fi)(?=\\(.+\\))", "");
        m = p.matcher(s);
        if (!m.matches()){
            return false;
        }
        p = Pattern.compile("^[0-9().\\[\\]x]+$");
        s = s.replaceAll("(?!=[^-+*/^.])(?:[-+*/^])(?<![^-+*/^])", "");
        m = p.matcher(s);
        if (!m.matches()){
            return false;
        }
        return circle.empty() && square.empty();
    }

    public ArrayList<Object> getObjectList(){
        if(!is_valid()){
            return null;
        }

        ArrayList<Object> lex = new ArrayList<>();

        int size = inputString.length();

        ArrayList<Pattern> patterns = new ArrayList<>();
        ArrayList<Matcher> matchers = new ArrayList<>();

        patterns.add(Pattern.compile("[-+*/^()x]"));
        patterns.add(Pattern.compile("[a-z0-9.]"));
        int k = 0;
        for (int i = 0; i < size; i++){
            String it = new Character(inputString.charAt(i)).toString();
            matchers.add(patterns.get(0).matcher(it));
            matchers.add(patterns.get(1).matcher(it));
            if(matchers.get(0).matches()){
                lex.add(it);
            }else{
                String num = "";
                while (matchers.get(1).matches()){
                    num += it;
                    i++;
                    if (i == size)
                        break;
                    it = new Character(inputString.charAt(i)).toString();
                    matchers.remove(1);
                    matchers.add(patterns.get(1).matcher(it));
                }
                i--;
                lex.add(num);
            }
            matchers.clear();
        }

        Stack<Integer> circle = new Stack<>();
        for (Object x : lex){
            if(x.equals("(")){
                brackets.put(lex.indexOf(x), 0);
                circle.push(lex.indexOf(x));
            }
            if(x.equals(")")){
                brackets.put(circle.pop(), lex.indexOf(x));
            }
        }

        size = lex.size();
        for (int i = 0; i < size; i++){
            Pattern num = Pattern.compile("((-|\\+)?[0-9]+(\\.[0-9]+)?)+");
            Pattern arg = Pattern.compile("x");
            Matcher m = num.matcher(lex.get(i).toString());
            if (m.matches()){
                Operand op = new Operand(Double.valueOf(lex.get(i).toString()));
                lex.remove(i);
                lex.add(i, op);
            }
            m = arg.matcher(lex.get(i).toString());
            if (m.matches()){
                Operand op = new Operand(Argument.ARGUMENT);
                lex.remove(i);
                lex.add(i, op);
            }
        }

        return lex;
    }

    public Expression StrToExpr(){
        ArrayList<Object> lex = getObjectList();
        ArrayList<Integer> priority = new ArrayList<>();
        while (lex.size() != 1){
            if (!brackets.isEmpty()){
                int openL = -1;
                for (Integer i : brackets.keySet()){
                    if (i > openL){
                        openL = i;
                    }
                }
                int closeL = brackets.get(openL);
                for(int i = openL + 1; i < closeL; i++){
                    if(lex.get(i).equals("^")){
                        priority.add(i);
                    }
                }
                for(int i = openL + 1; i < closeL; i++){
                    if(lex.get(i).equals("*") || lex.get(i).equals("/")){
                        priority.add(i);
                    }
                }
                for(int i = openL + 1; i < closeL; i++){
                    if(lex.get(i).equals("+") || lex.get(i).equals("-")){
                        priority.add(i);
                    }
                }
                for (Integer op : priority) {
                    String operator = lex.get(op).toString();
                    if (operator.equals("^")){
                        Expression expr = new Expression(BiOperator.POW, (Operand) lex.get(op - 1), (Operand) lex.get(op + 1));
                        lex.remove(op);
                        lex.remove(op - 1);
                        lex.remove(op + 1);
                        lex.add(op - 1, expr);
                    }else if(operator.equals("*")){
                        Expression expr = new Expression(BiOperator.PRODUCT, (Operand) lex.get(op - 1), (Operand) lex.get(op + 1));
                        lex.remove(op);
                        lex.remove(op - 1);
                        lex.remove(op + 1);
                        lex.add(op - 1, expr);
                    }else if(operator.equals("/")){
                        Expression expr = new Expression(BiOperator.FRACT, (Operand) lex.get(op - 1), (Operand) lex.get(op + 1));
                        lex.remove(op);
                        lex.remove(op - 1);
                        lex.remove(op + 1);
                        lex.add(op - 1, expr);
                    }else if(operator.equals("+")){
                        Expression expr = new Expression(BiOperator.PLUS, (Operand) lex.get(op - 1), (Operand) lex.get(op + 1));
                        lex.remove(op);
                        lex.remove(op - 1);
                        lex.remove(op + 1);
                        lex.add(op - 1, expr);
                    }else {
                        Expression expr = new Expression(BiOperator.MINUS, (Operand) lex.get(op - 1), (Operand) lex.get(op + 1));
                        lex.remove(op);
                        lex.remove(op - 1);
                        lex.remove(op + 1);
                        lex.add(op - 1, expr);
                    }
                    lex.remove(openL);
                    lex.remove(openL+2);
                    brackets.remove(openL);
                }
            }else {
                for(int i = 0; i < lex.size(); i++){
                    if(lex.get(i).equals("^")){
                        priority.add(i);
                    }
                }
                for(int i = 0; i < lex.size(); i++){
                    if(lex.get(i).equals("*") || lex.get(i).equals("/")){
                        priority.add(i);
                    }
                }
                for(int i = 0; i < lex.size(); i++){
                    if(lex.get(i).equals("+") || lex.get(i).equals("-")){
                        priority.add(i);
                    }
                }
                for (int i = 0; i < priority.size(); i ++) {
                    int op = priority.get(i);
                    String operator = lex.get(op).toString();
                    if (operator.equals("^")){
                        Expression expr = new Expression(BiOperator.POW, (Function) lex.get(op - 1), (Function) lex.get(op + 1));
                        lex.remove(op);
                        lex.remove(op);
                        lex.remove(op - 1);
                        lex.add(op - 1, expr);
                    }else if(operator.equals("*")){
                        Expression expr = new Expression(BiOperator.PRODUCT, (Function) lex.get(op - 1), (Function) lex.get(op + 1));
                        lex.remove(op);
                        lex.remove(op);
                        lex.remove(op - 1);
                        lex.add(op - 1, expr);
                    }else if(operator.equals("/")){
                        Expression expr = new Expression(BiOperator.FRACT, (Function) lex.get(op - 1), (Function) lex.get(op + 1));
                        lex.remove(op);
                        lex.remove(op);
                        lex.remove(op - 1);
                        lex.add(op - 1, expr);
                    }else if(operator.equals("+")){
                        Expression expr = new Expression(BiOperator.PLUS, (Function) lex.get(op - 1), (Function) lex.get(op + 1));
                        lex.remove(op);
                        lex.remove(op);
                        lex.remove(op - 1);
                        lex.add(op - 1, expr);
                    }else {
                        Expression expr = new Expression(BiOperator.MINUS, (Function) lex.get(op - 1), (Function) lex.get(op + 1));
                        lex.remove(op);
                        lex.remove(op);
                        lex.remove(op - 1);
                        lex.add(op - 1, expr);
                    }
                }
            }
        }
        return (Expression) lex.get(0);
    }
}
