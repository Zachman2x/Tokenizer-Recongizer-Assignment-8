
public class Common {
    public enum TokenType {
    LEFT_PARENTHESIS,   // (
    RIGHT_PARENTHESIS,  // )
    LEFT_BRACKET,       // {
    RIGHT_BRACKET,      // }
    WHILE_KEYWORD,      // while
    RETURN_KEYWORD,     // return
    EQUAL,              // =
    COMMA,              // ,
    EOL,                // ;
    VARTYPE,            // int | void
    IDENTIFIER,         // [a-zA-Z][a-zA-Z0-9]*
    BINOP,              // + | * | != | == | %
    NUMBER,             // [0-9][0-9]*
    EOF                 // end of file
    }
    public final TokenType type;
    public final String lexeme;

    public Common(TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        return type + " " + lexeme;
    }
}


