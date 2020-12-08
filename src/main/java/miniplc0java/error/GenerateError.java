package miniplc0java.error;

import miniplc0java.util.Pos;

public class GenerateError extends CompileError {
    private static final long serialVersionUID = 1L;

    ErrorCode code;
    Pos pos;

    @Override
    public ErrorCode getErr() {
        return code;
    }

    @Override
    public Pos getPos() {
        return pos;
    }

    /**
     * @param code
     * @param pos
     */
    public GenerateError(ErrorCode code, Pos pos) {
        this.code = code;
        this.pos = pos;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Analyze Error: ").append(code).append(", at: ").append(pos).toString();
    }
}
