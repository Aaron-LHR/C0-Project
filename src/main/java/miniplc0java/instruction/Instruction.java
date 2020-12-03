package miniplc0java.instruction;

import miniplc0java.tokenizer.IdentType;
import miniplc0java.value.Numeral;

import java.util.IdentityHashMap;
import java.util.Objects;

public class Instruction {
    private Operation opt;
    Numeral x;

    public Instruction(Operation opt) {
        this.opt = opt;
        this.x = new Numeral(IdentType.VOID, 0.0);
    }

    public Instruction(Operation opt, Numeral x) {
        this.opt = opt;
        this.x = x;
    }

    public Instruction() {
        this.opt = Operation.LIT;
        this.x = new Numeral(IdentType.DOUBLE, 0.0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Instruction that = (Instruction) o;
        return opt == that.opt && Objects.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opt, x);
    }

    public Operation getOpt() {
        return opt;
    }

    public void setOpt(Operation opt) {
        this.opt = opt;
    }

    public Numeral getX() {
        return x;
    }

    public void setX(Numeral x) {
        this.x = x;
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
                return String.format("%s %s", this.opt, this.x);
            default:
                return "ILL";
        }
    }
}
