import java.io.*;
import java.util.*;

public class Recognizer {

    static List<Token> tokens = new ArrayList<>();
    static int index = 0;
    static PrintWriter out;

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.out.println("Usage: java Recognizer <input token file> <outputfile>");
            System.exit(0);
        }

        readTokens(args[0]);
        out = new PrintWriter(args[1]);

        program(); // top rule

        if (index != tokens.size() - 1)
            error("program", "Only consumed " + index +
                    " of the " + (tokens.size()-1) + " given tokens");

        out.println("PARSED!!!");
        out.close();
    }

    static void readTokens(String f) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        while ((line = br.readLine()) != null) {
            String[] split = line.split(" ",2);
            tokens.add(new Token(TokenType.valueOf(split[0]), split[1]));
        }
        br.close();
    }

    static Token lookahead() { return tokens.get(index); }
    static Token consume() { return tokens.get(index++); }

    static void expect(TokenType t, String rule) {
        if (lookahead().type != t)
            error(rule,"expected token #" + index +
                         " to be " + t + " but was " + lookahead().type);
        consume();
    }

    static void program() { body(); }

    static void body() {
        if (lookahead().type == TokenType.VARTYPE ||
            lookahead().type == TokenType.IDENTIFIER ||
            lookahead().type == TokenType.PRINT_KEYWORD)
        {
            statement();
            body();
        }
        else {
            return;
        }
    }

    static void statement() {
        if (lookahead().type == TokenType.VARTYPE) declaration();
        else if (lookahead().type == TokenType.IDENTIFIER) assign();
        else if (lookahead().type == TokenType.PRINT_KEYWORD) printStmt();
        else error("statement","expected valid statement non-terminal but was not");
    }

    static void declaration() {
        expect(TokenType.VARTYPE,"declaration");
        expect(TokenType.IDENTIFIER,"declaration");
        expect(TokenType.SEMICOLON,"declaration");
    }

    static void assign() {
        expect(TokenType.IDENTIFIER,"assign");
        expect(TokenType.ASSIGN,"assign");
        value();
        expect(TokenType.SEMICOLON,"assign");
    }

    static void printStmt() {
        expect(TokenType.PRINT_KEYWORD,"print");
        expect(TokenType.LEFT_PARENTHESIS,"print");
        expect(TokenType.IDENTIFIER,"print");
        expect(TokenType.RIGHT_PARENTHESIS,"print");
        expect(TokenType.SEMICOLON,"print");
    }

    static void value() {
        if(lookahead().type==TokenType.NUMBER ||
           lookahead().type==TokenType.IDENTIFIER) consume();
        else error("value","expected NUMBER or IDENTIFIER but was "+lookahead().type);
    }

    static void error(String rule, String msg) {
        out.println("Error: In grammar rule " + rule + ", " + msg);
        out.close();
        System.exit(0);
    }
}
