// CMSC 304 - Recognizer
import java.io.*;
import java.util.*;

public class Recognizer {
    static List<Token> tokens = new ArrayList<>();
    static int index = 0;
    static PrintWriter out = null;

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java Recognizer <tokensfile> <outputfile>");
            System.exit(0);
        }

        String inPath = args[0];
        String outPath = args[1];

        try (BufferedReader br = new BufferedReader(new FileReader(inPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                int firstSpace = line.indexOf(' ');
                if (firstSpace == -1) {
                    String tokName = line.trim();
                    tokens.add(new Token(TokenType.valueOf(tokName), ""));
                } else {
                    String tokName = line.substring(0, firstSpace);
                    String lexeme = line.substring(firstSpace + 1);
                    tokens.add(new Token(TokenType.valueOf(tokName), lexeme));
                }
            }
        }
        tokens.add(new Token(TokenType.EOF, ""));

        out = new PrintWriter(new FileWriter(outPath));
        function();

        int consumed = index; 
        int total = tokens.size() - 1;
        if (consumed != total) {
            // Error messages
            out.println("Error: Only consumed " + consumed + " of the " + total + " given tokens");
            out.close();
            System.exit(0);
        }

        out.println("PARSED!!!");
        out.close();
    }

    static Token la() {
        return tokens.get(index);
    }
    static Token consume() {
        return tokens.get(index++);
    }
    static boolean match(TokenType t) {
        if (la().type == t) { consume(); return true; }
        return false;
    }

    static void errorExpectedToken(String rule, int tokenNumberOneBased, TokenType expected, TokenType actual) {
        out.println("Error: In grammar rule " + rule + ", expected token #" + tokenNumberOneBased +
                " to be " + expected.name() + " but was " + actual.name());
        out.close();
        System.exit(0);
    }

    static void errorMissingNonTerminal(String rule) {
        out.println("Error: In grammar rule " + rule + ", expected a valid body non-terminal to be present but was not.");
        out.close();
        System.exit(0);
    }

    static void function() {
        header();
        body();
    }

    static void header() {
        // VARTYPE
        if (la().type != TokenType.VARTYPE) {
            errorExpectedToken("header", index + 1, TokenType.VARTYPE, la().type);
        }
        consume();
        if (la().type != TokenType.IDENTIFIER) {
            errorExpectedToken("header", index + 1, TokenType.IDENTIFIER, la().type);
        }
        consume();
        if (la().type != TokenType.LEFT_PARENTHESIS) {
            errorExpectedToken("header", index + 1, TokenType.LEFT_PARENTHESIS, la().type);
        }
        consume();
        if (la().type == TokenType.VARTYPE) {
            arg_decl();
        }
        if (la().type != TokenType.RIGHT_PARENTHESIS) {
            errorExpectedToken("header", index + 1, TokenType.RIGHT_PARENTHESIS, la().type);
        }
        consume();
    }

    static void arg_decl() {
        // first pair
        if (la().type != TokenType.VARTYPE) {
            errorMissingNonTerminal("arg-decl");
        }
        consume();

        if (la().type != TokenType.IDENTIFIER) {
            errorExpectedToken("arg-decl", index + 1, TokenType.IDENTIFIER, la().type);
        }
        consume();

        while (la().type == TokenType.COMMA) {
            consume(); // COMMA
            if (la().type != TokenType.VARTYPE) {
                errorExpectedToken("arg-decl", index + 1, TokenType.VARTYPE, la().type);
            }
            consume();
            if (la().type != TokenType.IDENTIFIER) {
                errorExpectedToken("arg-decl", index + 1, TokenType.IDENTIFIER, la().type);
            }
            consume();
        }
    }

    static void body() {
        if (la().type != TokenType.LEFT_BRACKET) {
            errorExpectedToken("body", index + 1, TokenType.LEFT_BRACKET, la().type);
        }
        consume();

        if (la().type == TokenType.WHILE_KEYWORD ||
            la().type == TokenType.RETURN_KEYWORD ||
            la().type == TokenType.IDENTIFIER) {
            statement_list();
        }

        if (la().type != TokenType.RIGHT_BRACKET) {
            errorExpectedToken("body", index + 1, TokenType.RIGHT_BRACKET, la().type);
        }
        consume();
    }

    static void statement_list() {
        if (!startOfStatement(la().type)) {
            errorMissingNonTerminal("statement-list");
        }
        while (startOfStatement(la().type)) {
            statement();
        }
    }

    static boolean startOfStatement(TokenType t) {
        return t == TokenType.WHILE_KEYWORD || t == TokenType.RETURN_KEYWORD || t == TokenType.IDENTIFIER;
    }

    static void statement() {
        TokenType t = la().type;
        if (t == TokenType.WHILE_KEYWORD) {
            while_loop();
        } else if (t == TokenType.RETURN_KEYWORD) {
            return_stmt();
        } else if (t == TokenType.IDENTIFIER) {
            assignment();
        } else {
            errorMissingNonTerminal("statement");
        }
    }

    // while-loop --> WHILE_KEYWORD LEFT_PARENTHESIS expression RIGHT_PARENTHESIS body
    static void while_loop() {
        if (la().type != TokenType.WHILE_KEYWORD) {
            errorExpectedToken("while-loop", index + 1, TokenType.WHILE_KEYWORD, la().type);
        }
        consume();

        if (la().type != TokenType.LEFT_PARENTHESIS) {
            errorExpectedToken("while-loop", index + 1, TokenType.LEFT_PARENTHESIS, la().type);
        }
        consume();

        expression();

        if (la().type != TokenType.RIGHT_PARENTHESIS) {
            errorExpectedToken("while-loop", index + 1, TokenType.RIGHT_PARENTHESIS, la().type);
        }
        consume();

        body();
    }

    // return --> RETURN_KEYWORD expression EOL
    static void return_stmt() {
        if (la().type != TokenType.RETURN_KEYWORD) {
            errorExpectedToken("return", index + 1, TokenType.RETURN_KEYWORD, la().type);
        }
        consume();

        expression();

        if (la().type != TokenType.EOL) {
            errorExpectedToken("return", index + 1, TokenType.EOL, la().type);
        }
        consume();
    }

    // assignment --> IDENTIFIER EQUAL expression EOL
    static void assignment() {
        if (la().type != TokenType.IDENTIFIER) {
            errorExpectedToken("assignment", index + 1, TokenType.IDENTIFIER, la().type);
        }
        consume();

        if (la().type != TokenType.EQUAL) {
            errorExpectedToken("assignment", index + 1, TokenType.EQUAL, la().type);
        }
        consume();

        expression();

        if (la().type != TokenType.EOL) {
            errorExpectedToken("assignment", index + 1, TokenType.EOL, la().type);
        }
        consume();
    }

    // expression --> factor expression'
    // factor --> IDENTIFIER | NUMBER | LEFT_PARENTHESIS expression RIGHT_PARENTHESIS
    // expression' --> BINOP factor expression' | epsilon
    static void expression() {
        factor();
        expressionPrime();
    }

    static void expressionPrime() {
        while (la().type == TokenType.BINOP) {
            consume(); // BINOP
            factor();
        }
    }

    static void factor() {
        TokenType t = la().type;
        if (t == TokenType.IDENTIFIER) {
            consume();
        } else if (t == TokenType.NUMBER) {
            consume();
        } else if (t == TokenType.LEFT_PARENTHESIS) {
            consume();
            expression();
            if (la().type != TokenType.RIGHT_PARENTHESIS) {
                errorExpectedToken("expression", index + 1, TokenType.RIGHT_PARENTHESIS, la().type);
            }
            consume();
        } else {
            // non-terminal expected but not found
            errorMissingNonTerminal("expression");
        }
    }
}
