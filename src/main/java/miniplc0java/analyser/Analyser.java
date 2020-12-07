package miniplc0java.analyser;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.CompileError;
import miniplc0java.error.ErrorCode;
import miniplc0java.error.ExpectedTokenError;
import miniplc0java.error.TokenizeError;
import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.IdentType;
import miniplc0java.tokenizer.Tokenizer;
import miniplc0java.util.Pos;

import java.util.*;

public final class Analyser {

    Tokenizer tokenizer;


    /** 当前偷看的 token */
    Token peekedToken = null;

    /** 符号表 */
    ArrayList<SymbolTable> listOfSymbolTable = new ArrayList<>();
    HashMap<String, FunctionEntry> functionSymbolTable = new HashMap<>();
    int globalStringIndex = 0;

    /** 下一个变量的栈偏移 */
    int nextOffset = 0;

//    int[][] OPGMatrix = new int[12][12];
    HashMap<TokenType, Integer> operatorPriority = new HashMap<>();
    HashMap<TokenType, HashMap<IdentType, ArrayList<Operation>>> binaryOperation = new HashMap<>();
    ArrayList<Integer> whileStack = new ArrayList<>();
    ArrayList<ArrayList<Instruction>> breakStack = new ArrayList<>();
    String curFunctionName;
    ArrayList<SymbolTable> curFunctionSymbolTable = new ArrayList<>();

    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        buildOperatorPriorityTable();
        buildBinaryOperationTable();
    }

    private void buildOperatorPriorityTable() {
        operatorPriority.put(TokenType.PRE_MINUS, 6);
        operatorPriority.put(TokenType.AS_KW, 5);
        operatorPriority.put(TokenType.MUL, 4);
        operatorPriority.put(TokenType.DIV, 4);
        operatorPriority.put(TokenType.PLUS, 3);
        operatorPriority.put(TokenType.MINUS, 3);
        operatorPriority.put(TokenType.EQ, 2);
        operatorPriority.put(TokenType.NEQ, 2);
        operatorPriority.put(TokenType.LT, 2);
        operatorPriority.put(TokenType.GT, 2);
        operatorPriority.put(TokenType.LE, 2);
        operatorPriority.put(TokenType.GE, 2);
        operatorPriority.put(TokenType.ASSIGN, 1);
        operatorPriority.put(TokenType.None, 0);
//        OPGMatrix[operatorPriority.get(TokenType.PLUS)][operatorPriority.get(TokenType.PLUS)] = 1;  // >
    }

    private void buildBinaryOperationTable() {
        HashMap<IdentType, ArrayList<Operation>> intOrDouble = new HashMap<>();
        ArrayList<Operation> operations = new ArrayList<>();

        operations.add(Operation.add_i);
        intOrDouble.put(IdentType.INT, operations);
        operations.clear();
        operations.add(Operation.add_f);
        intOrDouble.put(IdentType.DOUBLE, operations);
        binaryOperation.put(TokenType.PLUS, intOrDouble);

        operations.clear();
        operations.add(Operation.sub_i);
        intOrDouble.replace(IdentType.INT, operations);
        operations.clear();
        operations.add(Operation.sub_f);
        intOrDouble.replace(IdentType.DOUBLE, operations);
        binaryOperation.put(TokenType.MINUS, intOrDouble);

        operations.clear();
        operations.add(Operation.mul_i);
        intOrDouble.replace(IdentType.INT, operations);
        operations.clear();
        operations.add(Operation.mul_f);
        intOrDouble.replace(IdentType.DOUBLE, operations);
        binaryOperation.put(TokenType.MUL, intOrDouble);

        operations.clear();
        operations.add(Operation.div_i);
        intOrDouble.replace(IdentType.INT, operations);
        operations.clear();
        operations.add(Operation.div_f);
        intOrDouble.replace(IdentType.DOUBLE, operations);
//        intOrDouble.replace(IdentType.DOUBLE, Operation.div_u);
        binaryOperation.put(TokenType.DIV, intOrDouble);

        operations.clear();
        operations.add(Operation.cmp_i);
        intOrDouble.replace(IdentType.INT, operations);
        operations.clear();
        operations.add(Operation.cmp_f);
        intOrDouble.replace(IdentType.DOUBLE, operations);
        binaryOperation.put(TokenType.EQ, intOrDouble);

        operations.clear();
        operations.add(Operation.cmp_i);
        intOrDouble.replace(IdentType.INT, operations);
        operations.clear();
        operations.add(Operation.cmp_f);
        intOrDouble.replace(IdentType.DOUBLE, operations);
        binaryOperation.put(TokenType.EQ, intOrDouble);

        operations.clear();
        operations.add(Operation.cmp_i);
        intOrDouble.replace(IdentType.INT, operations);
        operations.clear();
        operations.add(Operation.cmp_f);
        intOrDouble.replace(IdentType.DOUBLE, operations);
        binaryOperation.put(TokenType.EQ, intOrDouble);

        operations.clear();
        operations.add(Operation.cmp_i);
        intOrDouble.replace(IdentType.INT, operations);
        operations.clear();
        operations.add(Operation.cmp_f);
        intOrDouble.replace(IdentType.DOUBLE, operations);
        binaryOperation.put(TokenType.EQ, intOrDouble);

        operations.clear();
        operations.add(Operation.cmp_i);
        intOrDouble.replace(IdentType.INT, operations);
        operations.clear();
        operations.add(Operation.cmp_f);
        intOrDouble.replace(IdentType.DOUBLE, operations);
        binaryOperation.put(TokenType.NEQ, intOrDouble);

        operations.clear();
        operations.add(Operation.cmp_i);
        operations.add(Operation.set_gt);
        intOrDouble.replace(IdentType.INT, operations);
        operations.clear();
        operations.add(Operation.cmp_f);
        operations.add(Operation.set_gt);
        intOrDouble.replace(IdentType.DOUBLE, operations);
        binaryOperation.put(TokenType.GT, intOrDouble);

        operations.clear();
        operations.add(Operation.cmp_i);
        operations.add(Operation.set_lt);
        intOrDouble.replace(IdentType.INT, operations);
        operations.clear();
        operations.add(Operation.cmp_f);
        operations.add(Operation.set_lt);
        intOrDouble.replace(IdentType.DOUBLE, operations);
        binaryOperation.put(TokenType.LT, intOrDouble);

        operations.clear();
        operations.add(Operation.cmp_i);
        operations.add(Operation.set_lt);
        intOrDouble.replace(IdentType.INT, operations);
        operations.clear();
        operations.add(Operation.cmp_f);
        operations.add(Operation.set_lt);
        intOrDouble.replace(IdentType.DOUBLE, operations);
        binaryOperation.put(TokenType.GE, intOrDouble);

        operations.clear();
        operations.add(Operation.cmp_i);
        operations.add(Operation.set_gt);
        intOrDouble.replace(IdentType.INT, operations);
        operations.clear();
        operations.add(Operation.cmp_f);
        operations.add(Operation.set_gt);
        intOrDouble.replace(IdentType.DOUBLE, operations);
        binaryOperation.put(TokenType.LE, intOrDouble);
        }

    public AnalyseResult analyse() throws CompileError {
        analyseProgram();
        // TODO: 2020/12/4 analyse时要返回：
        //1. 全局符号表
        //2. 函数表（函数的所有属性和指令集）
        return new AnalyseResult(listOfSymbolTable.get(0).getTable(), functionSymbolTable);
    }

    /**
     * 查看下一个 Token
     * 
     * @return
     * @throws TokenizeError
     */
    private Token peek() throws TokenizeError {
        if (peekedToken == null) {
            peekedToken = tokenizer.nextToken();
        }
        return peekedToken;
    }

    /**
     * 获取下一个 Token
     * 
     * @return
     * @throws TokenizeError
     */
    private Token next() throws TokenizeError {
        if (peekedToken != null) {
            var token = peekedToken;
            peekedToken = null;
            return token;
        } else {
            return tokenizer.nextToken();
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则返回 true
     * 
     * @param tt
     * @return
     * @throws TokenizeError
     */
    private boolean check(TokenType tt) throws TokenizeError {
        var token = peek();
        return token.getTokenType() == tt;
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回这个 token
     * 
     * @param tt 类型
     * @return 如果匹配则返回这个 token，否则返回 null
     * @throws TokenizeError
     */
    private Token nextIf(TokenType tt) throws TokenizeError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            return null;
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回，否则抛出异常
     * 
     * @param tt 类型
     * @return 这个 token
     * @throws CompileError 如果类型不匹配
     */
    private Token expect(TokenType tt) throws CompileError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            throw new ExpectedTokenError(tt, token);
        }
    }

    /**
     * 获取下一个变量的栈偏移
     * 
     * @return
     */
    private int getNextVariableOffset() {
        return this.nextOffset++;
    }

    /**
     * 添加一个符号
     * 
     * @param name          名字
     * @param isInitialized 是否已赋值
     * @param isConstant    是否是常量
     * @param curPos        当前 token 的位置（报错用）
     * @throws AnalyzeError 如果重复定义了则抛异常
     */
    private int addSymbol(String name, boolean isInitialized, boolean isConstant, Pos curPos, IdentType identType) throws AnalyzeError {
        SymbolTable symbolTable = this.listOfSymbolTable.get(this.listOfSymbolTable.size() - 1);
        if (symbolTable.get(name) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        } else {
            //            if (this.listOfSymbolTable.size() > 2 && this.listOfSymbolTable.size() > maxSize) {
//                maxSize = this.listOfSymbolTable.size();
//                ArrayList<SymbolTable> localTable = (ArrayList<SymbolTable>) this.listOfSymbolTable.clone();
//                localTable.remove(0);
//                localTable.remove(1);
//                addFunctionLocalTable(localTable, curPos);
//            } else {
//
//            }
            return symbolTable.put(name, isInitialized, isConstant, identType);
        }
    }

    private SymbolEntry getSymbol(String name, Pos curPos) throws AnalyzeError {
        for (int index = this.listOfSymbolTable.size() - 1; index >= 0; index--) {
            SymbolTable symbolTable = this.listOfSymbolTable.get(index);
            var entry = symbolTable.get(name);
            if (entry != null) {
                return entry;
            }
        }
        throw new AnalyzeError(ErrorCode.VariableNotDeclared, curPos);
    }

    private boolean curIsGlobal() {
        return this.listOfSymbolTable.size() == 1;
    }

    private void addFunctionSymbol(String name, ArrayList<IdentType> function_param_list, IdentType returnValueType, ArrayList<Instruction> instructions, ArrayList<SymbolTable> listOfSymbolTable, Pos curPos) throws AnalyzeError {
        SymbolTable globalSymbolTable = this.listOfSymbolTable.get(0);
        if (globalSymbolTable.get(name) != null || functionSymbolTable.get(name) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        } else {
            this.functionSymbolTable.put(name, new FunctionEntry(function_param_list, returnValueType, instructions, listOfSymbolTable, getNextVariableOffset()));
        }
    }

    private FunctionEntry getFunctionSymbol(String name, Pos curPos) throws AnalyzeError {
        FunctionEntry functionEntry = functionSymbolTable.get(name);
        if (functionEntry == null) {
            throw new AnalyzeError(ErrorCode.FunctionNotDeclared, curPos);
        } else {
            return functionEntry;
        }
    }

    private void addFunctionLocalTable(SymbolTable symbolTable, Pos curPos) throws AnalyzeError {
//        FunctionEntry functionEntry = functionSymbolTable.get(this.curFunctionName);
//        if (functionEntry == null) {
//            throw new AnalyzeError(ErrorCode.FunctionNotDeclared, curPos);
//        } else {
//            functionEntry.addSymbolTable(symbolTable);
//        }
        this.curFunctionSymbolTable.add(symbolTable);
    }

    private SymbolTable addScope() {
        int level, basePoint;
        if (listOfSymbolTable.size() == 0) {
            level = 0;
            basePoint = 0;
        } else {
            SymbolTable lastSymbolTable = listOfSymbolTable.get(listOfSymbolTable.size() - 1);
            level = lastSymbolTable.getLevel() + 1;
            basePoint = lastSymbolTable.getOffset();
        }
        SymbolTable symbolTable = new SymbolTable(level, basePoint);
        listOfSymbolTable.add(symbolTable);
        return symbolTable;
    }

    private void removeScope() {
        this.listOfSymbolTable.remove(this.listOfSymbolTable.size() - 1);
    }

    private void checkTypeMatch(IdentType I1, IdentType I2, Pos curPos) throws AnalyzeError {
        if (I1 != I2) {
            throw new AnalyzeError(ErrorCode.TypeMisMatch, curPos);
        }
    }

    /**
     * 设置符号为已赋值
     * 
     * @param name   符号名称
     * @param curPos 当前位置（报错用）
     * @throws AnalyzeError 如果未定义则抛异常
     */
    private void initializeSymbol(String name, Pos curPos) throws AnalyzeError {
        var entry = getSymbol(name, curPos);
        entry.setInitialized(true);
    }

    /**
     * 获取变量在栈上的偏移
     *
     * @param name   符号名
     * @param curPos 当前位置（报错用）
     * @return 栈偏移
     * @throws AnalyzeError
     */
    private int getOffset(String name, Pos curPos) throws AnalyzeError {
        var entry = getSymbol(name, curPos);
        return entry.getStackOffset();
    }

    /**
     * 获取变量是否是常量
     *
     * @param name   符号名
     * @param curPos 当前位置（报错用）
     * @return 是否为常量
     * @throws AnalyzeError
     */
    private boolean isConstant(String name, Pos curPos) throws AnalyzeError {
        var entry = getSymbol(name, curPos);
        return entry.isConstant();
    }

    @SuppressWarnings("unchecked")
    private void analyseProgram() throws CompileError {
        // 程序 -> 'begin' 主过程 'end'
        // 示例函数，示例如何调用子程序
        addScope();
        ArrayList<Instruction> instructions = new ArrayList<>();
        // TODO: 2020/12/4 添加标准库函数
        while (true) {
            switch (peek().getTokenType()) {
                case LET_KW:
                    analyse_let_decl_stmt(instructions);
                    break;
                case CONST_KW:
                    analyse_const_decl_stmt(instructions);
                    break;
                case FN_KW:
                    analyse_function();
                    break;
                default:
//                    throw new AnalyzeError(ErrorCode.InvalidIdentType, peek().getStartPos());
                    expect(TokenType.EOF);
//                    addFunctionLocalTable(this.listOfSymbolTable.get(0), new Pos(0, 0));
                    this.curFunctionSymbolTable.clear();
                    this.curFunctionSymbolTable.add(this.listOfSymbolTable.get(0));
                    addFunctionSymbol("_start", new ArrayList<>(), IdentType.VOID, instructions, (ArrayList<SymbolTable>) this.curFunctionSymbolTable.clone(), new Pos(0, 0));
                    return;
            }
        }
        // 不销毁全局符号表
//        removeScope();
    }

//    private void analyseItem(ArrayList<Instruction> instructions) throws CompileError {
//        switch (peek().getTokenType()) {
//            case LET_KW:
//                analyse_let_decl_stmt(instructions);
//                break;
//            case CONST_KW:
//                analyse_const_decl_stmt(instructions);
//                break;
//            case FN_KW:
//                analyse_function();
//                break;
//            default:
//                throw new AnalyzeError(ErrorCode.InvalidIdentType, peek().getStartPos());
//        }
//    }

    private void analyse_let_decl_stmt(ArrayList<Instruction> instructions) throws CompileError {
        expect(TokenType.LET_KW);
        var nameToken = expect(TokenType.IDENT);
        boolean initialized = false;
        expect(TokenType.COLON);
        var type = expect(TokenType.IDENT);
        IdentType identType;
        switch ((String) type.getValue()) {
            case "int":
                identType = IdentType.INT;
                break;
            case "double":
                identType = IdentType.DOUBLE;
                break;
            default:
                throw new AnalyzeError(ErrorCode.InvalidIdentType, type.getStartPos());
        }
        Instruction address;
        if (curIsGlobal()) {
            address = new Instruction(Operation.globa);
        } else {
            address = new Instruction(Operation.loca);
        }
        instructions.add(address);
        // 下个 token 是等于号吗？如果是的话分析初始化
        Token operator = nextIf(TokenType.ASSIGN);
        if (operator != null) {
            checkTypeMatch(identType, analyse_expr(instructions, TokenType.None), operator.getStartPos());
            initialized = true;
        }

        // 分号
        expect(TokenType.SEMICOLON);

        String name = (String) nameToken.getValue(); /* 名字 */
        var offset = addSymbol(name, initialized, false, /* 当前位置 */ nameToken.getStartPos(), identType);

        address.setValue(offset);
        instructions.add(new Instruction(Operation.store64));
    }

    private void analyse_const_decl_stmt(ArrayList<Instruction> instructions) throws CompileError {
        expect(TokenType.CONST_KW);
        var nameToken = expect(TokenType.IDENT);
        expect(TokenType.COLON);
        var type = expect(TokenType.IDENT);
        IdentType identType;
        switch ((String) type.getValue()) {
            case "int":
                identType = IdentType.INT;
                break;
            case "double":
                identType = IdentType.DOUBLE;
                break;
            default:
                throw new AnalyzeError(ErrorCode.InvalidIdentType, type.getStartPos());
        }
        Instruction address;
        if (curIsGlobal()) {
            address = new Instruction(Operation.globa);
        } else {
            address = new Instruction(Operation.loca);
        }
        instructions.add(address);
        // 下个 token 是等于号吗？如果是的话分析初始化
        Token operator = expect(TokenType.ASSIGN);
        checkTypeMatch(identType, analyse_expr(instructions, TokenType.None), operator.getStartPos());

        // 分号
        expect(TokenType.SEMICOLON);

        String name = (String) nameToken.getValue(); /* 名字 */
        var offset = addSymbol(name, true, true, /* 当前位置 */ nameToken.getStartPos(), identType);

        address.setValue(offset);
        instructions.add(new Instruction(Operation.store64));
    }

    @SuppressWarnings("unchecked")
    private void analyse_function() throws CompileError {
        addScope();
        this.curFunctionSymbolTable.clear();
        ArrayList<Instruction> instructions = new ArrayList<>();
        expect(TokenType.FN_KW);
        var nameToken = expect(TokenType.IDENT);
        expect(TokenType.L_PAREN);
        ArrayList<IdentType> function_param_list = analyse_function_param_list();
        expect(TokenType.R_PAREN);
        expect(TokenType.ARROW);
        var type = expect(TokenType.IDENT);
        IdentType identType;
        switch ((String) type.getValue()) {
            case "int":
                identType = IdentType.INT;
                break;
            case "double":
                identType = IdentType.DOUBLE;
                break;
            case "void":
                identType = IdentType.VOID;
                break;
            default:
                throw new AnalyzeError(ErrorCode.InvalidReturnValueType, type.getStartPos());
        }
        boolean hasReturned = analyse_block_stmt(instructions, (identType == IdentType.VOID));
        if (!hasReturned) {
            throw new AnalyzeError(ErrorCode.NoReturn, nameToken.getStartPos());
        }
        // 加入符号表
        String name = (String) nameToken.getValue();
        addFunctionSymbol(name, function_param_list, identType, instructions, (ArrayList<SymbolTable>) this.curFunctionSymbolTable.clone(), nameToken.getStartPos());

        removeScope();
    }

    private ArrayList<IdentType> analyse_function_param_list() throws CompileError {
        ArrayList<IdentType> function_param_list = new ArrayList<>();
        if (peek().getTokenType() == TokenType.IDENT || peek().getTokenType() == TokenType.CONST_KW) {
            analyse_function_param(function_param_list);
            while (nextIf(TokenType.COMMA) != null) {
                analyse_function_param(function_param_list);
            }
        }
        return function_param_list;
    }

    private void analyse_function_param(ArrayList<IdentType> function_param_list) throws CompileError {
        boolean isConstant = false;
        if (nextIf(TokenType.CONST_KW) != null) {
            isConstant = true;
        }
        var nameToken = expect(TokenType.IDENT);
        expect(TokenType.COLON);
        var type = expect(TokenType.IDENT);
        IdentType identType;
        switch ((String) type.getValue()) {
            case "int":
                identType = IdentType.INT;
                break;
            case "double":
                identType = IdentType.DOUBLE;
                break;
            default:
                throw new AnalyzeError(ErrorCode.InvalidIdentType, type.getStartPos());
        }
        // 加入符号表
        String name = (String) nameToken.getValue();
        addSymbol(name, true, isConstant, nameToken.getStartPos(), identType);
        function_param_list.add(identType);
    }

    private boolean analyse_block_stmt(ArrayList<Instruction> instructions, boolean hasReturned) throws CompileError {
        SymbolTable symbolTable = addScope();
        Token L_BRACE = expect(TokenType.L_BRACE);
        addFunctionLocalTable(symbolTable, L_BRACE.getStartPos());
        AnalyseStmtResult analyseStmtResult = new AnalyseStmtResult(true, hasReturned);
        while (analyseStmtResult.stmtFlag) {
            analyseStmtResult = analyse_stmt(instructions, analyseStmtResult.hasReturned);
        }
        expect(TokenType.R_BRACE);
        removeScope();
        return analyseStmtResult.hasReturned;
    }

    class AnalyseStmtResult {
        boolean stmtFlag;
        boolean hasReturned;

        public AnalyseStmtResult(boolean stmtFlag, boolean hasReturned) {
            this.stmtFlag = stmtFlag;
            this.hasReturned = hasReturned;
        }

        public AnalyseStmtResult(boolean hasReturned) {
            this.hasReturned = hasReturned;
        }

        public AnalyseStmtResult() {
        }
    }

    private AnalyseStmtResult analyse_stmt(ArrayList<Instruction> instructions, boolean hasReturned) throws CompileError {
        AnalyseStmtResult analyseStmtResult = new AnalyseStmtResult(true, hasReturned);
        switch (peek().getTokenType()) {
            case IDENT:
            case L_PAREN:
            case MINUS:
                analyse_expr_stmt(instructions);
                break;
            case LET_KW:
                analyse_let_decl_stmt(instructions);
                break;
            case CONST_KW:
                analyse_const_decl_stmt(instructions);
                break;
            case IF_KW:
                analyse_if_stmt(instructions, hasReturned);
//                if (!ifHasReturned) {
//                    throw new AnalyzeError(ErrorCode.NoReturn, peek().getStartPos());
//                }
                break;
            case WHILE_KW:
                this.whileStack.add(instructions.size());
                this.breakStack.add(new ArrayList<>());
                analyse_while_stmt(instructions, hasReturned);
//                if (!whileHasReturned) {
//                    throw new AnalyzeError(ErrorCode.NoReturn, peek().getStartPos());
//                }
                for (Instruction breakInstruction: this.breakStack.get(this.breakStack.size() -1)) {
                    breakInstruction.setValue(instructions.size() - breakInstruction.getIntValue() - 1);
                }
                this.breakStack.remove(this.breakStack.size() -1);
                this.whileStack.remove(this.whileStack.size() - 1);
                break;
            case BREAK_KW:
                analyse_break_stmt(instructions);
                break;
            case CONTINUE_KW:
                analyse_continue_stmt(instructions);
                break;
            case RETURN_KW:
                analyse_return_stmt(instructions);
                analyseStmtResult.hasReturned = true;
                break;
            case L_BRACE:
                analyseStmtResult.hasReturned = analyse_block_stmt(instructions, hasReturned);
                break;
            case SEMICOLON:
                analyse_empty_stmt();
                break;
            default:
                analyseStmtResult.stmtFlag = false;
                break;
        }
        return analyseStmtResult;
    }

    private void analyse_expr_stmt(ArrayList<Instruction> instructions) throws CompileError {
        // TODO: 2020/11/18 表达式如果有值，值将会被丢弃
        analyse_expr(instructions, TokenType.None);
        expect(TokenType.SEMICOLON);
    }

    private void analyse_if_stmt(ArrayList<Instruction> instructions, boolean hasReturned) throws CompileError {
        Token if_kw = expect(TokenType.IF_KW);
        boolean ifHasReturned = getJumpInstruction(instructions, if_kw, hasReturned);
        if (!ifHasReturned) {
            throw new AnalyzeError(ErrorCode.NoReturn, if_kw.getStartPos());
        }
        while (check(TokenType.ELSE_KW)) {
            Token ELSE_KW = expect(TokenType.ELSE_KW);
            boolean elseHasReturn;
            if (peek().getTokenType() == TokenType.IF_KW) {
                elseHasReturn = getJumpInstruction(instructions, if_kw, hasReturned);
                if (!elseHasReturn) {
                    throw new AnalyzeError(ErrorCode.NoReturn, ELSE_KW.getStartPos());
                }
            } else {
                elseHasReturn = analyse_block_stmt(instructions, hasReturned);
//                if (!elseHasReturn) {
//                    throw new AnalyzeError(ErrorCode.NoReturn, ELSE_KW.getStartPos());
//                }
                break;
            }
        }
    }

    private boolean getJumpInstruction(ArrayList<Instruction> instructions, Token if_kw, boolean hasReturn) throws CompileError {
        IdentType exprRet = analyse_expr(instructions, TokenType.None);
        Instruction jump;
        switch (exprRet) {
            case TRUE:
                jump = new Instruction(Operation.br_false);
                break;
            case FALSE:
                jump = new Instruction(Operation.br_true);
                break;
            default:
                throw new AnalyzeError(ErrorCode.InvalidIfExpr, if_kw.getStartPos());
        }
        instructions.add(jump);
        int jumpLength = instructions.size();
        boolean jumpHasReturned = analyse_block_stmt(instructions, hasReturn);
        jumpLength = instructions.size() - jumpLength - 1;
        jump.setValue(jumpLength);
        return jumpHasReturned;
    }

    private boolean analyse_while_stmt(ArrayList<Instruction> instructions, boolean hasReturned) throws CompileError {
        Token while_kw = expect(TokenType.WHILE_KW);
        int jumpBackwardLength = instructions.size();
        boolean jumpHasReturned = getJumpInstruction(instructions, while_kw, hasReturned);
        jumpBackwardLength = - (instructions.size() - jumpBackwardLength + 2);
        instructions.add(new Instruction(Operation.br, jumpBackwardLength));
        return jumpHasReturned;
    }

    private void analyse_break_stmt(ArrayList<Instruction> instructions) throws CompileError {
        Token break_kw = expect(TokenType.BREAK_KW);
        if (this.whileStack.size() == 0) {
            throw new AnalyzeError(ErrorCode.BreakError, break_kw.getStartPos());
        }
        Instruction breakInstruction = new Instruction(Operation.br);
        instructions.add(breakInstruction);
        this.breakStack.get(this.breakStack.size() - 1).add(breakInstruction);
        expect(TokenType.SEMICOLON);
    }

    private void analyse_continue_stmt(ArrayList<Instruction> instructions) throws CompileError {
        Token continue_kw = expect(TokenType.CONTINUE_KW);
        if (this.whileStack.size() == 0) {
            throw new AnalyzeError(ErrorCode.ContinueError, continue_kw.getStartPos());
        }
        instructions.add(new Instruction(Operation.br, - (instructions.size() - this.whileStack.get(this.whileStack.size() - 1) + 2)));
        expect(TokenType.SEMICOLON);
    }

    // TODO: return
    private void analyse_return_stmt(ArrayList<Instruction> instructions) throws CompileError {
        expect(TokenType.RETURN_KW);
        if (peek().getTokenType() == TokenType.IDENT || peek().getTokenType() == TokenType.L_PAREN || peek().getTokenType() == TokenType.MINUS || peek().getTokenType() == TokenType.UINT_LITERAL || peek().getTokenType() == TokenType.DOUBLE_LITERAL || peek().getTokenType() == TokenType.STRING_LITERAL || peek().getTokenType() == TokenType.CHAR_LITERAL) {
            analyse_expr(instructions, TokenType.None);
        }
        instructions.add(new Instruction(Operation.ret));
        expect(TokenType.SEMICOLON);
    }

    private void analyse_empty_stmt() throws CompileError {
        expect(TokenType.SEMICOLON);
    }

    private IdentType analyse_expr(ArrayList<Instruction> instructions, TokenType stackTop) throws CompileError {
        SymbolEntry symbolEntry;
        IdentType exprRet;  // 下一层递归的返回值
        IdentType result = IdentType.VOID;
        switch (peek().getTokenType()) {
            case IDENT:
                var nameToken = expect(TokenType.IDENT);
                switch (peek().getTokenType()) {
                    case ASSIGN:    // assign_expr
                        if (operatorPriority.get(TokenType.ASSIGN) <= operatorPriority.get(stackTop)) {
                            return result;
                        }
                        symbolEntry = getSymbol((String) nameToken.getValue(), nameToken.getStartPos());
                        if (symbolEntry.isConstant()) {
                            throw new AnalyzeError(ErrorCode.AssignToConstant, nameToken.getStartPos());
                        }
                        instructions.add(new Instruction(symbolEntry.getOperationByLocation(), symbolEntry.getStackOffset()));  // 压地址
                        Token assign = expect(TokenType.ASSIGN);
                        exprRet = analyse_expr(instructions, TokenType.ASSIGN);   // 压值
                        checkTypeMatch(symbolEntry.getIdentType(), exprRet, assign.getStartPos());
                        instructions.add(new Instruction(Operation.store64));
                        result = IdentType.VOID;
                        break;
                    case L_PAREN:   // call_expr
                        FunctionEntry functionEntry = getFunctionSymbol((String) nameToken.getValue(), nameToken.getStartPos());
                        expect(TokenType.L_PAREN);
                        instructions.add(new Instruction(Operation.push));  // 压返回值
                        analyse_call_param_list(functionEntry.getFunction_param_list(), instructions, nameToken.getStartPos()); // 压参数
                        expect(TokenType.R_PAREN);
                        instructions.add(new Instruction(Operation.call, functionEntry.getStackOffset()));
                        result = functionEntry.getReturnValueType();
                        break;
                    default:    // ident_expr
                        symbolEntry = getSymbol((String) nameToken.getValue(), nameToken.getStartPos());
                        instructions.add(new Instruction(symbolEntry.getOperationByLocation(), symbolEntry.getStackOffset()));
                        instructions.add(new Instruction(Operation.load64));
                        result = symbolEntry.getIdentType();
                        break;
                }
                break;
            case L_PAREN:   // group_expr
                expect(TokenType.L_PAREN);
                result = analyse_expr(instructions, TokenType.None);
                expect(TokenType.R_PAREN);
                break;
            case MINUS:     // negate_expr
                if (operatorPriority.get(TokenType.PRE_MINUS) <= operatorPriority.get(stackTop)) {
                    return result;
                }
                Token minus = expect(TokenType.MINUS);
                result = analyse_expr(instructions, TokenType.PRE_MINUS);
                Operation negative;
                if (result == IdentType.INT) {
                    negative = Operation.neg_i;
                } else if (result == IdentType.DOUBLE) {
                    negative = Operation.neg_f;
                } else {
                    throw new AnalyzeError(ErrorCode.TypeMisMatch, minus.getStartPos());
                }
                instructions.add(new Instruction(negative));
                break;
            case UINT_LITERAL:  // literal_expr
                Token uint = expect(TokenType.UINT_LITERAL);
                int intValue = (int)uint.getValue();
                instructions.add(new Instruction(Operation.push, intValue));
                result = IdentType.INT;
                break;
            case DOUBLE_LITERAL:
                Token DOUBLE_LITERAL = expect(TokenType.DOUBLE_LITERAL);
                double doubleValue = (double)DOUBLE_LITERAL.getValue();
                instructions.add(new Instruction(Operation.push, doubleValue));
                result = IdentType.DOUBLE;
                break;
            // TODO: 2020/11/18 字符串字面量 只会在 putstr 调用中出现，语义是对应的全局常量的编号
            case STRING_LITERAL:
                Token STRING_LITERAL = expect(TokenType.STRING_LITERAL);
                var offset = addSymbol(String.valueOf(this.globalStringIndex++), true, true, /* 当前位置 */ STRING_LITERAL.getStartPos(), IdentType.STRING_LITERAL);
                instructions.add(new Instruction(Operation.globa, offset));
                instructions.add(new Instruction(Operation.push, (String)STRING_LITERAL.getValue()));
                instructions.add(new Instruction(Operation.store64));
                result = IdentType.STRING_LITERAL;
                break;
            case CHAR_LITERAL:
                Token CHAR_LITERAL = expect(TokenType.CHAR_LITERAL);
                // TODO: 2020/12/5 转换是否有问题
                int charValue = (int) (char) CHAR_LITERAL.getValue();
                instructions.add(new Instruction(Operation.push, charValue));
                result = IdentType.INT;
                break;
            default:
                throw new AnalyzeError(ErrorCode.InvalidExpr, peek().getStartPos());
        }
        Set<TokenType> binary_operator = new HashSet<>();
        binary_operator.add(TokenType.PLUS);
        binary_operator.add(TokenType.MINUS);
        binary_operator.add(TokenType.MUL);
        binary_operator.add(TokenType.DIV);
        binary_operator.add(TokenType.EQ);
        binary_operator.add(TokenType.NEQ);
        binary_operator.add(TokenType.LT);
        binary_operator.add(TokenType.GT);
        binary_operator.add(TokenType.LE);
        binary_operator.add(TokenType.GE);
        while (true) {
            if (binary_operator.contains(peek().getTokenType())) {  // binary_operator
                if (operatorPriority.get(peek().getTokenType()) <= operatorPriority.get(stackTop)) {
                    return result;
                }
                var operator = next();
                exprRet = analyse_expr(instructions, operator.getTokenType());
                checkTypeMatch(result, exprRet, operator.getStartPos());
                switch (operator.getTokenType()) {
                    case PLUS:
                    case MINUS:
                    case MUL:
                    case DIV:
                        for (Operation operation: binaryOperation.get(operator.getTokenType()).get(result)) {
                            instructions.add(new Instruction(operation));
                        }
                        break;
                    case EQ:
                    case GE:
                    case LE:
                        for (Operation operation: binaryOperation.get(operator.getTokenType()).get(result)) {
                            instructions.add(new Instruction(operation));
                        }
                        result = IdentType.FALSE;
                        break;
                    case NEQ:
                    case LT:
                    case GT:
                        for (Operation operation: binaryOperation.get(operator.getTokenType()).get(result)) {
                            instructions.add(new Instruction(operation));
                        }
                        result = IdentType.TRUE;
                        break;
                    default:
                        // not reach
                        throw new AnalyzeError(ErrorCode.InvalidBinaryOperator, operator.getStartPos());
                }
            } else if (peek().getTokenType() == TokenType.AS_KW) {  // as_expr
                if (operatorPriority.get(peek().getTokenType()) <= operatorPriority.get(stackTop)) {
                    return result;
                }
                var as = expect(TokenType.AS_KW);
                var type = expect(TokenType.IDENT);
                // TODO: 2020/11/18 强制类型转换
                switch ((String) type.getValue()) {
                    case "int":
                        if (result == IdentType.INT) {
                            break;
                        } else if (result == IdentType.DOUBLE) {
                            instructions.add(new Instruction(Operation.ftoi));
                            result = IdentType.INT;
                        } else {
                            throw new AnalyzeError(ErrorCode.InvalidAsExpr, as.getStartPos());
                        }
                        break;
                    case "double":
                        if (result == IdentType.INT) {
                            instructions.add(new Instruction(Operation.itof));
                            result = IdentType.DOUBLE;
                        } else if (result == IdentType.DOUBLE) {
                            break;
                        } else {
                            throw new AnalyzeError(ErrorCode.InvalidAsExpr, as.getStartPos());
                        }
                        break;
                    default:
                        throw new AnalyzeError(ErrorCode.InvalidIdentType, type.getStartPos());
                }
            } else {
                break;
            }
        }
        return result;
    }



    // TODO: 2020/11/18
    private void analyse_call_param_list(ArrayList<IdentType> function_param_list, ArrayList<Instruction> instructions, Pos curPos) throws CompileError {
        // TODO: 2020/11/18 判断与函数定义参数类型数量是否一致
        ArrayList<TokenType> prefixOfExpr = new ArrayList<>();
        prefixOfExpr.add(TokenType.IDENT);
        prefixOfExpr.add(TokenType.L_PAREN);
        prefixOfExpr.add(TokenType.MINUS);
        prefixOfExpr.add(TokenType.UINT_LITERAL);
        prefixOfExpr.add(TokenType.DOUBLE_LITERAL);
        prefixOfExpr.add(TokenType.STRING_LITERAL);
        prefixOfExpr.add(TokenType.CHAR_LITERAL);
        int paramIndex = 0;
        int lengthOfParamList = function_param_list.size();
        if (prefixOfExpr.contains(peek().getTokenType())) {
            paramIndex = analyse_call_param(function_param_list, instructions, curPos, paramIndex, lengthOfParamList);
        }
        while (nextIf(TokenType.COMMA) != null) {
            paramIndex = analyse_call_param(function_param_list, instructions, curPos, paramIndex, lengthOfParamList);
        }
        if (paramIndex < lengthOfParamList) {
            throw new AnalyzeError(ErrorCode.TooShortParamList, curPos);
        }
    }

    private int analyse_call_param(ArrayList<IdentType> function_param_list, ArrayList<Instruction> instructions, Pos curPos, int paramIndex, int lengthOfParamList) throws CompileError {
        IdentType exprRet = analyse_expr(instructions, TokenType.None);
        if (paramIndex >= lengthOfParamList) {
            throw new AnalyzeError(ErrorCode.TooLongParamList, curPos);
        }
        if (function_param_list.get(paramIndex) != exprRet) {
            throw new AnalyzeError(ErrorCode.TypeMisMatch, curPos);
        }
        paramIndex++;
        return paramIndex;
    }

//    private void analyseStatementSequence() throws CompileError {
//        // 语句序列 -> 语句*
//        // 语句 -> 赋值语句 | 输出语句 | 空语句
//
//        while (true) {
//            // 如果下一个 token 是……
//            var peeked = peek();
//            if (peeked.getTokenType() == TokenType.IDENT) {
//                // 调用相应的分析函数
//                // 如果遇到其他非终结符的 FIRST 集呢？
//                analyseAssignmentStatement();
//            } else if (peeked.getTokenType() == TokenType.Print) {
//                analyseOutputStatement();
//            } else if (peeked.getTokenType() == TokenType.SEMICOLON) {
//                expect(TokenType.SEMICOLON);
//            } else {
//                // 都不是，摸了
//                break;
//            }
//        }
//    }
//
//    private int analyseConstantExpression() throws CompileError {
//        // 常表达式 -> 符号? 无符号整数
//        boolean negative = false;
//        if (nextIf(TokenType.PLUS) != null) {
//            negative = false;
//        } else if (nextIf(TokenType.MINUS) != null) {
//            negative = true;
//        }
//
//        var token = expect(TokenType.UINT_LITERAL);
//
//        int value = (int) token.getValue();
//        if (negative) {
//            value = -value;
//        }
//
//        return value;
//    }
//
//    private void analyseExpression() throws CompileError {
//        // 表达式 -> 项 (加法运算符 项)*
//        // 项
//        analyseItem();
//
//        while (true) {
//            // 预读可能是运算符的 token
//            var op = peek();
//            if (op.getTokenType() != TokenType.PLUS && op.getTokenType() != TokenType.MINUS) {
//                break;
//            }
//
//            // 运算符
//            next();
//
//            // 项
//            analyseItem();
//
//            // 生成代码
//            if (op.getTokenType() == TokenType.PLUS) {
//                instructions.add(new Instruction(Operation.ADD));
//            } else if (op.getTokenType() == TokenType.MINUS) {
//                instructions.add(new Instruction(Operation.SUB));
//            }
//        }
//    }
//
//    private void analyseAssignmentStatement() throws CompileError {
//        // 赋值语句 -> 标识符 '=' 表达式 ';'
//
//        // 分析这个语句
//
//        // 标识符是什么？
//        var nameToken = expect(TokenType.IDENT);
//        expect(TokenType.ASSIGN);
//        analyseExpression();
//        expect(TokenType.SEMICOLON);
//        // 标识符是什么？
//
//        String name = (String) nameToken.getValue();
//        var symbol = symbolTable.get(name);
//        if (symbol == null) {
//            // 没有这个标识符
//            throw new AnalyzeError(ErrorCode.NotDeclared, /* 当前位置 */ nameToken.getStartPos());
//        } else if (symbol.isConstant) {
//            // 标识符是常量
//            throw new AnalyzeError(ErrorCode.AssignToConstant, /* 当前位置 */ nameToken.getStartPos());
//        }
//        // 设置符号已初始化
//        initializeSymbol(name, nameToken.getStartPos());
//
//        // 把结果保存
//        var offset = getOffset(name, nameToken.getStartPos());
//        instructions.add(new Instruction(Operation.STO, offset));
//    }
//
//    private void analyseOutputStatement() throws CompileError {
//        // 输出语句 -> 'print' '(' 表达式 ')' ';'
//
//        expect(TokenType.Print);
//        expect(TokenType.L_PAREN);
//
//        analyseExpression();
//
//        expect(TokenType.R_PAREN);
//        expect(TokenType.SEMICOLON);
//
//        instructions.add(new Instruction(Operation.WRT));
//    }
//
//    private void analyseItem() throws CompileError {
//        // 项 -> 因子 (乘法运算符 因子)*
//
//        // 因子
//        analyseFactor();
//
//        // 预读可能是运算符的 token
//        while (true) {
//            Token op = peek();
//            if (op.getTokenType() != TokenType.MUL && op.getTokenType() != TokenType.DIV) {
//                break;
//            }
//
//            // 运算符
//            next();
//
//            // 因子
//            analyseFactor();
//
//            // 生成代码
//            if (op.getTokenType() == TokenType.MUL) {
//                instructions.add(new Instruction(Operation.MUL));
//            } else if (op.getTokenType() == TokenType.DIV) {
//                instructions.add(new Instruction(Operation.DIV));
//            }
//        }
//    }
//
//    private void analyseFactor() throws CompileError {
//        // 因子 -> 符号? (标识符 | 无符号整数 | '(' 表达式 ')')
//
//        boolean negate;
//        if (nextIf(TokenType.MINUS) != null) {
//            negate = true;
//            // 计算结果需要被 0 减
//            instructions.add(new Instruction(Operation.LIT, 0));
//        } else {
//            nextIf(TokenType.PLUS);
//            negate = false;
//        }
//
//        if (check(TokenType.IDENT)) {
//            // 是标识符
//
//            // 加载标识符的值
//            var nameToken = expect(TokenType.IDENT);
//            String name = (String) nameToken.getValue();/* 快填 */
//            var symbol = symbolTable.get(name);
//            if (symbol == null) {
//                // 没有这个标识符
//                throw new AnalyzeError(ErrorCode.NotDeclared, /* 当前位置 */ nameToken.getStartPos());
//            } else if (!symbol.isInitialized) {
//                // 标识符没初始化
//                throw new AnalyzeError(ErrorCode.NotInitialized, /* 当前位置 */ nameToken.getStartPos());
//            }
//            var offset = getOffset(name, nameToken.getStartPos());
//            instructions.add(new Instruction(Operation.LOD, offset));
//        } else if (check(TokenType.UINT_LITERAL)) {
//            // 是整数
//            // 加载整数值
//            var token = expect(TokenType.UINT_LITERAL);
//            int value = (int) token.getValue();
//            instructions.add(new Instruction(Operation.LIT, value));
//        } else if (check(TokenType.L_PAREN)) {
//            // 是表达式
//            expect(TokenType.L_PAREN);
//            // 调用相应的处理函数
//            analyseExpression();
//            expect(TokenType.R_PAREN);
//        } else {
//            // 都不是，摸了
//            throw new ExpectedTokenError(List.of(TokenType.IDENT, TokenType.UINT_LITERAL, TokenType.L_PAREN), next());
//        }
//
//        if (negate) {
//            instructions.add(new Instruction(Operation.SUB));
//        }
//    }
}
