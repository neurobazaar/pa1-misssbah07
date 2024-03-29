
package csc435.app;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SortWords {
    public long num_words = 0;
    public double execution_time = 0.0;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Improper number of arguments. Usage: java SortWords <input_dir> <output_dir>");
            System.exit(1);
        }

        SortWords sortWords = new SortWords();

        long startTime = System.currentTimeMillis();
        sortWords.processDirectory(new File(args[0]), new File(args[1]));
        long endTime = System.currentTimeMillis();
        sortWords.execution_time = endTime - startTime;

        System.out.print("Finished sorting " + sortWords.num_words + " words");
        System.out.println(" in " + sortWords.execution_time + " milliseconds");
    }

    private void processDirectory(File inputDir, File outputDir) {
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File[] files = inputDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    processDirectory(file, new File(outputDir, file.getName()));
                } else {
                    File outputFile = new File(outputDir, file.getName().replaceAll("\\.txt$", "_sorted.txt"));
                    sortWordsInFile(file, outputFile);
                }
            }
        }
    }

    private void sortWordsInFile(File inputFile, File outputFile) {
        try {
            List<String> lines = Files.readAllLines(inputFile.toPath());
            List<Map.Entry<String, Integer>> entries = new ArrayList<>();

            for (String line : lines) {
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    entries.add(new AbstractMap.SimpleEntry<>(parts[0], Integer.parseInt(parts[1])));
                }
            }

            // Sort the entries by frequency in descending order
            entries.sort((a, b) -> b.getValue().compareTo(a.getValue()));

            // Convert the sorted entries back to strings
            List<String> sortedLines = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : entries) {
                sortedLines.add(entry.getKey() + " " + entry.getValue());
                num_words += entry.getValue();
            }

            // Write the sorted words to the output file
            Files.write(outputFile.toPath(), sortedLines);
        } catch (IOException e) {
            System.err.println("Error processing file: " + inputFile.getPath());
            e.printStackTrace();
        }
    }
}
