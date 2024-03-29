
package csc435.app;

import java.io.*;
import java.nio.file.*;
import java.util.regex.*;

public class CleanDataset {
    public long dataset_size = 0;
    public double execution_time = 0.0;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Improper number of arguments. Usage: java CleanDataset <input_dir> <output_dir>");
            System.exit(1);
        }

        CleanDataset cleanDataset = new CleanDataset();

        long startTime = System.currentTimeMillis();
        cleanDataset.cleanDirectory(new File(args[0]), new File(args[1]));
        long endTime = System.currentTimeMillis();
        cleanDataset.execution_time = endTime - startTime;

        System.out.print("Finished cleaning " + cleanDataset.dataset_size + " MiB of data");
        System.out.println(" in " + cleanDataset.execution_time + " milliseconds");
    }

    private void cleanDirectory(File inputDir, File outputDir) {
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File[] files = inputDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    cleanDirectory(file, new File(outputDir, file.getName()));
                } else {
                    File outputFile = new File(outputDir, file.getName());
                    cleanFile(file, outputFile);
                }
            }
        }
    }

    private void cleanFile(File inputFile, File outputFile) {
        try {
            String content = new String(Files.readAllBytes(inputFile.toPath()));
            String cleanedContent = cleanContent(content);
            Files.write(outputFile.toPath(), cleanedContent.getBytes());
            dataset_size += outputFile.length() / (1024 * 1024); // size in MiB
        } catch (IOException e) {
            System.err.println("Error processing file: " + inputFile.getPath());
            e.printStackTrace();
        }
    }

    private String cleanContent(String content) {
        // Remove carriage returns
        String cleanedContent = content.replace("\r", "");
        // Replace sequences of delimiters with a single space (assuming space is the delimiter)
        cleanedContent = cleanedContent.replaceAll("[\n\t ]+", " ");
        // Remove non-alphanumeric characters (except delimiters which are now just single spaces)
        cleanedContent = cleanedContent.replaceAll("[^a-zA-Z0-9 ]", "");
        return cleanedContent;
    }
}
