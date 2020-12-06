package miniplc0java.analyser;

import miniplc0java.instruction.Instruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnalyseResult {
    private HashMap<String, SymbolEntry> symbolTable;
    private HashMap<String, FunctionEntry> functionSymbolTable;

    public AnalyseResult(HashMap<String, SymbolEntry> symbolTable, HashMap<String, FunctionEntry> functionSymbolTable) {
        this.symbolTable = symbolTable;
        this.functionSymbolTable = functionSymbolTable;
    }

    public HashMap<String, SymbolEntry> getSymbolTable() {
        return symbolTable;
    }

    public HashMap<String, FunctionEntry> getFunctionSymbolTable() {
        return functionSymbolTable;
    }

    @Override
    public String toString() {
        return "全局符号表为：\n" +
                symbolTable + "\n" +
                "函数表为：\n" +
                functionSymbolTable;
    }
}
