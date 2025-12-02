// CMSC 304 - Recognizer
import java.util.*;
import java.io.*;

public class Recognizer {

    private final List<Common> tokens;
    private int index = 0;
    private PrintWriter writer;

    public Recognizer(List<Common> tokens, PrintWriter writer) {
        this.tokens = tokens;
        this.writer = writer;
    }

    public Recognizer(List<Common> tokens) {
        this.tokens = tokens;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Recognizer <inputFile> <outputFile>");
            return;
        }

        String inputPath = args[0];
        String outputPath = args[1];

        List<Common> tokenList = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputPath));
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+", 2);
                Common.TokenType type = Common.TokenType.valueOf(parts[0]);
                String lexeme = (parts.length > 1) ? parts[1] : "";
                tokenList.add(new Common(type, lexeme));
            }

            br.close();
            PrintWriter writer = new PrintWriter(new FileWriter(outputPath));

            Recognizer recognizer = new Recognizer(tokenList, writer);
            recognizer.recognize();

            if (recognizer.index != recognizer.tokens.size()) {
                int consumed = recognizer.index;
                int total = recognizer.tokens.size();
                writer.println("Error: Only consumed " + consumed +
                               " of the " + total + " given tokens");
                writer.flush();
                System.exit(0);
            }

            writer.println("PARSED!!!");
            writer.flush();
            writer.close();

        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
    }

    private Common peek() {
        if (index >= tokens.size()) {
            error("Error: Attempted to peek past end of token stream at token #" + index);
        }
        return tokens.get(index);
    }

    private Common advance() {
        if (index >= tokens.size()) {
            error("Error: Attempted to advance past end of token stream at token #" + index);
        }
        return tokens.get(index++);
    }

    private boolean match(Common.TokenType type) {
        if (index >= tokens.size()) return false;
        if (peek().type == type) {
            advance();
            return true;
        }
        return false;
    }

    private void expectToken(String rule, Common.TokenType expected) {
        if (index >= tokens.size()) {
            writer.println("Error: In grammar rule " + rule +
                           ", expected token #" + index +
                           " to be " + expected +
                           " but was EOF");
            writer.flush();
            System.exit(0);
        }

        Common.TokenType actual = peek().type;
        if (actual != expected) {
            writer.println("Error: In grammar rule " + rule +
                           ", expected token #" + index +
                           " to be " + expected +
                           " but was " + actual);
            writer.flush();
            System.exit(0);
        }

        advance();
    }

    // Non-terminal missing error
    private void nonTerminalError(String rule, String nonterm) {
        writer.println("Error: In grammar rule " + rule +
                       ", expected a valid " + nonterm +
                       " non-terminal to be present but was not.");
        writer.flush();
        System.exit(0);
    }

    // Generic error 
    private void error(String msg) {
        writer.println(msg);
        writer.flush();
        System.exit(0);
    }


    public void recognize() {
        parseFunction();
    }

    private void parseFunction() {
        parseHeader();
        parseBody();
    }

    private void parseHeader() {

        expectToken("function", Common.TokenType.VARTYPE);
        expectToken("function", Common.TokenType.IDENTIFIER);
        expectToken("function", Common.TokenType.LEFT_PARENTHESIS);

        if (index < tokens.size() && peek().type == Common.TokenType.VARTYPE) {
            parseArgDecl();
        }

        expectToken("function", Common.TokenType.RIGHT_PARENTHESIS);
    }

    private void parseArgDecl() {
        expectToken("argument", Common.TokenType.VARTYPE);
        expectToken("argument", Common.TokenType.IDENTIFIER);

        while (match(Common.TokenType.COMMA)) {
            expectToken("argument", Common.TokenType.VARTYPE);
            expectToken("argument", Common.TokenType.IDENTIFIER);
        }
    }

    private void parseBody() {
        expectToken("body", Common.TokenType.LEFT_BRACKET);

        while (index < tokens.size() &&
               (peek().type == Common.TokenType.WHILE_KEYWORD ||
                peek().type == Common.TokenType.RETURN_KEYWORD ||
                peek().type == Common.TokenType.IDENTIFIER)) {
            parseStatement();
        }

        expectToken("body", Common.TokenType.RIGHT_BRACKET);
    }

    private void parseStatement() {
        if (match(Common.TokenType.WHILE_KEYWORD)) {
            expectToken("while", Common.TokenType.LEFT_PARENTHESIS);
            parseExpression();
            expectToken("while", Common.TokenType.RIGHT_PARENTHESIS);
            parseBody();
            return;
        }

        if (match(Common.TokenType.RETURN_KEYWORD)) {
            parseExpression();
            expectToken("return", Common.TokenType.EOL);
            return;
        }

        if (peek().type == Common.TokenType.IDENTIFIER) {
            expectToken("assignment", Common.TokenType.IDENTIFIER);
            expectToken("assignment", Common.TokenType.EQUAL);
            parseExpression();
            expectToken("assignment", Common.TokenType.EOL);
            return;
        }

        nonTerminalError("function", "statement");
    }

    private void parseExpression() {
        parseTerm();

        while (index < tokens.size() && peek().type == Common.TokenType.BINOP) {
            advance();
            parseTerm();
        }
    }

    private void parseTerm() {
        if (match(Common.TokenType.IDENTIFIER)) return;
        if (match(Common.TokenType.NUMBER)) return;

        if (match(Common.TokenType.LEFT_PARENTHESIS)) {
            parseExpression();
            expectToken("term", Common.TokenType.RIGHT_PARENTHESIS);
            return;
        }

        if (index >= tokens.size()) {
            writer.println("Error: In grammar rule term, expected token #" + index + " to be one of IDENTIFIER, NUMBER, or LEFT_PARENTHESIS but was EOF");
            writer.flush();
            System.exit(0);
        } else {
            Common.TokenType actual = peek().type;
            writer.println("Error: In grammar rule term, expected token #" + index + " to be one of IDENTIFIER, NUMBER, or LEFT_PARENTHESIS but was " + actual);
            writer.flush();
            System.exit(0);
        }
    }
}
