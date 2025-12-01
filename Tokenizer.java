// CMSC 304 - Tokenizer
import java.io.*;
import java.util.*;

public class Tokenizer {

    private final String input;
    private final List<String> lexemes = new ArrayList<>();

    public Tokenizer(String input) {
        this.input = input;
    }

    public void scan() {
        StringBuilder current = new StringBuilder();
        int i = 0;

        while (i < input.length()) {
            char c = input.charAt(i);

            // Whitespace → delimiter
            if (Character.isWhitespace(c)) {
                flush(current);
                i++;
                continue;
            }

            // Symbol lexemes (may be multi-character)
            if (isSymbolStart(c)) {
                flush(current);

                if (i + 1 < input.length()) {
                    String two = "" + c + input.charAt(i + 1);
                    if (isTwoCharSymbol(two)) {
                        lexemes.add(two);
                        i += 2;
                        continue;
                    }
                }

                lexemes.add("" + c);
                i++;
                continue;
            }

            // Alphanumeric lexeme
            current.append(c);
            i++;
        }

        flush(current);
    }

    private void flush(StringBuilder sb) {
        if (sb.length() > 0) {
            lexemes.add(sb.toString());
            sb.setLength(0);
        }
    }

    private boolean isSymbolStart(char c) {
        return "(){}=,;!*+%".indexOf(c) >= 0;
    }

    private boolean isTwoCharSymbol(String s) {
        return s.equals("==") || s.equals("!=");
    }

    public List<Token> toTokens() {
        List<Token> tokens = new ArrayList<>();

        for (String lex : lexemes) {
            tokens.add(new Token(matchType(lex), lex));
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    private TokenType matchType(String lex) {
        switch (lex) {
            case "(": return TokenType.LEFT_PARENTHESIS;
            case ")": return TokenType.RIGHT_PARENTHESIS;
            case "{": return TokenType.LEFT_BRACKET;
            case "}": return TokenType.RIGHT_BRACKET;
            case "=": return TokenType.EQUAL;
            case ",": return TokenType.COMMA;
            case ";": return TokenType.EOL;
            case "while": return TokenType.WHILE_KEYWORD;
            case "return": return TokenType.RETURN_KEYWORD;
            case "int":
            case "void": return TokenType.VARTYPE;
        }

        if (lex.equals("+") || lex.equals("*") ||
            lex.equals("!=") || lex.equals("==") || lex.equals("%")) {
            return TokenType.BINOP;
        }

        if (isNumber(lex)) return TokenType.NUMBER;

        return TokenType.IDENTIFIER;
    }

    private boolean isNumber(String lex) {
        for (char c : lex.toCharArray())
            if (!Character.isDigit(c)) return false;
        return true;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java Tokenizer <input> <output>");
            return;
        }

        String input = new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get(args[0])
        ));

        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.scan();
        List<Token> tokens = tokenizer.toTokens();

        PrintWriter out = new PrintWriter(args[1]);
        for (Token t : tokens) {
            if (t.type == TokenType.EOF) continue;
            out.println(t.type + " " + t.lexeme);
        }
        out.close();
    }
}
