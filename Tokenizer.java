// CMSC 304 - Tokenizer
import java.io.*;
import java.util.*;

public class Tokenizer {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            // Gradescope requires exactly two command-line args
            System.out.println("Usage: java Tokenizer <inputfile> <outputfile>");
            System.exit(0);
        }

        String inPath = args[0];
        String outPath = args[1];

        StringBuilder src = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(inPath))) {
            int r;
            while ((r = br.read()) != -1) {
                src.append((char) r);
            }
        }

        List<String> lexemes = generateLexemes(src.toString());
        List<Token> tokens = classifyLexemes(lexemes);

        try (PrintWriter pw = new PrintWriter(new FileWriter(outPath))) {
            for (Token t : tokens) {
                pw.println(t.toString());
            }
        }
    }

    // Generate lexemes by going through each character.
    static List<String> generateLexemes(String s) {
        List<String> lexemes = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();

        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);

            if (Character.isWhitespace(c)) {
                if (buffer.length() > 0) {
                    lexemes.add(buffer.toString());
                    buffer.setLength(0);
                }
                i++;
                continue;
            }

            if (Character.isLetter(c) || Character.isDigit(c)) {
                buffer.append(c);
                i++;
                continue;
            }

            if (buffer.length() > 0) {
                lexemes.add(buffer.toString());
                buffer.setLength(0);
            }

            if ((c == '!' || c == '=') && i + 1 < s.length()) {
                char nxt = s.charAt(i + 1);
                if (c == '!' && nxt == '=') {
                    lexemes.add("!=");
                    i += 2;
                    continue;
                } else if (c == '=' && nxt == '=') {
                    lexemes.add("==");
                    i += 2;
                    continue;
                }
            }

            if (c == '(' || c == ')' || c == '{' || c == '}' || c == ',' ||
                c == ';' || c == '=' || c == '+' || c == '*' || c == '%') {
                lexemes.add(String.valueOf(c));
                i++;
                continue;
            }

            lexemes.add(String.valueOf(c));
            i++;
        }

        if (buffer.length() > 0) {
            lexemes.add(buffer.toString());
        }

        return lexemes;
    }

    // Map lexemes (strings) to token classes. No regex used.
    static List<Token> classifyLexemes(List<String> lexemes) {
        List<Token> tokens = new ArrayList<>();

        for (String lex : lexemes) {
            if (lex.equals("while")) {
                tokens.add(new Token(TokenType.WHILE_KEYWORD, lex));
                continue;
            }
            if (lex.equals("return")) {
                tokens.add(new Token(TokenType.RETURN_KEYWORD, lex));
                continue;
            }
            if (lex.equals("int") || lex.equals("void")) {
                tokens.add(new Token(TokenType.VARTYPE, lex));
                continue;
            }

            // Constant assignemnts
            switch (lex) {
                case "(":
                    tokens.add(new Token(TokenType.LEFT_PARENTHESIS, lex));
                    continue;
                case ")":
                    tokens.add(new Token(TokenType.RIGHT_PARENTHESIS, lex));
                    continue;
                case "{":
                    tokens.add(new Token(TokenType.LEFT_BRACKET, lex));
                    continue;
                case "}":
                    tokens.add(new Token(TokenType.RIGHT_BRACKET, lex));
                    continue;
                case ",":
                    tokens.add(new Token(TokenType.COMMA, lex));
                    continue;
                case ";":
                    tokens.add(new Token(TokenType.EOL, lex));
                    continue;
                case "=":
                    tokens.add(new Token(TokenType.EQUAL, lex));
                    continue;
                case "+":
                case "*":
                case "%":
                    tokens.add(new Token(TokenType.BINOP, lex));
                    continue;
                case "==":
                case "!=":
                    tokens.add(new Token(TokenType.BINOP, lex));
                    continue;
                default:
            }

            if (isNumber(lex)) {
                tokens.add(new Token(TokenType.NUMBER, lex));
                continue;
            }

            if (isIdentifier(lex)) {
                tokens.add(new Token(TokenType.IDENTIFIER, lex));
                continue;
            }
            tokens.add(new Token(TokenType.IDENTIFIER, lex));
        }

        return tokens;
    }

    static boolean isNumber(String s) {
        if (s.length() == 0) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }

    static boolean isIdentifier(String s) {
        if (s.length() == 0) return false;
        char first = s.charAt(0);
        if (!Character.isLetter(first)) return false;
        for (int i = 1; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isLetterOrDigit(c)) return false;
        }
        return true;
    }
}
