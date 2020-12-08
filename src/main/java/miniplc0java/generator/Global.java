package miniplc0java.generator;

import miniplc0java.tokenizer.IdentType;

import java.util.Collections;

public class Global {
    String isConst;
    String valueCount;
    int stringLength;

    public Global(boolean isConst, IdentType identType, int stringLength) {
        if (isConst) {
            this.isConst = "01";
        } else {
            this.isConst = "00";
        }
        if (identType == IdentType.STRING_LITERAL) {
            this.valueCount = String.format("%08x", stringLength);
        } else {
            this.valueCount = "00000000";
        }
        this.stringLength = stringLength;
    }

    @Override
    public String toString() {
        return isConst + valueCount + Collections.nCopies(stringLength, "00");
    }
}
