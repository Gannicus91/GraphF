import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Main {

    public static void main(String[] args) throws IOException {
        Expression func = new Expression
                (BiOperator.PLUS,
                new Operand(UOperator.ABS, new Expression(BiOperator.POW, new Operand(Argument.ARGUMENT), new Operand(3))),
                new Operand(5));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String s = bufferedReader.readLine();
        ExString f = new ExString(s);
        ArrayList<Object> l = f.getObjectList();
        Expression e = f.StrToExpr();
        System.out.println(e.eval(2));
    }

    public static String format(String inputString){
        inputString = inputString.replaceAll(" ", "");
        inputString = inputString.toLowerCase();
        return inputString;
    }
}
