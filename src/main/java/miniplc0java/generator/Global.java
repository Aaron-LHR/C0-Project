package miniplc0java.generator;

import miniplc0java.tokenizer.IdentType;

public class Global {
    String isConst;
    String valueCount;

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
    }
}
