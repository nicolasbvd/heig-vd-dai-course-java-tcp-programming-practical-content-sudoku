package ch.heigvd.dai.sudoku;
import java.util.Arrays;
import java.util.BitSet;
import java.io.*;

import static ch.heigvd.dai.sudoku.Difficulty.*;
import static ch.heigvd.dai.sudoku.MoveValidity.*;


public class Sudoku {

    private int size;
    private int[][] grid;
    private BitSet mask;

    int hexToInt(char c) {
        // Check if the character is a digit (0-9)
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        // Check if the character is an uppercase hex digit (A-F)
        else if (c >= 'A' && c <= 'G') {
            return c - 'A' + 10;
        }
        // Check if the character is a lowercase hex digit (a-f)
        else if (c >= 'a' && c <= 'g') {
            return c - 'a' + 10;
        }
        // If not a valid hex character, throw an exception
        throw new IllegalArgumentException("Invalid hexadecimal character: " + c);
    }

    char intToHex(int i) {
        // Check if the integer is within the valid range (0-15)
        if (i >= 0 && i <= 9) {
            return (char) (i + '0');
        } else if (i >= 10 && i <= 15) {
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

    public Sudoku(byte[] encodedGrid) {
        size = encodedGrid[0];
        grid = new int[size][size];
        mask = new BitSet(size * size);  // Initialize empty mask

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = encodedGrid[i * size + j + 1];
            }
        }
    }

    public Sudoku(int new_size){
        size = new_size;
        grid = new int[size][size];
        mask = new BitSet(size * size);
    }

    byte[] importSudoku9x9(Difficulty difficulty) throws IOException {
        String sudokuString;
        Sudoku9x9FileManager manager = new Sudoku9x9FileManager();
        sudokuString = manager.getRandomPuzzle(difficulty);

        // Convert the 81-character grid string to byte array
        byte[] to_solve = new byte[81];
        for (int i = 0; i < 81; i++) {
            char c = sudokuString.charAt(i);
            // Convert char to numeric value (0-9)
            to_solve[i] = (byte)(c - '0');

            //To remove when solver
            grid[i/size][i%size] = to_solve[i];
        }


        /*
        Solve and mask
         */

        return to_solve;
    }

    /*
    TODO not buffered
     */
    byte[] importSudoku16x16() throws IOException {
        String[] sudokuString;
        Sudoku16x16FileManager manager = new Sudoku16x16FileManager();
        sudokuString = manager.getRandomPuzzle();

        byte[] toSolve = new byte[16*16];
        BitSet new_mask =  new BitSet(16*16);
        for (int row = 0; row < 16; row++) {
            for (int col = 0; col < 16; col++) {
                grid[row][col] = hexToInt(sudokuString[1].charAt(row*16+col));
                toSolve[row*16+col] = (byte)hexToInt(sudokuString[0].charAt(row*16+col));
                if(toSolve[row*16+col] == 0) {
                    new_mask.set(row * 16 + col);
                }
            }
        }
        mask = new_mask;
        return toSolve;
    }

    //Medium default value
    byte[] importSudoku9x9() throws IOException {
        return importSudoku9x9(MEDIUM);
    }

    public byte[] encode() {
        byte[] encoded = new byte[size * size + 1];
        encoded[0] = (byte) size;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                encoded[i * size + j + 1] = (byte) grid[i][j];
            }
        }
        return encoded;
    }

    public MoveValidity verifyMove(byte[] move) {
        // Check boundaries
        if (!(move[0] >= 0 && move[0] < size && move[1] >= 0 && move[1] < size)) {
            return OUT_OF_BOUNDS;
        }

        // Check if not already placed
        if (mask.get(move[0] * size + move[1])) {
            return ALREADY_PLACED;
        }

        // Check if correct
        if (move[2] != grid[move[0]][move[1]]) {
            return WRONG_MOVE;
        }

        // Correct move
        return CORRECT_MOVE;
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

    //TODO 16x16 not supported
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int blockSize = (int) Math.sqrt(size);

        // Add column numbers header
        sb.append("   "); // Space for row labels
        for (int j = 0; j < size; j++) {
            sb.append(" ").append(j + 1);
            if ((j + 1) % blockSize == 0 && j < size - 1) {
                sb.append("  ");
            }
        }
        sb.append("\n");

        // Add horizontal separator line
        sb.append("   ");
        for (int j = 0; j < size; j++) {
            sb.append("--");
            if ((j + 1) % blockSize == 0 && j < size - 1) {
                sb.append("--");
            }
        }
        sb.append("\n");

        // Add grid with row labels
        for (int i = 0; i < size; i++) {
            sb.append((char) ('A' + i)).append(" |");

            for (int j = 0; j < size; j++) {
                sb.append(" ").append((grid[i][j] != 0 ? grid[i][j] : " "));
                if ((j + 1) % blockSize == 0 && j < size - 1) {
                    sb.append(" |");
                }
            }
            sb.append("\n");

            if ((i + 1) % blockSize == 0 && i < size - 1) {
                sb.append("   ");
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
            // A valid 9x9 Sudoku grid
            Sudoku sudoku = new Sudoku(9);
            byte[] move = sudoku.importSudoku9x9();
            System.out.println(Arrays.toString(move));
            System.out.println(sudoku);
        }
    }
}