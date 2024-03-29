
package csc435.app;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CountWords {
    public long dataset_size = 0;
    public double execution_time = 0.0;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Improper number of arguments. Usage: java CountWords <input_dir> <output_dir>");
            System.exit(1);
        }

        CountWords countWords = new CountWords();

        long startTime = System.currentTimeMillis();
        countWords.processDirectory(new File(args[0]), new File(args[1]));
        long endTime = System.currentTimeMillis();
        countWords.execution_time = endTime - startTime;

        System.out.print("Finished counting " + countWords.dataset_size + " MiB of words");
        System.out.println(" in " + countWords.execution_time + " milliseconds");
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
                    File outputFile = new File(outputDir, file.getName().replaceAll("\\.txt$", "_counts.txt"));
                    countWordsInFile(file, outputFile);
                }
            }
        }
    }

    private void countWordsInFile(File inputFile, File outputFile) {
        try {
            String content = new String(Files.readAllBytes(inputFile.toPath()));
            Map<String, Integer> wordCounts = new HashMap<>();
            Scanner scanner = new Scanner(content);
            while (scanner.hasNext()) {
                String word = scanner.next();
                wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
            }
            scanner.close();

            List<String> counts = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
                counts.add(entry.getKey() + " " + entry.getValue());
            }
            Files.write(outputFile.toPath(), counts);
            dataset_size += outputFile.length() / (1024 * 1024); // size in MiB
        } catch (IOException e) {
            System.err.println("Error processing file: " + inputFile.getPath());
            e.printStackTrace();
        }
    }
}
