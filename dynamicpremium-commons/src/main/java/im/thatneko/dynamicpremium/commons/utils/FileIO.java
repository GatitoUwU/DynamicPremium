package im.thatneko.dynamicpremium.commons.utils;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
public class FileIO {
    @SneakyThrows
    public static void writeFile(File file, String data) {
        FileWriter myWriter = new FileWriter(file);
        myWriter.write(data);
        myWriter.close();
    }

    @SneakyThrows
    public static String readFile(File file) {
        StringBuilder builder = new StringBuilder();

        Scanner myReader = new Scanner(file);
        while (myReader.hasNextLine()) {
            builder.append(myReader.nextLine());
        }
        myReader.close();

        return builder.toString();
    }
}