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

    public List<Common> toTokens() {
        List<Common> tokens = new ArrayList<>();

        for (String lex : lexemes) {
            tokens.add(new Common(matchType(lex), lex));
        }

        tokens.add(new Common(Common.TokenType.EOF, ""));
        return tokens;
    }

    private Common.TokenType matchType(String lex) {
        switch (lex) {
            case "(": return Common.TokenType.LEFT_PARENTHESIS;
            case ")": return Common.TokenType.RIGHT_PARENTHESIS;
            case "{": return Common.TokenType.LEFT_BRACKET;
            case "}": return Common.TokenType.RIGHT_BRACKET;
            case "=": return Common.TokenType.EQUAL;
            case ",": return Common.TokenType.COMMA;
            case ";": return Common.TokenType.EOL;
            case "while": return Common.TokenType.WHILE_KEYWORD;
            case "return": return Common.TokenType.RETURN_KEYWORD;
            case "int":
            case "void": return Common.TokenType.VARTYPE;
        }

        if (lex.equals("+") || lex.equals("*") ||
            lex.equals("!=") || lex.equals("==") || lex.equals("%")) {
            return Common.TokenType.BINOP;
        }

        if (isNumber(lex)) return Common.TokenType.NUMBER;

        return Common.TokenType.IDENTIFIER;
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
        List<Common> tokens = tokenizer.toTokens();

        PrintWriter out = new PrintWriter(args[1]);
        for (Common t : tokens) {
            if (t.type == Common.TokenType.EOF) continue;
            out.println(t.type + " " + t.lexeme);
        }
        out.close();
    }
}
