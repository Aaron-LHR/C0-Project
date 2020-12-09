package miniplc0java.analyser;

import miniplc0java.generator.Function;
import miniplc0java.generator.Global;
import miniplc0java.instruction.Instruction;

import java.util.*;

public class AnalyseResult {
    private HashMap<String, SymbolEntry> symbolTable;
    private ArrayList<Map.Entry<String, SymbolEntry>> symbolList;
    private HashMap<String, FunctionEntry> functionSymbolTable;
    private ArrayList<Map.Entry<String, FunctionEntry>> functionList;

    public AnalyseResult(HashMap<String, SymbolEntry> symbolTable, HashMap<String, FunctionEntry> functionSymbolTable) {
        this.symbolTable = symbolTable;
        this.functionSymbolTable = functionSymbolTable;
        symbolList = new ArrayList<>(symbolTable.entrySet());
        symbolList.sort(Comparator.comparingInt(o -> o.getValue().getStackOffset()));
        functionList = new ArrayList<>(functionSymbolTable.entrySet());
        functionList.sort(Comparator.comparingInt(o -> o.getValue().getStackOffset()));
    }

    public HashMap<String, SymbolEntry> getSymbolTable() {
        return symbolTable;
    }

    public HashMap<String, FunctionEntry> getFunctionSymbolTable() {
        return functionSymbolTable;
    }

    @Override
    public String toString() {
        StringBuilder functionStringBuilder = new StringBuilder();
        for (Map.Entry<String, FunctionEntry> entry: functionList) {
            FunctionEntry functionEntry = entry.getValue();
            functionStringBuilder.append("functionName:").append(entry.getKey()).append("\n");
            functionStringBuilder.append(functionEntry).append("\n");
            functionStringBuilder.append("\n");
        }
        StringBuilder symbolStringBuilder = new StringBuilder();
        for (Map.Entry<String, SymbolEntry> entry: symbolList) {
            SymbolEntry symbolEntry = entry.getValue();
            symbolStringBuilder.append("VariableName:").append(entry.getKey()).append("\n");
            symbolStringBuilder.append(symbolEntry).append("\n");
            symbolStringBuilder.append("\n");
        }
        return "全局符号表为：\n" +
                symbolStringBuilder.toString() + "\n" +
                "函数表为：\n" +
                functionStringBuilder.toString();
    }
}
