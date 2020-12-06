package miniplc0java.tokenizer;

public enum  IdentType {
    INT,
    VOID,
    DOUBLE,
    STRING_LITERAL,
    TRUE,
    FALSE
    ;

    @Override
    public String toString() {
        switch (this) {
            case INT:
                return "int";
            case VOID:
                return "void";
            case DOUBLE:
                return "double";
            case STRING_LITERAL:
                return "string";
            default:
                return "InvalidTokenType";
        }
    }
}
