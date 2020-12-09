package miniplc0java.generator;

import miniplc0java.tokenizer.IdentType;

import java.util.Collections;

public class Global {
    String isConst;
    IdentType identType;
    String valueCount;
    String content;
    int stringLength;

    public Global(boolean isConst, IdentType identType, String content) {
        if (isConst) {
            this.isConst = "01";
        } else {
            this.isConst = "00";
        }
        this.identType = identType;
        this.content = content;
        if (identType == IdentType.STRING_LITERAL) {
            this.stringLength = content.length();
            this.valueCount = String.format("%08x", stringLength);
        } else {
            this.valueCount = "00000000";
            this.stringLength = 8;
        }
    }

    @Override
    public String toString() {
        if (identType == IdentType.STRING_LITERAL) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < content.length(); i++) {
                stringBuilder.append(String.format("%x", (int) content.charAt(i)));
            }
            return isConst + valueCount + stringBuilder.toString();
        } else {
            return isConst + valueCount + "00".repeat(Math.max(0, stringLength));
        }
    }
}
