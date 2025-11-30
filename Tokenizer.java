import java.io.*;
import java.util.*;

public class Tokenizer {

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.out.println("Usage: java Tokenizer <inputfile> <outputfile>");
            System.exit(0);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        int ch;
        while ((ch = br.read()) != -1) sb.append((char) ch);
        br.close();

        List<Token> lexemes = generateLexemes(sb.toString());
        List<Token> tokens = classifyTokens(lexemes);

        PrintWriter out = new PrintWriter(outputFile);
        for (Token t : tokens) out.println(t);
        out.close();
    }

    static List<Token> generateLexemes(String src) {
        List<Token> list = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);

            if (Character.isLetterOrDigit(c)) {
                buffer.append(c);
            } else {
                if (buffer.length() > 0) {
                    list.add(new Token(null, buffer.toString()));
                    buffer.setLength(0);
                }

                if (!Character.isWhitespace(c)) {
                    list.add(new Token(null, Character.toString(c)));
                }
            }
        }
        if (buffer.length() > 0)
            list.add(new Token(null, buffer.toString()));

        return list;
    }

    static List<Token> classifyTokens(List<Token> raw) {
        List<Token> out = new ArrayList<>();

        for (Token t : raw) {
            String x = t.lexeme;

            if (x.equals("int")) out.add(new Token(TokenType.VARTYPE, x));
            else if (x.equals("return")) out.add(new Token(TokenType.RETURN_KEYWORD, x));
            else if (x.equals("print")) out.add(new Token(TokenType.PRINT_KEYWORD, x));
            else if (isNumber(x)) out.add(new Token(TokenType.NUMBER, x));
            else if (x.equals("(")) out.add(new Token(TokenType.LEFT_PARENTHESIS, x));
            else if (x.equals(")")) out.add(new Token(TokenType.RIGHT_PARENTHESIS, x));
            else if (x.equals("{")) out.add(new Token(TokenType.LEFT_BRACE, x));
            else if (x.equals("}")) out.add(new Token(TokenType.RIGHT_BRACE, x));
            else if (x.equals("[")) out.add(new Token(TokenType.LEFT_BRACKET, x));
            else if (x.equals("]")) out.add(new Token(TokenType.RIGHT_BRACKET, x));
            else if (x.equals(",")) out.add(new Token(TokenType.COMMA, x));
            else if (x.equals(";")) out.add(new Token(TokenType.SEMICOLON, x));
            else if (x.equals("=")) out.add(new Token(TokenType.ASSIGN, x));
            else if (x.equals("+")) out.add(new Token(TokenType.PLUS, x));
            else if (x.equals("-")) out.add(new Token(TokenType.MINUS, x));
            else if (x.equals("*")) out.add(new Token(TokenType.STAR, x));
            else if (x.equals("/")) out.add(new Token(TokenType.SLASH, x));
            else out.add(new Token(TokenType.IDENTIFIER, x));
        }
        out.add(new Token(TokenType.EOF,"EOF"));
        return out;
    }
    static boolean isNumber(String s) {
        for (char c : s.toCharArray())
            if (!Character.isDigit(c)) return false;
        return true;
    }
}
