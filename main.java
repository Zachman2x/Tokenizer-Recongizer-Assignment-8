//main class used for testing
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        File testFolder = new File("testfiles");
        File[] files = testFolder.listFiles((d, name) -> name.endsWith(".txt"));

        if (files == null) {
            System.out.println("No test folder found.");
            return;
        }

        for (File f : files) {
            System.out.println("=== Testing " + f.getName() + " ===");

            String input = new String(Files.readAllBytes(f.toPath()));

            // Tokenize
            Tokenizer tokenizer = new Tokenizer(input);
            tokenizer.scan();
            List<Common> tokens = tokenizer.toTokens();

            // Recognize
            Recognizer recognizer = new Recognizer(tokens);
            try {
                recognizer.recognize();
                System.out.println("PARSED!!!");
            } catch (RuntimeException e) {
                System.out.println("ERROR: " + e.getMessage());
            }

            System.out.println();
        }
    }
}
