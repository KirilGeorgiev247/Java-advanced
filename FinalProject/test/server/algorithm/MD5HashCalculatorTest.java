package server.algorithm;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MD5HashCalculatorTest {

    @Test
    void testIfCorrectHashIsReturnedForABC() throws NoSuchAlgorithmException, IOException {
        String input = "abc";
        String expectedResult = "900150983cd24fb0d6963f7d28e17f72";

        HashCalculator calculator = new MD5HashCalculator();
        String actualResult = calculator.calculate(new ByteArrayInputStream(input.getBytes()));

        assertEquals(expectedResult, actualResult, "MD5 hash calculation does not match for input: " + input);
    }

    @Test
    void testIfCorrectHashIsReturnedForHelloWorld() throws NoSuchAlgorithmException, IOException {
        String input = "Hello world!";
        String expectedResult = "86fb269d190d2c85f6e0468ceca42a20";

        HashCalculator calculator = new MD5HashCalculator();
        String actualResult = calculator.calculate(new ByteArrayInputStream(input.getBytes()));

        assertEquals(expectedResult, actualResult, "MD5 hash calculation does not match for input: " + input);
    }
}

