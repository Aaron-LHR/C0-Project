package miniplc0java.error;

public enum ErrorCode {
    NoError, // Should be only used internally.
    StreamError, EOF, InvalidInput, InvalidIdentifier, IntegerOverflow, DoubleOverflow, InvalidDouble,
    InvalidStringLiteral, InvalidCharLiteral, InvalidIdentType, InvalidReturnValueType, InvalidExpr, TypeMismatch,
    DivideZero, TooShortParamList, TooLongParamList,
    NoBegin, NoEnd, NeedIdentifier, ConstantNeedValue, NoSemicolon, InvalidVariableDeclaration, IncompleteExpression,
    NotDeclared, AssignToConstant, DuplicateDeclaration, NotInitialized, InvalidAssignment, InvalidPrint, ExpectedToken
}
