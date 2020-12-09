package miniplc0java.analyser;

import miniplc0java.instruction.Operation;
import miniplc0java.tokenizer.IdentType;

public class SymbolEntry {
    private boolean isConstant;
    private boolean isInitialized;
    private int stackOffset;
    private IdentType identType;
    private int level;
    private Operation operation;
    private String stringContent;
    private int stringLength;

    /**
     * @param isConstant:
     * @param isInitialized:
     * @param stackOffset:
     * @param identType:
     * @param level:
     */
    public SymbolEntry(boolean isConstant, boolean isInitialized, int stackOffset, IdentType identType, int level, String content) {
        this.isConstant = isConstant;
        this.isInitialized = isInitialized;
        this.stackOffset = stackOffset;
        this.identType = identType;
        this.level = level;
        if (level == 0) {
            operation = Operation.globa;
        } else if (level == 1) {
            operation = Operation.arga;
        } else {
            operation = Operation.loca;
        }
        this.stringContent = content;
        this.stringLength = content.length();
    }

    /**
     * @return the stackOffset
     */
    public int getStackOffset() {
        return stackOffset;
    }

    public IdentType getIdentType() {
        return identType;
    }

    public int getLevel() {
        return level;
    }

    public Operation getOperation() {
        return operation;
    }

    public int getStringLength() {
        return stringLength;
    }

    public Operation getOperationByLocation() {
        return operation;
    }

    public String getStringContent() {
        return stringContent;
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

    @Override
    public String toString() {
        return "SymbolEntry{" +
                "isConstant=" + isConstant +
                ", isInitialized=" + isInitialized +
                ", stackOffset=" + stackOffset +
                ", identType=" + identType +
                ", level=" + level +
                ", operation=" + operation +
                ", content=" + stringContent + '}';
    }
}
