// CMSC 304 - Recognizer
import java.util.*;

public class Recognizer {

    private final List<Common> tokens;
    private int index = 0;

    public Recognizer(List<Common> tokens) {
        this.tokens = tokens;
    }

    private Common peek() {
        return tokens.get(index);
    }

    private Common advance() {
        return tokens.get(index++);
    }

    private boolean match(Common.TokenType type) {
        if (peek().type == type) {
            advance();
            return true;
        }
        return false;
    }

    private void expect(Common.TokenType type, String msg) {
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
        if (peek().type != Common.TokenType.EOF)
            error("Only consumed part of the file — extra tokens remain.");
    }

    private void parseFunction() {
        parseHeader();
        parseBody();
    }

    private void parseHeader() {
        expect(Common.TokenType.VARTYPE, "function return type");
        expect(Common.TokenType.IDENTIFIER, "function name");
        expect(Common.TokenType.LEFT_PARENTHESIS, "(");

        if (peek().type == Common.TokenType.VARTYPE) {
            parseArgDecl();
        }

        expect(Common.TokenType.RIGHT_PARENTHESIS, ")");
    }

    private void parseArgDecl() {
        expect(Common.TokenType.VARTYPE, "argument type");
        expect(Common.TokenType.IDENTIFIER, "argument name");

        while (match(Common.TokenType.COMMA)) {
            expect(Common.TokenType.VARTYPE, "argument type");
            expect(Common.TokenType.IDENTIFIER, "argument name");
        }
    }

    private void parseBody() {
        expect(Common.TokenType.LEFT_BRACKET, "{");

        while (peek().type == Common.TokenType.WHILE_KEYWORD ||
               peek().type == Common.TokenType.RETURN_KEYWORD ||
               peek().type == Common.TokenType.IDENTIFIER) {
            parseStatement();
        }

        expect(Common.TokenType.RIGHT_BRACKET, "}");
    }

    private void parseStatement() {
        if (match(Common.TokenType.WHILE_KEYWORD)) {
            expect(Common.TokenType.LEFT_PARENTHESIS, "(");
            parseExpression();
            expect(Common.TokenType.RIGHT_PARENTHESIS, ")");
            parseBody();
            return;
        }

        if (match(Common.TokenType.RETURN_KEYWORD)) {
            parseExpression();
            expect(Common.TokenType.EOL, ";");
            return;
        }

        expect(Common.TokenType.IDENTIFIER, "assignment identifier");
        expect(Common.TokenType.EQUAL, "=");
        parseExpression();
        expect(Common.TokenType.EOL, ";");
    }

    private void parseExpression() {
        parseTerm();

        while (peek().type == Common.TokenType.BINOP) {
            advance();
            parseTerm();
        }
    }

    private void parseTerm() {
        if (match(Common.TokenType.IDENTIFIER)) return;
        if (match(Common.TokenType.NUMBER)) return;

        if (match(Common.TokenType.LEFT_PARENTHESIS)) {
            parseExpression();
            expect(Common.TokenType.RIGHT_PARENTHESIS, ")");
            return;
        }

        error("Invalid term: " + peek().lexeme);
    }
}
