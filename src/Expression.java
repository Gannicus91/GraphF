
public class Expression implements Function{

    public BiOperator operator;
    private Function op_1;
    private Function op_2;

    public Expression(BiOperator operator, Function op_1, Function op_2){
        this.operator = operator;
        this.op_1 = op_1;
        this.op_2 = op_2;
    }

    public double eval(double arg){
        if (operator == BiOperator.PLUS){
            return op_1.eval(arg) + op_2.eval(arg);
        }else if(operator == BiOperator.MINUS){
            return op_1.eval(arg) - op_2.eval(arg);
        }else if(operator == BiOperator.PRODUCT){
            return op_1.eval(arg) * op_2.eval(arg);
        }else if(operator == BiOperator.FRACT){
            if (op_2.eval(arg) != 0)
                return op_1.eval(arg) / op_2.eval(arg);
            else return Integer.MAX_VALUE;
        }else {
            return Math.pow(op_1.eval(arg), op_2.eval(arg));
        }
    }
}

enum BiOperator{
    PLUS,
    MINUS,
    PRODUCT,
    FRACT,
    POW,

}
