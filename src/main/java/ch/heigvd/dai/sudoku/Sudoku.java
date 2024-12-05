package ch.heigvd.dai.sudoku;
import java.util.BitSet;
import java.io.*;

import ch.heigvd.dai.sudoku.enums.Difficulty;
import ch.heigvd.dai.sudoku.enums.MoveValidity;
import ch.heigvd.dai.sudoku.fileManagers.Sudoku16x16FileManager;
import ch.heigvd.dai.sudoku.fileManagers.Sudoku9x9FileManager;
import ch.heigvd.dai.sudoku.solver.Solver;

import static ch.heigvd.dai.sudoku.enums.Difficulty.*;
import static ch.heigvd.dai.sudoku.enums.MoveValidity.*;


public class Sudoku {

    private int size;
    private int[][] grid;
    private BitSet mask;

    int hexToInt(char c) {
        // Check if the character is a digit (0-9)
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        // Check if the character is an uppercase hex digit (A-G)
        else if (c >= 'A' && c <= 'G') {
            return c - 'A' + 10;
        }
        // Check if the character is a lowercase hex digit (a-g)
        else if (c >= 'a' && c <= 'g') {
            return c - 'a' + 10;
        }
        // If not a valid hex character, throw an exception
        throw new IllegalArgumentException("Invalid hexadecimal character: " + c);
    }

    char intToHex(int i) {
        // Check if the integer is within the valid range (0-16)
        if (i >= 0 && i <= 9) {
            return (char) (i + '0');
        } else if (i >= 10 && i <= 16) {
            return (char) (i - 10 + 'A');
        }
        // If not within range, throw an exception
        throw new IllegalArgumentException("Invalid integer for hexadecimal: " + i);
    }

    public Sudoku(int size, int[][] grid, BitSet mask) {
        this.size = size;
        this.grid = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(grid[i], 0, this.grid[i], 0, size);
        }
        this.mask = mask;
    }

    public Sudoku(String stringGrid, String new_size) {
        size = Integer.parseInt(new_size);
        grid = new int[size][size];
        mask = new BitSet(size * size);  // Initialize empty mask

        // Convert strings to integers and populate the grid
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = hexToInt(stringGrid.charAt(i * size + j));
            }
        }
    }

    public Sudoku(int new_size){
        size = new_size;
        grid = new int[size][size];
        mask = new BitSet(size * size);
    }


    String importSudoku9x9(Difficulty difficulty) throws IOException {
        String sudokuString;
        Sudoku9x9FileManager manager = new Sudoku9x9FileManager();
        sudokuString = manager.getRandomPuzzle(difficulty);

        size = 9;
        BitSet new_mask =  new BitSet(size*size);
        grid = new int[size][size];

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                grid[row][col] = hexToInt(sudokuString.charAt(row*9+col));
                if(grid[row][col] == 0) {
                    new_mask.set(row * 9 + col);
                }
            }
        }
        mask = new_mask;


        // Create and initialize the solver
        Solver solver = new Solver();

        // Transfer your grid to the solver
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (grid[row][col] != 0) {
                    solver.addNum(col, row, grid[row][col]);
                }
            }
        }

        // Solve the puzzle
        if (solver.solveSudoku()) {
            // Transfer the solution back to your grid
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    grid[row][col] = solver.value(col, row);
                }
            }
        } else {
            throw new IllegalStateException("No solution exists for this puzzle");
        }

        return sudokuString;
    }

    /*
    TODO not buffered
     */
    String importSudoku16x16() throws IOException {
        String[] sudokuString;
        Sudoku16x16FileManager manager = new Sudoku16x16FileManager();
        sudokuString = manager.getRandomPuzzle();

        size = 16;
        BitSet new_mask =  new BitSet(size*size);
        grid = new int[size][size];

        for (int row = 0; row < 16; row++) {
            for (int col = 0; col < 16; col++) {
                grid[row][col] = hexToInt(sudokuString[1].charAt(row*16+col));
                if(hexToInt(sudokuString[0].charAt(row*16+col)) == 0) {
                    new_mask.set(row * 16 + col);
                }
            }
        }
        mask = new_mask;
        return sudokuString[0];
    }

    //Medium default value
    String importSudoku9x9() throws IOException {
        return importSudoku9x9(MEDIUM);
    }

    public String importSudoku(String new_size_string) throws IOException {
        int new_size = Integer.parseInt(new_size_string);

        switch (new_size) {
            case 9:
                try {
                    return importSudoku9x9();  // Default difficulty MEDIUM
                } catch (IOException e) {
                    throw new IOException("Error importing 9x9 Sudoku puzzle", e);
                }
            case 16:
                try {
                    return importSudoku16x16();  // For 16x16 Sudoku
                } catch (IOException e) {
                    throw new IOException("Error importing 16x16 Sudoku puzzle", e);
                }
            default:
                throw new IllegalArgumentException("Unsupported Sudoku size: " + new_size);
        }
    }

    public void applyMove(String position, String value) {
        int row = position.charAt(0) - 'A';  // B -> 1
        int col = Integer.parseInt(position.substring(1)) - 1;  // 12 -> 11
        int valueInt = Integer.parseInt(value);
        grid[row][col] = valueInt;
        mask.clear(row * size + col);
    }


    public MoveValidity verifyMove(String position, String value) {
        // Convert position string (e.g. "B12") to row and column
        int row = position.charAt(0) - 'A';  // B -> 1
        int col = Integer.parseInt(position.substring(1)) - 1;  // 12 -> 11
        int valueInt = Integer.parseInt(value);

        // Check boundaries
        if (!(row >= 0 && row < size && col >= 0 && col < size) || !(valueInt >= 1 && valueInt <= size)) {
            return OUT_OF_BOUNDS;
        }

        // Check if not already placed
        if (!mask.get(row * size + col)) {
            return ALREADY_PLACED;
        }

        // Check if correct
        if (valueInt != grid[row][col]) {
            return WRONG_MOVE;
        }

        // Correct move
        applyMove(position, value);
        if(mask.cardinality() == 0){
            return COMPLETED;
        }
        return CORRECT_MOVE;
    }

    public boolean validity() {
        // Check rows
        for (int row = 0; row < size; row++) {
            BitSet nums = new BitSet(size + 1);
            for (int col = 0; col < size; col++) {
                int num = grid[row][col];
                if (num != 0) {
                    if (nums.get(num)) return false;
                    nums.set(num);
                }
            }
        }

        // Check columns
        for (int col = 0; col < size; col++) {
            BitSet nums = new BitSet(size + 1);
            for (int row = 0; row < size; row++) {
                int num = grid[row][col];
                if (num != 0) {
                    if (nums.get(num)) return false;
                    nums.set(num);
                }
            }
        }

        // Check boxes
        int boxSize = (int) Math.sqrt(size);
        for (int box = 0; box < size; box++) {
            BitSet nums = new BitSet(size + 1);
            int boxRow = (box / boxSize) * boxSize;
            int boxCol = (box % boxSize) * boxSize;

            for (int i = 0; i < boxSize; i++) {
                for (int j = 0; j < boxSize; j++) {
                    int num = grid[boxRow + i][boxCol + j];
                    if (num != 0) {
                        if (nums.get(num)) return false;
                        nums.set(num);
                    }
                }
            }
        }

        return true;
    }

    public Sudoku applyMask() {
        Sudoku sudoku = new Sudoku(size, grid, mask);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (mask.get(i * size + j)) {
                    sudoku.grid[i][j] = 0;
                }
            }
        }
        return sudoku;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int blockSize = (int) Math.sqrt(size);

        // Add column numbers header
        sb.append("    "); // Space for row labels (extra space for 2-digit numbers)
        for (int j = 0; j < size; j++) {
            sb.append(" ").append(intToHex(j+1));
            if ((j + 1) % blockSize == 0 && j < size - 1) {
                sb.append("  ");
            }
        }
        sb.append("\n");

        // Add horizontal separator line
        sb.append("    "); // Match the header spacing
        for (int j = 0; j < size; j++) {
            sb.append("--");
            if ((j + 1) % blockSize == 0 && j < size - 1) {
                sb.append("--");
            }
        }
        sb.append("\n");

        // Add grid with row labels
        for (int i = 0; i < size; i++) {
            sb.append((char) ('A' + i)).append("  |"); // Extra space for alignment

            for (int j = 0; j < size; j++) {
                sb.append(" ").append(grid[i][j] != 0 ? intToHex(grid[i][j]) : " ");
                if ((j + 1) % blockSize == 0 && j < size - 1) {
                    sb.append(" |");
                }
            }
            sb.append("\n");

            if ((i + 1) % blockSize == 0 && i < size - 1) {
                sb.append("    "); // Match the header spacing
                for (int j = 0; j < size; j++) {
                    sb.append("--");
                    if ((j + 1) % blockSize == 0 && j < size - 1) {
                        sb.append("--");
                    }
                }
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    // Test class to demonstrate usage
    static class Test {
        public static void main(String[] args) throws IOException {
            // Test different difficulties
            Difficulty[] difficulties = {EASY, MEDIUM, HARD};

            for (Difficulty diff : difficulties) {
                System.out.println("\nTesting " + diff + " puzzle:");

                Sudoku sudoku = new Sudoku(9);
                String original = sudoku.importSudoku9x9(diff);

                System.out.println("Original puzzle:");
                System.out.println(sudoku.applyMask());

                System.out.println("\nSolved puzzle:");
                System.out.println(sudoku);

                System.out.println("Solution valid: " + sudoku.validity());
                System.out.println("---------------------");
            }
        }
    }
}