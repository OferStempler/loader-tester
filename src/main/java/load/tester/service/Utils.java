package load.tester.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;


public class Utils {

    public static String loadFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Utils.class.getResourceAsStream(filename)))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
