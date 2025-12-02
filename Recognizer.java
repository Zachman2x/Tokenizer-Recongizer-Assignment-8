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

                // Input line: IDENTIFIER  hello
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

            // If reached here, parsing succeeded
            writer.println("PARSED!!!");
            writer.flush();
            writer.close();

        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
    }
    
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
        writer.println(msg);
        writer.flush();
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
