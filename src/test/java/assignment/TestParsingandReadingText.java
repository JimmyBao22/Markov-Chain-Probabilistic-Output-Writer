package assignment;

import java.io.FileWriter;
import java.io.IOException;

public class TestParsingandReadingText {

    public static void main(String[] args) throws IOException {

        RandomWriter rw = (RandomWriter) RandomWriter.createProcessor(10);

        // String returnedString = rw.inputAndReadText(rw.reader, "prog2/test_books/MuchAdo.txt");

        FileWriter fileWriter = new FileWriter("test.txt");
        // fileWriter.write(returnedString);
        fileWriter.close();
    }


}
