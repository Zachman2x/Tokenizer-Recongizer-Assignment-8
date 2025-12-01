// CMSC 304 - TokenType 
public enum TokenType {
    LEFT_PARENTHESIS,    // (
    RIGHT_PARENTHESIS,   // )
    LEFT_BRACKET,        // {
    RIGHT_BRACKET,       // }
    COMMA,               // ,
    EOL,                 // ;
    EQUAL,               // =
    WHILE_KEYWORD,       // while
    RETURN_KEYWORD,      // return
    VARTYPE,             // int | void
    IDENTIFIER,          // [a-zA-Z][a-zA-Z0-9]*
    BINOP,               // + | * | != | == | %
    NUMBER,              // [0-9][0-9]*
    
    EOF
}
