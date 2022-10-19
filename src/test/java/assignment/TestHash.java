package assignment;

import java.io.*;
import java.util.Random;

public class TestHash {

    public static void main(String[] args) {
        try {
            testHash("prog2/test_books/testHashInput.txt", "test.txt", 10, 10000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void testHash(String inputFilename, String outputFilename, int k, int length) throws IOException {
        Random randomGenerator = new Random();
        int lowerLimit = 97; 			// letter 'a'
        int upperLimit = 122; 			// letter 'z'
        long hash_value = (long)(1e9 + 7);
        for (int j=0; j<5000; j++) {
            FileWriter fileWriter = new FileWriter(inputFilename);
            StringBuilder sb = new StringBuilder(length);
            char[] currentString = new char[length];
            for (int i=0; i<length; i++) {
                char current = (char)(randomGenerator.nextInt(upperLimit - lowerLimit) + lowerLimit);
                sb.append(current);
                currentString[i] = current;
            }
            fileWriter.write(sb.toString());
            fileWriter.close();
            RandomWriter rw = (RandomWriter) RandomWriter.createProcessor(k);
            rw.readText(inputFilename);
            rw.writeText(outputFilename, length);
            for (int i=0; i<length-k; i++) {
                // calculate each hash
                long hash = 0;
                for (int x=0; x<k; x++) {
                    hash = hash * hash_value + currentString[i+x];
                }
//                if (!rw.mapHashtoCharacters.containsKey(hash)) {
//                    System.out.println("HASH WRONG");
//                    return;
//                }
            }
            System.out.println("Test " + j + " complete.");
        }
        System.out.println("HASH WORKS");
    }
}
