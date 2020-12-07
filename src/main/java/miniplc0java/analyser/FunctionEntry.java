package miniplc0java.analyser;

import miniplc0java.instruction.Instruction;
import miniplc0java.tokenizer.IdentType;

import java.util.ArrayList;

public class FunctionEntry {
    private ArrayList<IdentType> function_param_list;
    private IdentType returnValueType;
    private ArrayList<Instruction> instructions;
    private int NumOfLocal;
    private int stackOffset;

    /**
     * @param function_param_list:
     * @param returnValueType:
     * @param stackOffset:
     */
    public FunctionEntry(ArrayList<IdentType> function_param_list, IdentType returnValueType, ArrayList<Instruction> instructions, int NumOfLocal, int stackOffset) {
        this.function_param_list = function_param_list;
        this.returnValueType = returnValueType;
        this.instructions = instructions;
        this.NumOfLocal = NumOfLocal;
        this.stackOffset = stackOffset;
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    /**
     * @return the stackOffset
     */
    public int getStackOffset() {
        return stackOffset;
    }

    public ArrayList<IdentType> getFunction_param_list() {
        return function_param_list;
    }

    public IdentType getReturnValueType() {
        return returnValueType;
    }

    /**
     * @param stackOffset the stackOffset to set
     */
    public void setStackOffset(int stackOffset) {
        this.stackOffset = stackOffset;
    }
}
