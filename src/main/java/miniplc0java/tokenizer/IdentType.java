package miniplc0java.tokenizer;

public enum  IdentType {
    INT,
    VOID,
    DOUBLE;

    @Override
    public String toString() {
        switch (this) {
            case INT:
                return "int";
            case VOID:
                return "void";
            case DOUBLE:
                return "double";
            default:
                return "InvalidTokenType";
        }
    }
}
