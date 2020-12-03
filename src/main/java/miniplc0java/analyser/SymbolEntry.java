package miniplc0java.analyser;

import miniplc0java.tokenizer.IdentType;
import miniplc0java.value.Numeral;

public class SymbolEntry {
    boolean isConstant;
    boolean isInitialized;
    int stackOffset;
    IdentType identType;
    Numeral numeral;
    int level;

    /**
     * @param isConstant:
     * @param isInitialized:
     * @param stackOffset:
     * @param identType:
     * @param numeral:
     * @param level:
     */
    public SymbolEntry(boolean isConstant, boolean isInitialized, int stackOffset, IdentType identType, Numeral numeral, int level) {
        this.isConstant = isConstant;
        this.isInitialized = isInitialized;
        this.stackOffset = stackOffset;
        this.identType = identType;
        this.numeral = numeral;
        this.level = level;
    }

    /**
     * @return the stackOffset
     */
    public int getStackOffset() {
        return stackOffset;
    }

    /**
     * @return the isConstant
     */
    public boolean isConstant() {
        return isConstant;
    }

    /**
     * @return the isInitialized
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * @param isConstant the isConstant to set
     */
    public void setConstant(boolean isConstant) {
        this.isConstant = isConstant;
    }

    /**
     * @param isInitialized the isInitialized to set
     */
    public void setInitialized(boolean isInitialized) {
        this.isInitialized = isInitialized;
    }

    /**
     * @param stackOffset the stackOffset to set
     */
    public void setStackOffset(int stackOffset) {
        this.stackOffset = stackOffset;
    }
}
