package ch.heigvd.dai.sudoku.fileManagers;

import java.io.*;
import java.nio.file.*;
import java.util.Random;
import java.util.List;

public class Sudoku16x16FileManager  {
    private static final String DATASET_PATH = "/app/dataset/16X16-Sudoku-Dataset/16x16Dataset.csv";

    /**
     * Reads a random puzzle from the 16x16 dataset
     * @return String[] containing [puzzle, solution]
     * @throws IOException if file cannot be read
     */
    public static String[] getRandomPuzzle() throws IOException {
        // Read all lines from the file
        List<String> allLines = Files.readAllLines(Paths.get(DATASET_PATH));

        // Remove header if exists
        if (!allLines.isEmpty() && allLines.get(0).toLowerCase().contains("puzzle")) {
            allLines.remove(0);
        }

        // Get a random line
        Random random = new Random();
        String selectedLine = allLines.get(random.nextInt(allLines.size()));

        // Split the line into puzzle and solution
        // Assuming CSV format is: puzzle,solution
        String[] parts = selectedLine.split(",");

        if (parts.length != 2) {
            throw new IOException("Invalid puzzle format in CSV");
        }

        return parts;
    }
}