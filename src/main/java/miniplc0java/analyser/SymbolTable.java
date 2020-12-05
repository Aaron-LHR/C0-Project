package miniplc0java.analyser;

import miniplc0java.tokenizer.IdentType;
import miniplc0java.Numeral.Numeral;

import java.util.HashMap;

public class SymbolTable {
    HashMap<String, SymbolEntry> table;
    private int level;
    private int basePoint;
    private int curPoint;

    public SymbolTable(int level, int basePoint) {
        this.table = new HashMap<>();
        this.level = level;
        this.basePoint = basePoint;
        this.curPoint = basePoint;
    }

    public SymbolEntry get(String name) {
        return this.table.get(name);
    }

    public int put(String name, boolean isInitialized, boolean isConstant, IdentType identType, Numeral numeral) {
        this.table.put(name, new SymbolEntry(isConstant, isInitialized, this.curPoint, identType, numeral, this.level));
        return this.curPoint++;
    }

    public int getLevel() {
        return level;
    }

    public int getOffset() {
        return this.curPoint;
    }
}

enum IdentLocation {
    global, arga, loca
}
