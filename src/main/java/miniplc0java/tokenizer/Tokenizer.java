package miniplc0java.tokenizer;

import miniplc0java.error.TokenizeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.util.Pos;

public class Tokenizer {

    private StringIter it;

    public Tokenizer(StringIter it) {
        this.it = it;
    }

    // 这里本来是想实现 Iterator<Token> 的，但是 Iterator 不允许抛异常，于是就这样了
    /**
     * 获取下一个 Token
     * 
     * @return
     * @throws TokenizeError 如果解析有异常则抛出
     */
    public Token nextToken() throws TokenizeError {
        it.readAll();

        // 跳过之前的所有空白字符
        skipSpaceCharacters();

        if (it.isEOF()) {
            return new Token(TokenType.EOF, "", it.currentPos(), it.currentPos());
        }

        char peek = it.peekChar();
        if (Character.isDigit(peek)) {
            return numberLiteral();
        } else if (Character.isAlphabetic(peek)) {
            return lexIdentOrKeyword();
        } else if (peek == '\"') {
            return lexStringLiteral();
        } else if (peek == '\'') {
            return lexCharLiteral();
        } else {
            return lexOperatorOrCommentOrUnknown();
        }
    }

    private Token numberLiteral() throws TokenizeError {
        // 请填空：
        // 直到查看下一个字符不是数字为止:
        // -- 前进一个字符，并存储这个字符
        //
        // 解析存储的字符串为无符号整数
        // 解析成功则返回无符号整数类型的token，否则返回编译错误
        //
        // Token 的 Value 应填写数字的值
        boolean isDouble = false;
        StringBuilder stringBuilder = new StringBuilder();
        Pos start = it.currentPos();
        while(!it.isEOF()) {
            char peek = it.peekChar();
            if (Character.isDigit(peek)) {
                stringBuilder.append(it.nextChar());
            } else if (peek == '.' && !isDouble) {
                stringBuilder.append(it.nextChar());
                if (Character.isDigit(it.peekChar())) {
                    isDouble = true;
                } else {
                    throw new TokenizeError(ErrorCode.InvalidDouble ,it.currentPos());
                }
            } else if ((peek == 'e' || peek == 'E') && isDouble) {
                stringBuilder.append(it.nextChar());
                peek = it.peekChar();
                if (peek == '+' || peek == '-') {
                    stringBuilder.append(it.nextChar());
                }
                if (Character.isDigit(it.peekChar())) {
                    isDouble = true;
                } else {
                    throw new TokenizeError(ErrorCode.InvalidDouble ,it.currentPos());
                }
            } else {
                break;
            }
        }
        String result = stringBuilder.toString();
        Pos end = it.currentPos();
        if (isDouble) {
            try {
                return new Token(TokenType.UINT_LITERAL, Integer.parseInt(result), start, end);
            } catch (NumberFormatException e) {
                throw new TokenizeError(ErrorCode.IntegerOverflow ,it.currentPos());
            }
        } else {
            try {
                return new Token(TokenType.DOUBLE_LITERAL, Double.parseDouble(result), start, end);
            } catch (NumberFormatException e) {
                throw new TokenizeError(ErrorCode.DoubleOverflow ,it.currentPos());
            }
        }
    }

    private Token lexIdentOrKeyword() {
        // 请填空：
        // 直到查看下一个字符不是数字或字母为止:
        // -- 前进一个字符，并存储这个字符
        //
        // 尝试将存储的字符串解释为关键字
        // -- 如果是关键字，则返回关键字类型的 token
        // -- 否则，返回标识符
        //
        // Token 的 Value 应填写标识符或关键字的字符串
        StringBuilder stringBuilder = new StringBuilder();
        Pos start = it.currentPos();
        while(!it.isEOF()) {
            char peek = it.peekChar();
            if (Character.isLetterOrDigit(peek)) {
                stringBuilder.append(it.nextChar());
            } else {
                break;
            }
        }
        String result = stringBuilder.toString();
        Pos end = it.currentPos();
        TokenType tokenType;
        switch (result) {
            case "fn":
                tokenType = TokenType.FN_KW;
                break;
            case "let":
                tokenType = TokenType.LET_KW;
                break;
            case "const":
                tokenType = TokenType.CONST_KW;
                break;
            case "as":
                tokenType = TokenType.AS_KW;
                break;
            case "while":
                tokenType = TokenType.WHILE_KW;
                break;
            case "if":
                tokenType = TokenType.IF_KW;
                break;
            case "else":
                tokenType = TokenType.ELSE_KW;
                break;
            case "return":
                tokenType = TokenType.RETURN_KW;
                break;
            case "break":
                tokenType = TokenType.BREAK_KW;
                break;
            case "continue":
                tokenType = TokenType.CONTINUE_KW;
                break;
            default:
                tokenType = TokenType.IDENT;
        }
        return new Token(tokenType, result, start, end);
    }

    private Token lexStringLiteral() throws TokenizeError {
        StringBuilder stringBuilder = new StringBuilder();
        Pos start = it.currentPos();
        it.nextChar();
        while(!it.isEOF()) {
            char peek = it.peekChar();
            if (peek == '\\') {
                stringBuilder.append(it.nextChar());
                peek = it.peekChar();
                if (peek == '\'' || peek == '\"' || peek == '\\' || peek == 'n' || peek == 't' || peek == 'r') {
                    stringBuilder.append(it.nextChar());
                } else {
                    throw new TokenizeError(ErrorCode.InvalidStringLiteral ,it.currentPos());
                }
            } else if (peek == '\"') {
                it.nextChar();
                break;
            } else {
                stringBuilder.append(it.nextChar());
            }
        }
        String result = stringBuilder.toString();
        Pos end = it.currentPos();
        return new Token(TokenType.STRING_LITERAL, result, start, end);
    }

    private Token lexCharLiteral() throws TokenizeError {
        char result;
        Pos start = it.currentPos();
        it.nextChar();
        char peek = it.peekChar();
        if (peek == '\\') {
            it.nextChar();
            peek = it.peekChar();
            switch (peek) {
                case '\'':
                    result = '\'';
                    break;
                case '\"':
                    result = '\"';
                    break;
                case '\\':
                    result = '\\';
                    break;
                case 'n':
                    result = '\n';
                    break;
                case 't':
                    result = '\t';
                    break;
                case 'r':
                    result = '\r';
                    break;
                default:
                    throw new TokenizeError(ErrorCode.InvalidCharLiteral, it.currentPos());
            }
            it.nextChar();
        } else {
            result = it.nextChar();
        }
        Pos end = it.currentPos();
        // TODO: 2020/12/4 字符字面量 的语义是被包裹的字符的 ASCII 编码无符号扩展到 64 位的整数值，类型是 int
        return new Token(TokenType.CHAR_LITERAL, result, start, end);
    }

    private Token lexOperatorOrCommentOrUnknown() throws TokenizeError {
        switch (it.nextChar()) {
            case '+':
                return new Token(TokenType.PLUS, '+', it.previousPos(), it.currentPos());

            case '-':
                // 填入返回语句
                if (it.peekChar() == '>') {
                    Pos startPos = it.previousPos();
                    it.nextChar();
                    return new Token(TokenType.ARROW, "->", startPos, it.currentPos());
                } else {
                    return new Token(TokenType.MINUS, '-', it.previousPos(), it.currentPos());
                }

            case '*':
                // 填入返回语句
                return new Token(TokenType.MUL, '*', it.previousPos(), it.currentPos());

            case '/':
                // 填入返回语句
                if (it.peekChar() == '/') { //注释功能
                    Pos startPos = it.previousPos();
                    it.nextChar();
                    StringBuilder stringBuilder = new StringBuilder();
                    while (it.nextChar() != '\n') {
                        stringBuilder.append(it.nextChar());
                    }
                    String result = stringBuilder.toString();
                    Pos endPos = it.currentPos();
                    return new Token(TokenType.COMMENT, result, startPos, endPos);
                } else {
                    return new Token(TokenType.DIV, '/', it.previousPos(), it.currentPos());
                }

            // 填入更多状态和返回语句
            case '=':
                if (it.peekChar() == '=') {
                    Pos startPos = it.previousPos();
                    it.nextChar();
                    return new Token(TokenType.EQ, "==", startPos, it.currentPos());
                } else {
                    return new Token(TokenType.ASSIGN, '=', it.previousPos(), it.currentPos());
                }

            case '!':
                if (it.peekChar() == '=') {
                    Pos startPos = it.previousPos();
                    it.nextChar();
                    return new Token(TokenType.NEQ, "!=", startPos, it.currentPos());
                } else {
                    throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
                }

            case '<':
                if (it.peekChar() == '=') {
                    Pos startPos = it.previousPos();
                    it.nextChar();
                    return new Token(TokenType.LE, "<=", startPos, it.currentPos());
                } else {
                    return new Token(TokenType.LT, '<', it.previousPos(), it.currentPos());
                }

            case '>':
                if (it.peekChar() == '=') {
                    Pos startPos = it.previousPos();
                    it.nextChar();
                    return new Token(TokenType.GE, ">=", startPos, it.currentPos());
                } else {
                    return new Token(TokenType.GT, '>', it.previousPos(), it.currentPos());
                }

            case '(':
                // 填入返回语句
                return new Token(TokenType.L_PAREN, '(', it.previousPos(), it.currentPos());

            case ')':
                // 填入返回语句
                return new Token(TokenType.R_PAREN, ')', it.previousPos(), it.currentPos());

            case '{':
                // 填入返回语句
                return new Token(TokenType.L_BRACE, '{', it.previousPos(), it.currentPos());

            case '}':
                // 填入返回语句
                return new Token(TokenType.R_BRACE, '}', it.previousPos(), it.currentPos());

            case ',':
                // 填入返回语句
                return new Token(TokenType.COMMA, ',', it.previousPos(), it.currentPos());

            case ':':
                // 填入返回语句
                return new Token(TokenType.COLON, ':', it.previousPos(), it.currentPos());

            case ';':
                // 填入返回语句
                return new Token(TokenType.SEMICOLON, ';', it.previousPos(), it.currentPos());

            default:
                // 不认识这个输入，摸了
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        }
    }

    private void skipSpaceCharacters() {
        while (!it.isEOF() && Character.isWhitespace(it.peekChar())) {
            it.nextChar();
        }
    }
}
