// CMSC 304 - Recognizer
import java.util.*;

public class Recognizer {

    private final List<Token> tokens;
    private int index = 0;

    public Recognizer(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token peek() {
        return tokens.get(index);
    }

    private Token advance() {
        return tokens.get(index++);
    }

    private boolean match(TokenType type) {
        if (peek().type == type) {
            advance();
            return true;
        }
        return false;
    }

    private void expect(TokenType type, String msg) {
        if (!match(type)) {
            error("Expected " + type + " but got " + peek().type + " → " + msg);
        }
    }

    private void error(String msg) {
        System.out.println(msg);
        System.exit(0);
    }

    // Grammar: function → header body
    public void recognize() {
        parseFunction();
        if (peek().type != TokenType.EOF)
            error("Only consumed part of the file — extra tokens remain.");
    }

    private void parseFunction() {
        parseHeader();
        parseBody();
    }

    private void parseHeader() {
        expect(TokenType.VARTYPE, "function return type");
        expect(TokenType.IDENTIFIER, "function name");
        expect(TokenType.LEFT_PARENTHESIS, "(");

        if (peek().type == TokenType.VARTYPE) {
            parseArgDecl();
        }

        expect(TokenType.RIGHT_PARENTHESIS, ")");
    }

    private void parseArgDecl() {
        expect(TokenType.VARTYPE, "argument type");
        expect(TokenType.IDENTIFIER, "argument name");

        while (match(TokenType.COMMA)) {
            expect(TokenType.VARTYPE, "argument type");
            expect(TokenType.IDENTIFIER, "argument name");
        }
    }

    private void parseBody() {
        expect(TokenType.LEFT_BRACKET, "{");

        while (peek().type == TokenType.WHILE_KEYWORD ||
               peek().type == TokenType.RETURN_KEYWORD ||
               peek().type == TokenType.IDENTIFIER) {
            parseStatement();
        }

        expect(TokenType.RIGHT_BRACKET, "}");
    }

    private void parseStatement() {
        if (match(TokenType.WHILE_KEYWORD)) {
            expect(TokenType.LEFT_PARENTHESIS, "(");
            parseExpression();
            expect(TokenType.RIGHT_PARENTHESIS, ")");
            parseBody();
            return;
        }

        if (match(TokenType.RETURN_KEYWORD)) {
            parseExpression();
            expect(TokenType.EOL, ";");
            return;
        }

        expect(TokenType.IDENTIFIER, "assignment identifier");
        expect(TokenType.EQUAL, "=");
        parseExpression();
        expect(TokenType.EOL, ";");
    }

    private void parseExpression() {
        parseTerm();

        while (peek().type == TokenType.BINOP) {
            advance();
            parseTerm();
        }
    }

    private void parseTerm() {
        if (match(TokenType.IDENTIFIER)) return;
        if (match(TokenType.NUMBER)) return;

        if (match(TokenType.LEFT_PARENTHESIS)) {
            parseExpression();
            expect(TokenType.RIGHT_PARENTHESIS, ")");
            return;
        }

        error("Invalid term: " + peek().lexeme);
    }
}
