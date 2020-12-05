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
import miniplc0java.Numeral.Numeral;

import java.util.*;

public final class Analyser {

    Tokenizer tokenizer;


    /** 当前偷看的 token */
    Token peekedToken = null;

    /** 符号表 */
    ArrayList<SymbolTable> listOfSymbolTable = new ArrayList<>();
    HashMap<String, FunctionEntry> functionSymbolTable = new HashMap<>();

    /** 下一个变量的栈偏移 */
    int nextOffset = 0;

//    int[][] OPGMatrix = new int[12][12];
    HashMap<TokenType, Integer> operatorPriority = new HashMap<>();

//     = {'+': 0, '*': 1, '(': 2, ')': 3, 'i': 4, '#': 5}

    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        buildOPGMatrix();
    }

    private void buildOPGMatrix() {
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
//        OPGMatrix[operatorPriority.get(TokenType.PLUS)][operatorPriority.get(TokenType.PLUS)] = 1;  // >
    }

    public List<Instruction> analyse() throws CompileError {
        analyseProgram();
        // TODO: 2020/12/4 analyse时要返回：
        //1. 全局符号表
        //2. 函数表（函数的所有属性和指令集）
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
    private int addSymbol(String name, boolean isInitialized, boolean isConstant, Pos curPos, IdentType identType, Numeral numeral) throws AnalyzeError {
        SymbolTable symbolTable = this.listOfSymbolTable.get(this.listOfSymbolTable.size() - 1);
        if (symbolTable.get(name) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        } else {
            return symbolTable.put(name, isInitialized, isConstant, identType, numeral);
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
        throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
    }

    private boolean curIsGlobal() {
        return this.listOfSymbolTable.size() == 1;
    }

    private void addFunctionSymbol(String name, ArrayList<IdentType> function_param_list, IdentType returnValueType, ArrayList<Instruction> instructions, Pos curPos) throws AnalyzeError {
        SymbolTable globalSymbolTable = this.listOfSymbolTable.get(0);
        if (globalSymbolTable.get(name) != null || functionSymbolTable.get(name) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        } else {
            this.functionSymbolTable.put(name, new FunctionEntry(function_param_list, returnValueType, instructions, getNextVariableOffset()));
        }
    }

    private FunctionEntry getFunctionSymbol(String name, Pos curPos) throws AnalyzeError {
        FunctionEntry functionEntry = functionSymbolTable.get(name);
        if (functionEntry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return functionEntry;
        }
    }

    private void addScope() {
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
    }

    private void removeScope() {
        this.listOfSymbolTable.remove(this.listOfSymbolTable.size() - 1);
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

    private void analyseProgram() throws CompileError {
        // 程序 -> 'begin' 主过程 'end'
        // 示例函数，示例如何调用子程序
        addScope();
        ArrayList<Instruction> instructions = new ArrayList<>();
        // TODO: 2020/12/4 添加标准库函数
        addFunctionSymbol("_start", new ArrayList<>(), IdentType.VOID, instructions, new Pos(0, 0));
        switch (peek().getTokenType()) {
            case LET_KW:
                analyse_let_decl_stmt(instructions);
                break;
            case CONST_KW:
                analyse_const_decl_stmt();
                break;
            case FN_KW:
                analyse_function();
                break;
            default:
                throw new AnalyzeError(ErrorCode.InvalidIdentType, peek().getStartPos());
        }
        expect(TokenType.EOF);
        removeScope();
    }

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
        // 下个 token 是等于号吗？如果是的话分析初始化
        var value = new Numeral(identType, 0.0);
        Token operator = nextIf(TokenType.ASSIGN);
        if (operator != null) {
            value.calculate(analyse_expr(), operator.getStartPos());
            initialized = true;
        }

        // 分号
        expect(TokenType.SEMICOLON);

        String name = (String) nameToken.getValue(); /* 名字 */
        var offset = addSymbol(name, initialized, false, /* 当前位置 */ nameToken.getStartPos(), identType, value);

        if (curIsGlobal()) {
            instructions.add(new Instruction(Operation.globa, new Numeral(IdentType.INT, offset)));
        } else {
            instructions.add(new Instruction(Operation.loca, new Numeral(IdentType.INT, offset)));
        }
        instructions.add(new Instruction(Operation.push, value));
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

        // TODO: 常表达式，常量只能被读取，不能被修改
        var value = new Numeral(identType, 0.0);
        Token operator = expect(TokenType.ASSIGN);
        value.calculate(analyse_expr(), operator.getStartPos());
        // 分号
        expect(TokenType.SEMICOLON);

        // 加入符号表
        String name = (String) nameToken.getValue();
        var offset = addSymbol(name, true, true, nameToken.getStartPos(), identType, value);

        // TODO: 这里把常量值直接放进栈里，位置和符号表记录的一样。
        // 更高级的程序还可以把常量的值记录下来，遇到相应的变量直接替换成这个常数值，
        // 我们这里就先不这么干了。
        if (curIsGlobal()) {
            instructions.add(new Instruction(Operation.globa, new Numeral(IdentType.INT, offset)));
        } else {
            instructions.add(new Instruction(Operation.loca, new Numeral(IdentType.INT, offset)));
        }
        instructions.add(new Instruction(Operation.push, value));
        instructions.add(new Instruction(Operation.store64));
    }

    private void analyse_function() throws CompileError {
        addScope();
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
        analyse_block_stmt(instructions);

        // 加入符号表
        String name = (String) nameToken.getValue();
        addFunctionSymbol(name, function_param_list, identType, instructions, nameToken.getStartPos());

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
        addSymbol(name, true, isConstant, nameToken.getStartPos(), identType, new Numeral(identType, 0.0));
        function_param_list.add(identType);
    }

    private void analyse_block_stmt(ArrayList<Instruction> instructions) throws CompileError {
        addScope();
        expect(TokenType.L_BRACE);
        boolean stmtFlag = true;
        while (stmtFlag) {
            stmtFlag = analyse_stmt(instructions);
        }
        expect(TokenType.R_BRACE);
        removeScope();
    }

    private boolean analyse_stmt(ArrayList<Instruction> instructions) throws CompileError {
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
                analyse_if_stmt();
                break;
            case WHILE_KW:
                analyse_while_stmt();
                break;
            case BREAK_KW:
                analyse_break_stmt();
                break;
            case CONTINUE_KW:
                analyse_continue_stmt();
                break;
            case RETURN_KW:
                analyse_return_stmt();
                break;
            case L_BRACE:
                analyse_block_stmt(instructions);
                break;
            case SEMICOLON:
                analyse_empty_stmt();
                break;
            default:
                return false;
        }
        return true;
    }

    // TODO: 最终值
    private void analyse_expr_stmt(ArrayList<Instruction> instructions) throws CompileError {
        // TODO: 2020/11/18 表达式如果有值，值将会被丢弃
        analyse_expr(instructions);
        expect(TokenType.SEMICOLON);
    }

    // TODO: if
    // 比较运算符的运行结果是布尔类型。在 c0 中，我们并没有规定布尔类型的实际表示方式。在 navm 虚拟机中，所有非 0 的布尔值都被视为 true，而 0 被视为 false。
    private void analyse_if_stmt() throws CompileError {
        expect(TokenType.IF_KW);
        analyse_expr();
        analyse_block_stmt();
        while (nextIf(TokenType.ELSE_KW) != null) {
            if (peek().getTokenType() == TokenType.IF_KW) {
                analyse_expr();
                analyse_block_stmt();
            } else {
                analyse_block_stmt();
                break;
            }
        }
    }

    // TODO: while
    // 比较运算符的运行结果是布尔类型。在 c0 中，我们并没有规定布尔类型的实际表示方式。在 navm 虚拟机中，所有非 0 的布尔值都被视为 true，而 0 被视为 false。
    private void analyse_while_stmt() throws CompileError {
        expect(TokenType.WHILE_KW);
        analyse_expr();
        analyse_block_stmt();
    }

    // TODO: break
    private void analyse_break_stmt() throws CompileError {
        expect(TokenType.BREAK_KW);
        expect(TokenType.SEMICOLON);
    }

    // TODO: continue
    private void analyse_continue_stmt() throws CompileError {
        expect(TokenType.CONTINUE_KW);
        expect(TokenType.SEMICOLON);
    }

    // TODO: return
    private void analyse_return_stmt() throws CompileError {
        expect(TokenType.RETURN_KW);
        if (peek().getTokenType() == TokenType.IDENT || peek().getTokenType() == TokenType.L_PAREN || peek().getTokenType() == TokenType.MINUS) {
            analyse_expr();
        }
        expect(TokenType.SEMICOLON);
    }

    private void analyse_empty_stmt() throws CompileError {
        expect(TokenType.SEMICOLON);
    }

    // TODO: 2020/11/18 提示：对于 运算符表达式 operator_expr、取反表达式 negate_expr 和类型转换表达式 as_expr 可以使用局部的算符优先文法进行分析
    private Numeral analyse_expr(ArrayList<Instruction> instructions) throws CompileError {
        SymbolEntry symbolEntry;
        Numeral result;
        switch (peek().getTokenType()) {
            case IDENT:
                var nameToken = expect(TokenType.IDENT);
                switch (peek().getTokenType()) {
                    case ASSIGN:    // assign_expr
                        symbolEntry = getSymbol((String) nameToken.getValue(), nameToken.getStartPos());
                        if (symbolEntry.isConstant()) {
                            throw new AnalyzeError(ErrorCode.AssignToConstant, nameToken.getStartPos());
                        }
                        Token assign = expect(TokenType.ASSIGN);
                        numeral = analyse_expr(instructions);
                        symbolEntry.getNumeral().calculate(numeral, assign.getStartPos());
                        instructions.add(new Instruction(symbolEntry.getOperationByLocation(), new Numeral(IdentType.INT, symbolEntry.getStackOffset())));
                        instructions.add(new Instruction(Operation.push, numeral));
                        instructions.add(new Instruction(Operation.store64));
                        result = new Numeral(IdentType.VOID, 0.0);
                        break;
                    case L_PAREN:   // call_expr
                        FunctionEntry functionEntry = getFunctionSymbol((String) nameToken.getValue(), nameToken.getStartPos());
                        expect(TokenType.L_PAREN);
                        instructions.add(new Instruction(Operation.push, new Numeral(IdentType.INT, 0.0)));
                        analyse_call_param_list(functionEntry.getFunction_param_list(), instructions, nameToken.getStartPos());
                        expect(TokenType.R_PAREN);
                        instructions.add(new Instruction(Operation.call, new Numeral(IdentType.INT, functionEntry.getStackOffset())));
                        result = new Numeral(functionEntry.getReturnValueType(), 0.0);
                        break;
                    default:    // ident_expr
                        symbolEntry = getSymbol((String) nameToken.getValue(), nameToken.getStartPos());
                        instructions.add(new Instruction(symbolEntry.getOperationByLocation(), new Numeral(IdentType.INT, symbolEntry.getStackOffset())));
                        instructions.add(new Instruction(Operation.load64));
                        return symbolEntry.getNumeral();
                }
                break;
            case L_PAREN:   // group_expr
                expect(TokenType.L_PAREN);
                numeral = analyse_expr(instructions);
                expect(TokenType.R_PAREN);
                return numeral;
            case MINUS:     // negate_expr
                Token minus = expect(TokenType.MINUS);
                numeral = analyse_expr(instructions);
                numeral.reverse(minus.getStartPos());
                Operation negative;
                if (numeral.getIdentType() == IdentType.INT) {
                    negative = Operation.negi;
                } else if (numeral.getIdentType() == IdentType.DOUBLE) {
                    negative = Operation.negf;
                } else {
                    throw new AnalyzeError(ErrorCode.TypeMismatch, minus.getStartPos());
                }
                instructions.add(new Instruction(negative));
                return numeral;
            // TODO: 2020/11/18 字符串字面量 只会在 putstr 调用中出现，语义是对应的全局常量的编号
            case UINT_LITERAL:  // literal_expr
                Token uint = expect(TokenType.UINT_LITERAL);
                numeral = new Numeral(IdentType.INT, (int)uint.getValue());
                instructions.add(new Instruction(Operation.push, numeral));
                // TODO: 2020/12/5 把int赋给double了
                return numeral;
            case DOUBLE_LITERAL:
                Token DOUBLE_LITERAL = expect(TokenType.DOUBLE_LITERAL);
                numeral = new Numeral(IdentType.DOUBLE, (double)DOUBLE_LITERAL.getValue());
                instructions.add(new Instruction(Operation.push, numeral));
                return numeral;
            case STRING_LITERAL:
            case CHAR_LITERAL:
                // TODO: 2020/11/18 返回值
            default:
                throw new AnalyzeError(ErrorCode.InvalidExpr, peek().getStartPos());
        }
        // TODO: 2020/11/18 可能要用哈希表存操作符字符串
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
                // TODO: 2020/11/18 识别操作符
                // TODO: 2020/11/18 每个运算符的两侧必须是相同类型的数据
                var operator = next();
                analyse_expr();
            } else if (peek().getTokenType() == TokenType.AS_KW) {  // as_expr
                var type = expect(TokenType.IDENT);
                // TODO: 2020/11/18 强制类型转换
                switch ((String) type.getValue()) {
                    case "int":

                        break;
                    case "double":

                        break;
                    default:
                        throw new AnalyzeError(ErrorCode.InvalidIdentType, type.getStartPos());
                }
            } else {
                break;
            }
        }
        // TODO: 2020/12/2 返回值
        return numeral;
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
        Numeral numeral = analyse_expr(instructions);
        if (paramIndex >= lengthOfParamList) {
            throw new AnalyzeError(ErrorCode.TooLongParamList, curPos);
        }
        if (function_param_list.get(paramIndex) != numeral.getIdentType()) {
            throw new AnalyzeError(ErrorCode.TypeMismatch, curPos);
        }
        instructions.add(new Instruction(Operation.push, numeral));
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
