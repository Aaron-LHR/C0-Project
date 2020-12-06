package miniplc0java.error;

public enum ErrorCode {
    NoError, // Should be only used internally.
    StreamError, EOF, InvalidInput, InvalidIdentifier, IntegerOverflow, DoubleOverflow, InvalidDouble,
    InvalidStringLiteral, InvalidCharLiteral, InvalidIdentType, InvalidReturnValueType, InvalidExpr, TypeMisMatch,
    DivideZero, TooShortParamList, TooLongParamList, InvalidBinaryOperator, InvalidAsExpr, InvalidIfExpr,
    NoBegin, NoEnd, NeedIdentifier, ConstantNeedValue, NoSemicolon, InvalidVariableDeclaration, IncompleteExpression,
    VariableNotDeclared, FunctionNotDeclared, AssignToConstant, DuplicateDeclaration, NotInitialized, InvalidAssignment, InvalidPrint, ExpectedToken,
    BreakError, ContinueError, NoReturn
}
