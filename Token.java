// CMSC 304 - Token
public class Token {
    public TokenType type;
    public String lexeme;

    public Token(TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public String toString() {
        return type + " " + lexeme;
    }
}
