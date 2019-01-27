import java.lang.Math;

public class Operand implements Function{
    private double value;
    private Argument argument;
    private UOperator operator;
    private Function argF;
    public Operand(double value){
        this.value = value;
        this.operator = UOperator.NULL;
    }
    public Operand(Argument argument){
        this.argument = argument;
        this.operator = UOperator.NULL;
    }

    public Operand(UOperator operator, Function argF){
        this.operator = operator;
        this.argF = argF;
    }

    /*public static class Logn implements Function {
            private Function argF;
            private Function base;
            public Logn(Function base, Function argF){
                this.argF = argF;
                this.base = base;
            }
            public double eval(double arg) {
                return Math.log(argF.eval(arg))/Math.log(base.eval(arg));
            }
        }*/
    public double eval(double arg){
        switch (operator){
            case SIGNUM:
                return Math.signum(argF.eval(arg));
            case ABS:
                return Math.abs(argF.eval(arg));
            case SIN:
                return Math.sin(argF.eval(arg));
            case COS:
                return Math.cos(argF.eval(arg));
            case TAN:
                return Math.tan(argF.eval(arg));
            case COT:
                return 1/Math.tan(argF.eval(arg));
            case SH:
                return Math.sinh(argF.eval(arg));
            case CH:
                return Math.cosh(argF.eval(arg));
            case TH:
                return Math.tanh(argF.eval(arg));
            case CTH:
                return 1/Math.tanh(argF.eval(arg));
            case ASIN:
                return Math.asin(argF.eval(arg));
            case ACOS:
                return Math.acos(argF.eval(arg));
            case ATAN:
                return Math.atan(argF.eval(arg));
            case ACOT:
                return Math.atan(argF.eval(arg));
            case ASINH:
            case ACOSH:
            case ATANH:
            case ACOTH:
            case LN:
                return Math.log(argF.eval(arg));
            case LG:
                return Math.log10(argF.eval(arg));
            case LOG2:
                return Math.log(argF.eval(arg))/Math.log(2);
            case PLUS:
                return argF.eval(arg);
            case MINUS:
                return -argF.eval(arg);
            default:
                return argument == Argument.ARGUMENT ? arg : value;
        }
    }
}
enum Argument{
    ARGUMENT,
}
enum UOperator{
    ABS,
    PLUS,
    MINUS,
    SIGNUM,
    SIN,
    COS,
    TAN,
    COT,
    SH,
    CH,
    TH,
    CTH,
    ASIN,
    ACOS,
    ATAN,
    ACOT,
    ASINH,
    ACOSH,
    ATANH,
    ACOTH,
    LN,
    LG,
    LOG2,
    NULL,
}