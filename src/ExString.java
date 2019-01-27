import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExString {
    private String inputString;

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
        p = Pattern.compile("^[-+*/0-9()^.xe]+$");
        String s = inputString.replaceAll("(?:abs|sin|cos|tg|ctg|asin|acos|atan|acot|sh|ch|th|cth|log2|lg|ln|sign)(?=\\(.+\\))|pi|fi", "");
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

        size = lex.size();

        for (int i = 0; i < size; i++){
            Pattern num = Pattern.compile("([0-9]+(\\.[0-9]+)?)+");
            Pattern arg = Pattern.compile("x");
            Matcher m = num.matcher(lex.get(i).toString());
            if (m.matches()){
                Operand op = new Operand(Double.valueOf(lex.get(i).toString()));
                lex.set(i, op);
            }
            m = arg.matcher(lex.get(i).toString());
            if (m.matches()){
                Operand op = new Operand(Argument.ARGUMENT);
                lex.set(i, op);
            }
        }

        for (int i = 1; i < size; i++){
            if(lex.get(i-1).equals("-") && i - 1 == 0 || lex.get(i).equals("-") && lex.get(i-1).equals("(")){
                if (i-1==0){
                    lex.set(i-1, "u-");
                }else {
                    lex.set(i, "u-");;
                }
            }
            if(lex.get(i-1).equals("+") && i - 1 == 0 || lex.get(i).equals("+") && lex.get(i-1).equals("(")){
                if (i-1==0){
                    lex.set(i-1, "u+");
                }else {
                    lex.set(i, "u+");
                }
            }
        }

        return lex;
    }

    public ArrayList<Object> INtoRPN(){
        ArrayList<Object> lex = getObjectList();
        Pattern p = Pattern.compile("abs|sin|cos|tg|ctg|asin|acos|atan|acot|sh|ch|th|cth|log2|lg|ln|sign|u-|u\\+");
        Map<String, Integer> priority = new HashMap<>();
        priority.put("-", 1);
        priority.put("+", 1);
        priority.put("*", 2);
        priority.put("/", 2);
        priority.put("^", 3);
        priority.put("(", 0);
        ArrayList<String> Right = new ArrayList<>();
        Right.add("^");
        Stack<Object> RPNStack = new Stack<>();
        ArrayList<Object> RPNList = new ArrayList<>();
        for(Object token : lex){
            if(token instanceof Operand){
                RPNList.add(token);
            }
            if(p.matcher(token.toString()).matches()){
                RPNStack.push(token);
            }
            if(token.equals("(")){
                RPNStack.push(token);
            }
            if(token.equals(")")){
                if(!RPNStack.empty()) {
                    Object peek = RPNStack.pop();
                    while (!peek.equals("(")) {
                        RPNList.add(peek);
                        peek = RPNStack.pop();
                    }
                }
            }
            Pattern p1 = Pattern.compile("[-+*/^]");
            if(p1.matcher(token.toString()).matches()){
                if (!RPNStack.empty()){
                    Object peek = RPNStack.peek();
                    while (p.matcher(peek.toString()).matches() ||
                            priority.get(peek.toString()) > priority.get(token.toString()) ||
                            !Right.contains(token.toString()) && priority.get(peek.toString()) == priority.get(token.toString())){
                        RPNList.add(RPNStack.pop());
                        if(RPNStack.empty()){
                            break;
                        }
                        peek = RPNStack.peek();
                    }
                }
                RPNStack.add(token);
            }
        }
        while (!RPNStack.empty()){
            RPNList.add(RPNStack.pop());
        }
        return RPNList;
    }

    public Function StrToExpr(){
        ArrayList<Object> lex = INtoRPN();
        int i = 0;
        while(lex.size()!=1){
            Expression exp;
            Operand op;
            switch (lex.get(i).toString()){
                case "^":
                    i = i - 2;
                    exp = new Expression(BiOperator.POW, (Function)lex.get(i), (Function) lex.get(i+1));
                    lex.remove(i);
                    lex.remove(i);
                    lex.set(i, exp);
                    break;
                case "*":
                    i = i - 2;
                    exp = new Expression(BiOperator.PRODUCT, (Function)lex.get(i), (Function) lex.get(i+1));
                    lex.remove(i);
                    lex.remove(i);
                    lex.set(i, exp);
                    break;
                case "/":
                    i = i - 2;
                    exp = new Expression(BiOperator.FRACT, (Function)lex.get(i), (Function) lex.get(i+1));
                    lex.remove(i);
                    lex.remove(i);
                    lex.set(i, exp);
                    break;
                case "+":
                    i = i - 2;
                    exp = new Expression(BiOperator.PLUS, (Function)lex.get(i), (Function) lex.get(i+1));
                    lex.remove(i);
                    lex.remove(i);
                    lex.set(i, exp);
                    break;
                case "-":
                    i = i - 2;
                    exp = new Expression(BiOperator.MINUS, (Function)lex.get(i), (Function) lex.get(i+1));
                    lex.remove(i);
                    lex.remove(i);
                    lex.set(i, exp);
                    break;
                case "u+":
                    i = i - 1;
                    op = new Operand(UOperator.PLUS, (Function) lex.get(i));
                    lex.remove(i);
                    lex.set(i, op);
                    break;
                case "u-":
                    i = i - 1;
                    op = new Operand(UOperator.MINUS, (Function) lex.get(i));
                    lex.remove(i);
                    lex.set(i, op);
                    break;
                case "sin":
                    i = i - 1;
                    op = new Operand(UOperator.SIN, (Function) lex.get(i));
                    lex.remove(i);
                    lex.set(i, op);
                    break;
                case "cos":
                    i = i - 1;
                    op = new Operand(UOperator.COS, (Function) lex.get(i));
                    lex.remove(i);
                    lex.set(i, op);
                    break;
                case "tg":
                    i = i - 1;
                    op = new Operand(UOperator.TAN, (Function) lex.get(i));
                    lex.remove(i);
                    lex.set(i, op);
                    break;
                case "ctg":
                    i = i - 1;
                    op = new Operand(UOperator.COT, (Function) lex.get(i));
                    lex.remove(i);
                    lex.set(i, op);
                    break;
                case "asin":
                    i = i - 1;
                    op = new Operand(UOperator.ASIN, (Function) lex.get(i));
                    lex.remove(i);
                    lex.set(i, op);
                    break;
                case "acos":
                    i = i - 1;
                    op = new Operand(UOperator.ACOS, (Function) lex.get(i));
                    lex.remove(i);
                    lex.set(i, op);
                    break;
                case "atan":
                    i = i - 1;
                    op = new Operand(UOperator.ATAN, (Function) lex.get(i));
                    lex.remove(i);
                    lex.set(i, op);
                    break;
                case "acot":
                    i = i - 1;
                    op = new Operand(UOperator.ACOT, (Function) lex.get(i));
                    lex.remove(i);
                    lex.set(i, op);
                    break;
                case "sh":
                    i = i - 1;
                    op = new Operand(UOperator.SH, (Function) lex.get(i));
                    lex.remove(i);
                    lex.set(i, op);
                    break;
                case "ch":
                    i = i - 1;
                    op = new Operand(UOperator.CH, (Function) lex.get(i));
                    lex.remove(i);
                    lex.set(i, op);
                    break;
                case "th":
                    i = i - 1;
                    op = new Operand(UOperator.TH, (Function) lex.get(i));
                    lex.remove(i);
                    lex.set(i, op);
                    break;
                case "cth":
                    i = i - 1;
                    op = new Operand(UOperator.CTH, (Function) lex.get(i));
                    lex.remove(i);
                    lex.set(i, op);
                    break;
                case "log2":
                    i = i - 1;
                    op = new Operand(UOperator.LOG2, (Function) lex.get(i));
                    lex.remove(i);
                    lex.set(i, op);
                    break;
                case "lg":
                    i = i - 1;
                    op = new Operand(UOperator.LG, (Function) lex.get(i));
                    lex.remove(i);
                    lex.set(i, op);
                    break;
                case "ln":
                    i = i - 1;
                    op = new Operand(UOperator.LN, (Function) lex.get(i));
                    lex.remove(i);
                    lex.set(i, op);
                    break;
                case "sign":
                    i = i - 1;
                    op = new Operand(UOperator.SIGNUM, (Function) lex.get(i));
                    lex.remove(i);
                    lex.set(i, op);
                    break;
                case "abs":
                    i = i - 1;
                    op = new Operand(UOperator.ABS, (Function) lex.get(i));
                    lex.remove(i);
                    lex.set(i, op);
                    break;
            }
            i++;
        }
        return (Function)lex.get(0);
    }

}
