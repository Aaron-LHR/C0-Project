package miniplc0java.tokenizer;

public enum TokenType {
    /** 空 */
    None,
    /** FN_KW */
    FN_KW,
    /** LET_KW */
    LET_KW,
    /** CONST_KW */
    CONST_KW,
    /** AS_KW */
    AS_KW,
    /** WHILE_KW */
    WHILE_KW,
    /** IF_KW */
    IF_KW,
    /** ELSE_KW */
    ELSE_KW,
    /** RETURN_KW */
    RETURN_KW,
    /** BREAK_KW */
    BREAK_KW,
    /** CONTINUE_KW */
    CONTINUE_KW,
    /** 无符号整数 */
    UINT_LITERAL,
    /** 字符串常量 */
    STRING_LITERAL,
    /** 浮点数常量 */
    DOUBLE_LITERAL,
    /** 字符常量 */
    CHAR_LITERAL,
    /** 标识符 */
    IDENT,
    /** 加号 */
    PLUS,
    /** 减号 */
    MINUS,
    /** 乘号 */
    MUL,
    /** 除号 */
    DIV,
    /** 赋值符号：= */
    ASSIGN,
    /** 等于符号：== */
    EQ,
    /** 不等符号：!= */
    NEQ,
    /** 小于号 */
    LT,
    /** 大于号 */
    GT,
    /** 小于等于号 */
    LE,
    /** 大于等于号 */
    GE,
    /** 左括号 */
    L_PAREN,
    /** 右括号 */
    R_PAREN,
    /** 左大括号 */
    L_BRACE,
    /** 右大括号 */
    R_BRACE,
    /** 箭头:-> */
    ARROW,
    /** 逗号 */
    COMMA,
    /** 冒号 */
    COLON,
    /** 分号 */
    SEMICOLON,
    /** 注释 */
    COMMENT,
    /** 文件尾 */
    EOF,
    /** 前置 - */
    PRE_MINUS;

    @Override
    public String toString() {
        switch (this) {
            case None:
                return "NullToken";
            case FN_KW:
                return "Fn";
            case LET_KW:
                return "let";
            case CONST_KW:
                return "const";
            case AS_KW:
                return "as";
            case WHILE_KW:
                return "while";
            case IF_KW:
                return "if";
            case ELSE_KW:
                return "else";
            case RETURN_KW:
                return "return";
            case BREAK_KW:
                return "break";
            case CONTINUE_KW:
                return "continue";
            case UINT_LITERAL:
                return "UnsignedInteger";
            case STRING_LITERAL:
                return "STRING_LITERAL";
            case DOUBLE_LITERAL:
                return "DOUBLE_LITERAL";
            case CHAR_LITERAL:
                return "CHAR_LITERAL";
            case IDENT:
                return "Identifier";
            case PLUS:
                return "PlusSign";
            case MINUS:
                return "MinusSign";
            case MUL:
                return "MultiplicationSign";
            case DIV:
                return "DivisionSign";
            case ASSIGN:
                return "AssignSign";
            case EQ:
                return "EqualSign";
            case NEQ:
                return "NoEqualSign";
            case LT:
                return "LessThanSign";
            case GT:
                return "GreaterThanSign";
            case LE:
                return "LessThanAndEqualSign";
            case GE:
                return "GreaterThanAndEqualSign";
            case EOF:
                return "EOF";
            case L_PAREN:
                return "LeftBracket";
            case R_PAREN:
                return "RightBracket";
            case L_BRACE:
                return "LeftBrace";
            case R_BRACE:
                return "RightBrace";
            case ARROW:
                return "Arrow";
            case COMMA:
                return "Comma";
            case COLON:
                return "Colon";
            case SEMICOLON:
                return "Semicolon";
            case COMMENT:
                return "Comment";
            default:
                return "InvalidToken";
        }
    }
}
