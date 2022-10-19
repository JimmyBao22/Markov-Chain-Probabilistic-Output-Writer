package assignment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class TestNewSeed {

    public static void main(String[] args) {
        String inputFilename = "prog2/test_books/testInput.txt";
        String outputFilename = "test.txt";
        RandomWriter.main(new String[] {inputFilename, outputFilename, "1", "10000000"});
        try {
            findProbability(inputFilename, outputFilename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Checks that the seed generation is correct. Assumes each character is different
    public static void findProbability(String inputFilename, String outputFilename) throws IOException {
        StringBuilder sb = new StringBuilder();
        String in = RandomWriter.inputAndReadText(sb, inputFilename);
        BufferedReader output = new BufferedReader(new FileReader(outputFilename));
        String out = output.readLine();
        HashMap<Character, Integer> indicesOfInput = new HashMap<>();
        for (int i=0; i<in.length(); i++) {
            indicesOfInput.put(in.charAt(i), i);
        }

        int currentIndexInInput = 0;
        boolean newSeed = true;
        int totalStartCharacters = 0;
        HashMap<Character, Integer> map = new HashMap<>();
        for (int i=0; i<out.length(); i++) {
            if (newSeed) {
                totalStartCharacters++;
                map.put(out.charAt(i), map.getOrDefault(out.charAt(i), 0) + 1);
                newSeed = false;
                currentIndexInInput = indicesOfInput.get(out.charAt(i));
            }
            else {
                // since every character is unique (assumption for function), if it's not a new seed it must be a following
                // character
                if (indicesOfInput.get(out.charAt(i)) != currentIndexInInput + 1) {
                    System.out.println("WRONG ANSWER");
                    return;
                }
                currentIndexInInput++;
            }
            if (out.charAt(i) == in.charAt(in.length()-1)) {
                newSeed = true;
            }
        }

        for (int i=0; i<in.length(); i++) {
            if (map.containsKey(in.charAt(i))) {
                System.out.println(in.charAt(i) + ": " + (map.get(in.charAt(i)) / (double)(totalStartCharacters)));
            }
            else {
                System.out.println(in.charAt(i) + ": 0");
            }
        }
    }
}
