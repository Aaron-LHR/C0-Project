package miniplc0java.instruction;

import miniplc0java.error.ErrorCode;
import miniplc0java.error.GenerateError;
import miniplc0java.tokenizer.IdentType;
import miniplc0java.util.Pos;

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

    public int getIntValue() {
        return intValue;
    }

    public String getGenerateInstruction() throws GenerateError {
//        System.out.println(this.opt);
        switch (this.opt) {
            case nop:
            case pop:
            case load64:
            case store64:
            case add_i:
            case add_f:
            case sub_i:
            case sub_f:
            case mul_i:
            case mul_f:
            case div_i:
            case div_f:
            case div_u:
            case cmp_i:
            case cmp_u:
            case cmp_f:
            case neg_i:
            case neg_f:
            case set_lt:
            case set_gt:
            case ftoi:
            case itof:
            case ret:
            case scan_c:
            case scan_f:
            case scan_i:
            case print_c:
            case print_f:
            case print_i:
            case print_s:
            case println:
                return String.format("%s", this.opt.getGenerateInstruction());
            case loca:
            case arga:
            case globa:
            case call:
            case br:
            case br_false:
            case br_true:
                return String.format("%s%08x", this.opt.getGenerateInstruction(), this.intValue);
            case push:
//                System.out.println("push");
                switch (identType) {
                    case INT:
                        return String.format("%s%016x", this.opt.getGenerateInstruction(), (long)this.intValue);
                    case DOUBLE:
                        return String.format("%s%x", this.opt.getGenerateInstruction(), Double.doubleToLongBits(this.doubleValue));
                    case STRING_LITERAL:
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < stringValue.length(); i++) {
                            stringBuilder.append(String.format("%x", (int) stringValue.charAt(i)));
                        }
                        return String.format("%s%s", this.opt.getGenerateInstruction(), stringBuilder.toString());
                }
            default:
                System.out.println("default");
                System.out.println("InstructionNotFound:" + this.opt);
                System.out.println("IdentType:" + identType);
                throw new GenerateError(ErrorCode.InstructionNotFound, new Pos(0, 0));
        }
    }

    @Override
    public String toString() {
        switch (this.opt) {
            case nop:
            case pop:
            case load64:
            case store64:
            case add_i:
            case add_f:
            case sub_i:
            case sub_f:
            case mul_i:
            case mul_f:
            case div_i:
            case div_f:
            case div_u:
            case cmp_i:
            case cmp_u:
            case cmp_f:
            case neg_i:
            case neg_f:
            case set_lt:
            case set_gt:
            case ftoi:
            case itof:
            case ret:
            case scan_c:
            case scan_f:
            case scan_i:
            case print_c:
            case print_f:
            case print_i:
            case print_s:
            case println:
                return String.format("%s", this.opt);
            case push:
            case loca:
            case arga:
            case globa:
            case call:
            case br:
            case br_false:
            case br_true:
                return String.format("%s %s", this.opt, this.intValue);
            default:
                return "error";
        }
    }
}
