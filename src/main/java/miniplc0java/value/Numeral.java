package miniplc0java.value;

import miniplc0java.tokenizer.IdentType;
import miniplc0java.error.AnalyzeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.util.Pos;

public class Numeral {
    IdentType identType;
    double value;

    public IdentType getIdentType() {
        return identType;
    }

    public double getValue() {
        return value;
    }

    public Numeral(IdentType identType, double value) {
        this.identType = identType;
        this.value = value;
    }

    public Numeral add(Numeral numeral, Pos pos) throws AnalyzeError {
        if (numeral.identType != this.identType) {
            throw new AnalyzeError(ErrorCode.TypeMismatch, pos);
        }
        return new Numeral(this.identType, this.value + numeral.value);
    }

    public Numeral minus(Numeral numeral, Pos pos) throws AnalyzeError {
        if (numeral.identType != this.identType) {
            throw new AnalyzeError(ErrorCode.TypeMismatch, pos);
        }
        return new Numeral(this.identType, this.value - numeral.value);
    }

    public Numeral multiply(Numeral numeral, Pos pos) throws AnalyzeError {
        if (numeral.identType != this.identType) {
            throw new AnalyzeError(ErrorCode.TypeMismatch, pos);
        }
        return new Numeral(this.identType, this.value * numeral.value);
    }

    public Numeral divide(Numeral numeral, Pos pos) throws AnalyzeError {
        if (numeral.identType != this.identType) {
            throw new AnalyzeError(ErrorCode.TypeMismatch, pos);
        }
        if (numeral.value == 0.0) {
            throw new AnalyzeError(ErrorCode.DivideZero, pos);
        }
        return new Numeral(this.identType, this.value / numeral.value);
    }

    public void assign(Numeral numeral, Pos pos) throws AnalyzeError {
        if (numeral.identType != this.identType) {
            throw new AnalyzeError(ErrorCode.TypeMismatch, pos);
        }
        if (numeral.value == 0.0) {
            throw new AnalyzeError(ErrorCode.DivideZero, pos);
        }
        this.value = numeral.value;
    }
}
