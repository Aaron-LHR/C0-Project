package miniplc0java.instruction;

import miniplc0java.tokenizer.IdentType;

import java.util.Objects;

public class Instruction {
    private Operation opt;
    int intValue;
    double doubleValue;
    String stringValue;
    IdentType identType;

    public Instruction(Operation opt) {
        this.opt = opt;
        this.identType = IdentType.VOID;
    }

    public Instruction(Operation opt, int intValue) {
        this.opt = opt;
        this.intValue = intValue;
        this.identType = IdentType.INT;
    }

    public Instruction(Operation opt, double doubleValue) {
        this.opt = opt;
        this.doubleValue = doubleValue;
        this.identType = IdentType.DOUBLE;
    }

    public Instruction(Operation opt, String stringValue) {
        this.opt = opt;
        this.stringValue = stringValue;
        this.identType = IdentType.STRING_LITERAL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Instruction that = (Instruction) o;
        return opt == that.opt && Objects.equals(intValue, that.intValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opt, intValue);
    }

    public Operation getOpt() {
        return opt;
    }

    public void setOpt(Operation opt) {
        this.opt = opt;
    }

//    public int getValue() {
//        if (this.identType == IdentType.INT) {
//            return intValue;
//        } else if (this.identType == IdentType.DOUBLE) {
//            return doubleValue;
//        }
//
//    }

    public void setValue(double doubleValue) {
        this.doubleValue = doubleValue;
        this.identType = IdentType.DOUBLE;
    }

    public void setValue(int intValue) {
        this.intValue = intValue;
        this.identType = IdentType.INT;
    }

    public void setValue(String stringValue) {
        this.stringValue = stringValue;
        this.identType = IdentType.STRING_LITERAL;
    }

    @Override
    public String toString() {
        switch (this.opt) {
            case ADD:
            case DIV:
            case ILL:
            case MUL:
            case SUB:
            case WRT:
                return String.format("%s", this.opt);
            case LIT:
            case LOD:
            case STO:
                return String.format("%s %s", this.opt, this.intValue);
            default:
                return "ILL";
        }
    }
}
