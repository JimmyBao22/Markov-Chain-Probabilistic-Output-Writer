package assignment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class TestDistribution {

    public static void main(String[] args) {
        String inputFilename = "prog2/test_books/CatInTheHat.txt";
        String outputFilename = "prog2/test.txt";
        int k = 25;
        RandomWriter.main(new String[] {inputFilename, outputFilename, String.valueOf(k), "100000000"});
        try {
            findDistribution(inputFilename, outputFilename, k, 0.05);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // for every single substring of length k, find the distribution of letters that come after
        // do this for both the inputfile and the output file, and compare the distributions
    public static void findDistribution(String inputFilename, String outputFilename, int k, double error) throws IOException {
        StringBuilder sb = new StringBuilder();
        String in = RandomWriter.inputAndReadText(sb, inputFilename);
        System.out.println(in.length());
        BufferedReader output = new BufferedReader(new FileReader(outputFilename));
        String out = output.readLine();

        HashMap<String, HashMap<Character, Integer>> inputDistribution = findExactDistribution(in, k);
        HashMap<String, HashMap<Character, Integer>> outputDistribution = findExactDistribution(out, k);

        double maximumError = 0;
        for (String substr : outputDistribution.keySet()) {
            if (!inputDistribution.containsKey(substr)) continue;

            // for every substring, given enough output, the distribution of characters after should be approximately equal
            double countOutputTotal = 0;
            for (Character c : outputDistribution.get(substr).keySet()) {
                if (!inputDistribution.get(substr).containsKey(c)) {
                    // this just means the next character was probably part of a new seed, or something of a similar manner
                    continue;
                }
                countOutputTotal += outputDistribution.get(substr).get(c);
            }

            double countInputTotal = 0;
            for (Character c : inputDistribution.get(substr).keySet()) {
                countInputTotal += inputDistribution.get(substr).get(c);
            }

            for (Character c : inputDistribution.get(substr).keySet()) {
                double outputDistributionForC = 0;
                if (outputDistribution.get(substr).containsKey(c)) {
                    outputDistributionForC = outputDistribution.get(substr).get(c) / countOutputTotal;
                }
                double inputDistributionForC = inputDistribution.get(substr).get(c) / countInputTotal;

                System.out.println(c + " " + outputDistributionForC + " " + inputDistributionForC);
                System.out.println(outputDistributionForC - inputDistributionForC);
                maximumError = Math.max(maximumError, Math.abs(outputDistributionForC - inputDistributionForC));
//                if (Math.abs(outputDistributionForC - inputDistributionForC) > error) {
//                    System.out.println("LARGE ERROR");
//                    System.out.println("substring: \"" + substr + "\" output: " + outputDistributionForC + " input: " + inputDistributionForC +
//                            " error: " + Math.abs(outputDistributionForC - inputDistributionForC));
//                    return;
//                }
            }
        }
        System.out.println(maximumError);
    }

    public static HashMap<String, HashMap<Character, Integer>> findExactDistribution(String s, int k) {
        int n = s.length();
        HashMap<String, HashMap<Character, Integer>> distribution = new HashMap<>();
        for (int i=0; i<n-k; i++) {
            String current = s.substring(i, i+k);
            if (distribution.containsKey(current)) {
                distribution.get(current).put(s.charAt(i+k),
                        distribution.get(current).getOrDefault(s.charAt(i+k), 0) + 1);
            }
            else {
                distribution.put(current, new HashMap<>());
                distribution.get(current).put(s.charAt(i+k), 1);
            }
        }
        return distribution;
    }
}