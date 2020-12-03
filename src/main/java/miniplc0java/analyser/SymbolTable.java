package miniplc0java.analyser;

import miniplc0java.tokenizer.IdentType;
import miniplc0java.util.Pos;
import miniplc0java.value.Numeral;

import java.util.HashMap;

public class SymbolTable {
    HashMap<String, SymbolEntry> table;
    private boolean hasNext;
    private int level;
    private int blockLevel;
    private int basePoint;
    private int curPoint;

    public SymbolTable(int level, int blockLevel, int basePoint) {
        this.table = new HashMap<>();
        this.hasNext = false;
        this.level = level;
        this.blockLevel = blockLevel;
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

    public boolean hasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public int getLevel() {
        return level;
    }

    public int getBlockLevel() {
        return blockLevel;
    }

    public int getOffset() {
        return this.curPoint;
    }
}
