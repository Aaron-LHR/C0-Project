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
    ArrayList<Instruction> instructions;

    /** 当前偷看的 token */
    Token peekedToken = null;

    /** 符号表 */
    ArrayList<HashMap<String, SymbolEntry>> listOfSymbolTable = new ArrayList<>();
    HashMap<String, FunctionEntry> functionSymbolTable = new HashMap<>();

    /** 下一个变量的栈偏移 */
    int nextOffset = 0;

    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.instructions = new ArrayList<>();
    }

    public List<Instruction> analyse() throws CompileError {
        analyseProgram();
        return instructions;
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
    private void addSymbol(String name, boolean isInitialized, boolean isConstant, Pos curPos, IdentType identType) throws AnalyzeError {
        HashMap<String, SymbolEntry> symbolTable = this.listOfSymbolTable.get(this.listOfSymbolTable.size() - 1);
        if (symbolTable.get(name) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        } else {
            symbolTable.put(name, new SymbolEntry(isConstant, isInitialized, getNextVariableOffset(), identType));
        }
    }

    private SymbolEntry getSymbol(String name, Pos curPos) throws AnalyzeError {
        for (int index = this.listOfSymbolTable.size() - 1; index >= 0; index--) {
            HashMap<String, SymbolEntry> symbolTable = this.listOfSymbolTable.get(index);
            var entry = symbolTable.get(name);
            if (entry != null) {
                return entry;
            }
        }
        throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
    }

    private void addFunctionSymbol(String name, ArrayList<IdentType> function_param_list, IdentType returnValueType, Pos curPos) throws AnalyzeError {
        HashMap<String, SymbolEntry> symbolTable = this.listOfSymbolTable.get(0);
        if (symbolTable.get(name) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        } else {
            this.functionSymbolTable.put(name, new FunctionEntry(function_param_list, returnValueType, getNextVariableOffset()));
        }
    }

    private void addScope() {
        HashMap<String, SymbolEntry> symbolTable = new HashMap<>();
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
        switch (peek().getTokenType()) {
            case LET_KW:
                analyse_let_decl_stmt();
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

    private void analyse_let_decl_stmt() throws CompileError {
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
        if (nextIf(TokenType.ASSIGN) != null) {
            // TODO: 分析初始化的表达式，如果存在初始化表达式，其类型应当与变量声明时的类型相同。
            analyseExpression();
            initialized = true;
        }

        // 分号
        expect(TokenType.SEMICOLON);

        String name = (String) nameToken.getValue(); /* 名字 */
        addSymbol(name, initialized, false, /* 当前位置 */ nameToken.getStartPos(), identType);

        // TODO: 如果没有初始化的话在栈里推入一个初始值，没有初始化的变量的值未定义。我们不规定对于使用未初始化变量的行为的处理方式，你可以选择忽略、提供默认值或者报错
        if (!initialized) {
            instructions.add(new Instruction(Operation.LIT, 0));
        }
    }

    private void analyse_const_decl_stmt() throws CompileError {
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

        // 等于号
        expect(TokenType.ASSIGN);

        // TODO: 常表达式，常量只能被读取，不能被修改
        var value = analyseConstantExpression();

        // 分号
        expect(TokenType.SEMICOLON);

        // 加入符号表
        String name = (String) nameToken.getValue();
        addSymbol(name, true, true, nameToken.getStartPos(), identType);

        // TODO: 这里把常量值直接放进栈里，位置和符号表记录的一样。
        // 更高级的程序还可以把常量的值记录下来，遇到相应的变量直接替换成这个常数值，
        // 我们这里就先不这么干了。
        instructions.add(new Instruction(Operation.LIT, value));
    }

    private void analyse_function() throws CompileError {
        addScope();
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
        analyse_block_stmt();

        // 加入符号表
        String name = (String) nameToken.getValue();
        addFunctionSymbol(name, function_param_list, identType, nameToken.getStartPos());

        // 这里把常量值直接放进栈里，位置和符号表记录的一样。
        // 更高级的程序还可以把常量的值记录下来，遇到相应的变量直接替换成这个常数值，
        // 我们这里就先不这么干了。
        // TODO: instructions.add(new Instruction(Operation.LIT, value));
        removeScope();
    }

    private ArrayList<IdentType> analyse_function_param_list() throws CompileError {
        ArrayList<IdentType> function_param_list = new ArrayList<>();
        if (peek().getTokenType() == TokenType.IDENT) {
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
            addSymbol(name, true, false, nameToken.getStartPos(), identType);
            function_param_list.add(identType);
            while (nextIf(TokenType.COMMA) != null) {
                nameToken = expect(TokenType.IDENT);
                expect(TokenType.COLON);
                type = expect(TokenType.IDENT);
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
                name = (String) nameToken.getValue();
                addSymbol(name, true, false, nameToken.getStartPos(), identType);
                function_param_list.add(identType);

                // 这里把常量值直接放进栈里，位置和符号表记录的一样。
                // 更高级的程序还可以把常量的值记录下来，遇到相应的变量直接替换成这个常数值，
                // 我们这里就先不这么干了。
                // TODO: instructions.add(new Instruction(Operation.LIT, value));
            }
        }
        return function_param_list;
    }

    private void analyse_block_stmt() throws CompileError {
        addScope();
        expect(TokenType.L_BRACE);
        boolean stmtFlag = true;
        while (stmtFlag) {
            stmtFlag = analyse_stmt();
        }
        expect(TokenType.R_BRACE);
        removeScope();
    }

    private boolean analyse_stmt() throws CompileError {
        switch (peek().getTokenType()) {
            case IDENT:
            case L_PAREN:
            case MINUS:
                analyse_expr_stmt();
                break;
            case LET_KW:
                analyse_let_decl_stmt();
                break;
            case CONST_KW:
                analyse_const_decl_stmt();
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
                analyse_block_stmt();
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
    private void analyse_expr_stmt() throws CompileError {
        // TODO: 2020/11/18 表达式如果有值，值将会被丢弃
        analyse_expr();
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
    private void analyse_expr() throws CompileError {
        switch (peek().getTokenType()) {
            case IDENT:
                var nameToken = expect(TokenType.IDENT);
                switch (peek().getTokenType()) {
                    case ASSIGN:    // assign_expr
                        // TODO: 2020/11/18 查符号表
                        // TODO: 2020/11/18 赋值表达式的值类型永远是 void（即不能被使用）
                        expect(TokenType.ASSIGN);
                        analyse_expr();
                        break;
                    case L_PAREN:   // call_expr
                        // TODO: 2020/11/18 查函数符号表
                        // TODO: 2020/11/18 标准库中的函数在调用前不需要声明
                        expect(TokenType.L_PAREN);
                        analyse_call_param_list();
                        expect(TokenType.R_PAREN);
                    default:    // ident_expr
                        // 伊布西龙
                        // TODO: 2020/11/18 查符号表
                }
                break;
            case L_PAREN:   // group_expr
                expect(TokenType.L_PAREN);
                analyse_expr();
                expect(TokenType.R_PAREN);
                break;
            case MINUS:     // negate_expr
                expect(TokenType.MINUS);
                analyse_expr();
                break;
            // TODO: 2020/11/18 字符串字面量 只会在 putstr 调用中出现，语义是对应的全局常量的编号
            case UINT_LITERAL:  // literal_expr
            case DOUBLE_LITERAL:
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
    }

    // TODO: 2020/11/18
    private void analyse_call_param_list() throws CompileError {
        // TODO: 2020/11/18 判断与函数定义参数类型数量是否一致
        analyse_expr();
        while (nextIf(TokenType.COMMA) != null) {
            analyse_expr();
        }
    }

    private void analyseStatementSequence() throws CompileError {
        // 语句序列 -> 语句*
        // 语句 -> 赋值语句 | 输出语句 | 空语句

        while (true) {
            // 如果下一个 token 是……
            var peeked = peek();
            if (peeked.getTokenType() == TokenType.IDENT) {
                // 调用相应的分析函数
                // 如果遇到其他非终结符的 FIRST 集呢？
                analyseAssignmentStatement();
            } else if (peeked.getTokenType() == TokenType.Print) {
                analyseOutputStatement();
            } else if (peeked.getTokenType() == TokenType.SEMICOLON) {
                expect(TokenType.SEMICOLON);
            } else {
                // 都不是，摸了
                break;
            }
        }
    }

    private int analyseConstantExpression() throws CompileError {
        // 常表达式 -> 符号? 无符号整数
        boolean negative = false;
        if (nextIf(TokenType.PLUS) != null) {
            negative = false;
        } else if (nextIf(TokenType.MINUS) != null) {
            negative = true;
        }

        var token = expect(TokenType.UINT_LITERAL);

        int value = (int) token.getValue();
        if (negative) {
            value = -value;
        }

        return value;
    }

    private void analyseExpression() throws CompileError {
        // 表达式 -> 项 (加法运算符 项)*
        // 项
        analyseItem();

        while (true) {
            // 预读可能是运算符的 token
            var op = peek();
            if (op.getTokenType() != TokenType.PLUS && op.getTokenType() != TokenType.MINUS) {
                break;
            }

            // 运算符
            next();

            // 项
            analyseItem();

            // 生成代码
            if (op.getTokenType() == TokenType.PLUS) {
                instructions.add(new Instruction(Operation.ADD));
            } else if (op.getTokenType() == TokenType.MINUS) {
                instructions.add(new Instruction(Operation.SUB));
            }
        }
    }

    private void analyseAssignmentStatement() throws CompileError {
        // 赋值语句 -> 标识符 '=' 表达式 ';'

        // 分析这个语句

        // 标识符是什么？
        var nameToken = expect(TokenType.IDENT);
        expect(TokenType.ASSIGN);
        analyseExpression();
        expect(TokenType.SEMICOLON);
        // 标识符是什么？

        String name = (String) nameToken.getValue();
        var symbol = symbolTable.get(name);
        if (symbol == null) {
            // 没有这个标识符
            throw new AnalyzeError(ErrorCode.NotDeclared, /* 当前位置 */ nameToken.getStartPos());
        } else if (symbol.isConstant) {
            // 标识符是常量
            throw new AnalyzeError(ErrorCode.AssignToConstant, /* 当前位置 */ nameToken.getStartPos());
        }
        // 设置符号已初始化
        initializeSymbol(name, nameToken.getStartPos());

        // 把结果保存
        var offset = getOffset(name, nameToken.getStartPos());
        instructions.add(new Instruction(Operation.STO, offset));
    }

    private void analyseOutputStatement() throws CompileError {
        // 输出语句 -> 'print' '(' 表达式 ')' ';'

        expect(TokenType.Print);
        expect(TokenType.L_PAREN);

        analyseExpression();

        expect(TokenType.R_PAREN);
        expect(TokenType.SEMICOLON);

        instructions.add(new Instruction(Operation.WRT));
    }

    private void analyseItem() throws CompileError {
        // 项 -> 因子 (乘法运算符 因子)*

        // 因子
        analyseFactor();

        // 预读可能是运算符的 token
        while (true) {
            Token op = peek();
            if (op.getTokenType() != TokenType.MUL && op.getTokenType() != TokenType.DIV) {
                break;
            }

            // 运算符
            next();

            // 因子
            analyseFactor();

            // 生成代码
            if (op.getTokenType() == TokenType.MUL) {
                instructions.add(new Instruction(Operation.MUL));
            } else if (op.getTokenType() == TokenType.DIV) {
                instructions.add(new Instruction(Operation.DIV));
            }
        }
    }

    private void analyseFactor() throws CompileError {
        // 因子 -> 符号? (标识符 | 无符号整数 | '(' 表达式 ')')

        boolean negate;
        if (nextIf(TokenType.MINUS) != null) {
            negate = true;
            // 计算结果需要被 0 减
            instructions.add(new Instruction(Operation.LIT, 0));
        } else {
            nextIf(TokenType.PLUS);
            negate = false;
        }

        if (check(TokenType.IDENT)) {
            // 是标识符

            // 加载标识符的值
            var nameToken = expect(TokenType.IDENT);
            String name = (String) nameToken.getValue();/* 快填 */
            var symbol = symbolTable.get(name);
            if (symbol == null) {
                // 没有这个标识符
                throw new AnalyzeError(ErrorCode.NotDeclared, /* 当前位置 */ nameToken.getStartPos());
            } else if (!symbol.isInitialized) {
                // 标识符没初始化
                throw new AnalyzeError(ErrorCode.NotInitialized, /* 当前位置 */ nameToken.getStartPos());
            }
            var offset = getOffset(name, nameToken.getStartPos());
            instructions.add(new Instruction(Operation.LOD, offset));
        } else if (check(TokenType.UINT_LITERAL)) {
            // 是整数
            // 加载整数值
            var token = expect(TokenType.UINT_LITERAL);
            int value = (int) token.getValue();
            instructions.add(new Instruction(Operation.LIT, value));
        } else if (check(TokenType.L_PAREN)) {
            // 是表达式
            expect(TokenType.L_PAREN);
            // 调用相应的处理函数
            analyseExpression();
            expect(TokenType.R_PAREN);
        } else {
            // 都不是，摸了
            throw new ExpectedTokenError(List.of(TokenType.IDENT, TokenType.UINT_LITERAL, TokenType.L_PAREN), next());
        }

        if (negate) {
            instructions.add(new Instruction(Operation.SUB));
        }
    }
}
