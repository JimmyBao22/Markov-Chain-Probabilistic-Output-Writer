package assignment;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.lang.String;

/*
 * CS 314H Assignment 2 - Random Writing
 *
 * Your task is to implement this RandomWriter class
 */
public class RandomWriter implements TextProcessor {

    public static void main(String[] args) {
        if (args == null || args.length != 4) {
            System.err.println("Invalid input for arguments.");
            return;
        }
        for (int i=0; i<4; i++) {
            if (args[i] == null) {
                System.err.println("Invalid input for arguments.");
                return;
            }
        }

        String inputFilename = args[0];
        String outputFilename = args[1];
        int k;
        try {
            k = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid inputted level of analysis k.");
            return;
        }
        int length;
        try {
            length = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid inputted for length of output.");
            return;
        }

        if (k < 0) {
            System.err.println("Invalid input: k cannot be negative.");
            return;
        }
        if (length < 0) {
            System.err.println("Invalid input: length cannot be negative.");
            return;
        }

        if (checkInputFile(inputFilename) || checkOutputFile(outputFilename)) {
            return;
        }

        RandomWriter currentRandomWriter = new RandomWriter(k);

        try {
            String inputString = inputAndReadText(currentRandomWriter.reader, inputFilename);
            if (k >= inputString.length()) {
                System.err.println("Source does not contain more characters than k.");
                return;
            }
        } catch (IOException e) {
            System.err.println("File Invalid.");
            return;
        }

        try {
            currentRandomWriter.readText(inputFilename);
            currentRandomWriter.writeText(outputFilename, length);
        } catch (IOException e) {
            System.err.println("File Invalid.");
            return;
        }
    }

    public static boolean checkInputFile(String inputFilename) {
        File inputFile = new File(inputFilename);
        if (!inputFile.canRead()) {
            System.err.println("Cannot read from the given file.");
            return true;
        }
        return false;
    }

    public static boolean checkOutputFile(String outputFilename) {
        File outputFile = new File(outputFilename);
        if (!outputFile.canWrite()) {
            System.err.println("Cannot write to the given file.");
            return true;
        }
        return false;
    }

    // Unless you need extra logic here, you might not have to touch this method
    public static TextProcessor createProcessor(int level) {
      return new RandomWriter(level);
    }

    public static String inputAndReadText(StringBuilder reader, String inputFilename) throws IOException {
            // if I already called this function before, then reader has already stored the input string
        if (!reader.isEmpty()) {
            return reader.toString();
        }
        try (BufferedReader inputReader = new BufferedReader(new FileReader(inputFilename))) {
            String currentString = inputReader.readLine();
            while (currentString != null) {
                currentString = currentString.trim();
                // replaced multiple spaces with a single space. Also replaces the newline character with a single space
                if (reader.length() != 0 && reader.lastIndexOf(" ") != reader.length() - 1) reader.append(" ");
                reader.append(currentString);
                currentString = inputReader.readLine();
            }
        }
        return reader.toString();
    }

    private Random randomGenerator;
    private StringBuilder reader;
    private StringBuilder writer;
    private int level;
    private long hashValue, hashPower;
    private HashMap<Long, ArrayList<Character>> mapHashtoCharacters;

    private RandomWriter(int level) {
        this.level = level;
        randomGenerator = new Random();
        reader = new StringBuilder();
        writer = new StringBuilder();
        hashValue = (long)(1e9 + 7);
        hashPower = level >= 1 ? calculatePower(hashValue, level - 1) : 1;
        mapHashtoCharacters = new HashMap<>();
    }

    public void readText(String inputFilename) throws IOException {
        if (checkInputFile(inputFilename)) return;

        String inputString = inputAndReadText(reader, inputFilename);
        int n = inputString.length();
        long hash = 0;
        for (int i=0; i<level; i++) {
            // since I am generating a new hash, subtractBefore = false and therefore the replacedCharacter doesn't matter
            hash = updateHash(hash, inputString.charAt(i), i >= level, ' ');
        }
        if (level >= 1) updateHashMap(hash, level-1, n, inputString);

        // rolling hash
        for (int i=level; i<n; i++) {
            hash = updateHash(hash, inputString.charAt(i), i >= level, inputString.charAt(i - level));
            updateHashMap(hash, i, n, inputString);
        }
    }

    private void updateHashMap(long hash, int index, int length, String inputString) {
        if (index + 1 < length) {
            Character addedCharacter = inputString.charAt(index + 1);
            if (mapHashtoCharacters.containsKey(hash)) {
                mapHashtoCharacters.get(hash).add(addedCharacter);
            }
            else {
                mapHashtoCharacters.put(hash, new ArrayList<>());
                mapHashtoCharacters.get(hash).add(addedCharacter);
            }
        }
    }

    private long updateHash(long hash, Character addedCharacter, boolean subtractBefore, Character replacedCharacter) {
        // if level is 0, then should not roll the hash
        if (level == 0) return 0;

        // subtractBefore is a boolean that checks whether or not I need to subtract the number before. For example, if
            // for the hash it is the first substring (length k), then subtractBefore = false
        if (subtractBefore) {
            hash -= hashPower * (long) replacedCharacter;
        }
        hash = hash * hashValue + (long) addedCharacter;
        return hash;
    }

        // fast method to calculate p^b in log(b) time
    private long calculatePower(long p, long b) {
        long power = 1;
        while (b > 0) {
            if (b % 2 == 1) {
                power *= p;
            }
            p *= p;
            b >>= 1;
        }
        return power;
    }

    public void writeText(String outputFilename, int length) throws IOException {
        if (checkOutputFile(outputFilename)) return;

        String inputString = reader.toString();
        long hash = newSeed(inputString);
        while (writer.length() < length) {
            if (mapHashtoCharacters.containsKey(hash)) {
                // choose a random character from the arraylist that this hash maps to
                int chosenCharacterIndex = randomGenerator.nextInt(mapHashtoCharacters.get(hash).size());
                writer.append(mapHashtoCharacters.get(hash).get(chosenCharacterIndex));

                hash = updateHash(hash, writer.charAt(writer.length() - 1), true,
                        writer.charAt(writer.length() - level - 1));
            }
            else {
                // if the map does not contain the hash, then that means this substring has no characters following
                    // to choose from
                hash = newSeed(inputString);
            }
        }

        // ensures length of outputText is length
        String outputText = writer.substring(0, length);
        try (FileWriter fileWriter = new FileWriter(outputFilename)) {
            fileWriter.write(outputText);
        }
    }

    private long newSeed(String inputString) {
        // the new seed can start from any of the characters with index in range from 0 to inputString.length()-level
        int seed = randomGenerator.nextInt(inputString.length() - level + 1);
        long hash = 0;
        for (int i = 0; i < level; i++) {
            // since I am generating a new hash, subtractBefore = false and therefore the replacedCharacter doesn't matter
            hash = updateHash(hash, inputString.charAt(seed + i), false, ' ');
            writer.append(inputString.charAt(seed + i));
        }
        return hash;
    }
}
