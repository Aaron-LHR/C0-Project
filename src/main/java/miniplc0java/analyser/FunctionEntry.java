package miniplc0java.analyser;

import miniplc0java.instruction.Instruction;
import miniplc0java.tokenizer.IdentType;

import java.util.ArrayList;

public class FunctionEntry {
    private ArrayList<IdentType> function_param_list;
    private IdentType returnValueType;
    private ArrayList<Instruction> instructions;
    private ArrayList<SymbolTable> listOfSymbolTable;
    private int stackOffset;
    private int functionNameOffset;

    /**
     * @param function_param_list:
     * @param returnValueType:
     * @param stackOffset:
     */
    public FunctionEntry(ArrayList<IdentType> function_param_list, IdentType returnValueType, ArrayList<Instruction> instructions, ArrayList<SymbolTable> functionSymbolTable, int stackOffset, int functionNameOffset) {
        this.function_param_list = function_param_list;
        this.returnValueType = returnValueType;
        this.instructions = instructions;
        this.listOfSymbolTable = functionSymbolTable;
        this.stackOffset = stackOffset;
        this.functionNameOffset = functionNameOffset;
    }

    public FunctionEntry() {
    }

    public FunctionEntry(ArrayList<IdentType> function_param_list, IdentType returnValueType, int stackOffset, int functionNameOffset) {
        this.function_param_list = function_param_list;
        this.returnValueType = returnValueType;
        this.instructions = new ArrayList<>();
        this.listOfSymbolTable = new ArrayList<>();
        this.stackOffset = stackOffset;
        this.functionNameOffset = functionNameOffset;
    }

    public void addSymbolTable(SymbolTable symbolTable) {
        this.listOfSymbolTable.add(symbolTable);
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

    public ArrayList<SymbolTable> getListOfSymbolTable() {
        return listOfSymbolTable;
    }

    public int getFunctionNameOffset() {
        return functionNameOffset;
    }

    /**
     * @param stackOffset the stackOffset to set
     */
    public void setStackOffset(int stackOffset) {
        this.stackOffset = stackOffset;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (SymbolTable symbolTable: listOfSymbolTable) {
            stringBuilder.append(symbolTable).append("\n");
        }
        return  "function_param_list=" + function_param_list +
                "\nreturnValueType=" + returnValueType +
                "\ninstructions=" + instructions +
                "\nlistOfSymbolTable=\n    " + stringBuilder.toString() +
                "stackOffset=" + stackOffset;
    }
}
