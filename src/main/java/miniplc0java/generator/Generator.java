package miniplc0java.generator;

import miniplc0java.analyser.AnalyseResult;
import miniplc0java.analyser.FunctionEntry;
import miniplc0java.analyser.SymbolEntry;
import miniplc0java.error.GenerateError;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Generator {
    private HashMap<String, SymbolEntry> symbolTable;
    private ArrayList<Map.Entry<String, SymbolEntry>> symbolList;
    private HashMap<String, FunctionEntry> functionSymbolTable;
    private ArrayList<Map.Entry<String, FunctionEntry>> functionList;
    String magic = "72303b3e";
    String version = "00000001";
    String globals_count;
    ArrayList<Global> globals = new ArrayList<>();
    String functions_count;
    ArrayList<Function> functions = new ArrayList<>();

    public Generator(AnalyseResult analyseResult) {
        this.symbolTable = analyseResult.getSymbolTable();
        this.functionSymbolTable = analyseResult.getFunctionSymbolTable();
        symbolList = new ArrayList<>(symbolTable.entrySet());
        symbolList.sort(Comparator.comparingInt(o -> o.getValue().getStackOffset()));
        functionList = new ArrayList<>(functionSymbolTable.entrySet());
        functionList.sort(Comparator.comparingInt(o -> o.getValue().getStackOffset()));
    }

    public void generate() throws GenerateError {
        globals_count = String.format("%08x", symbolTable.size());
        for (Map.Entry<String, SymbolEntry> entry: symbolList) {
            SymbolEntry symbolEntry = entry.getValue();
            globals.add(new Global(symbolEntry.isConstant(), symbolEntry.getIdentType(), symbolEntry.getStringContent()));
        }
        functions_count = String.format("%08x", functionSymbolTable.size());
//        System.out.println(String.format("%08x", symbolTable.size()));
        for (Map.Entry<String, FunctionEntry> entry: functionList) {
            FunctionEntry functionEntry = entry.getValue();
//            System.out.println(entry.getKey());
//            System.out.println(entry.getValue().getSizeOfListOfSymbolTable());
//            System.out.println(entry.getValue().getFunction_param_list().size());
            functions.add(new Function(functionEntry.getFunctionNameOffset(), functionEntry.getReturnValueType(), functionEntry.getFunction_param_list().size(), functionEntry.getSizeOfListOfSymbolTable(), functionEntry.getInstructions()));
        }
    }

    @Override
    public String toString() {
        StringBuilder globalsBuilder = new StringBuilder();
        for (Global global: globals) {
            globalsBuilder.append(global);
        }
        StringBuilder functionsBuilder = new StringBuilder();
        for (Function function: functions) {
            functionsBuilder.append(function);
        }
//        System.out.println(symbolList);
        return magic + version + globals_count + globalsBuilder.toString() + functions_count + functionsBuilder.toString();
    }
}
