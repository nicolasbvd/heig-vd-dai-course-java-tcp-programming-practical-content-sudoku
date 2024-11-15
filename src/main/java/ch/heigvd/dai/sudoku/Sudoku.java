package ch.heigvd.dai.sudoku;

import java.util.BitSet;

public class Sudoku {
    private final int size;
    private final int[][] grid;
    private final BitSet mask;

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

    public int verifyMove(byte[] move) {
        // Check boundaries
        if (!(move[0] >= 0 && move[0] < size && move[1] >= 0 && move[1] < size)) {
            return 1;
        }

        // Check if not already placed
        if (mask.get(move[0] * size + move[1])) {
            return 2;
        }

        // Check if correct
        if (move[2] != grid[move[0]][move[1]]) {
            return 3;
        }

        // Correct move
        return 0;
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
        public static void main(String[] args) {
            // A valid 9x9 Sudoku grid
            int[][] grid = {
                    {5, 3, 4, 6, 7, 8, 9, 1, 2},
                    {6, 7, 2, 1, 9, 5, 3, 4, 8},
                    {1, 9, 8, 3, 4, 2, 5, 6, 7},
                    {8, 5, 9, 7, 6, 1, 4, 2, 3},
                    {4, 2, 6, 8, 5, 3, 7, 9, 1},
                    {7, 1, 3, 9, 2, 4, 8, 5, 6},
                    {9, 6, 1, 5, 3, 7, 2, 8, 4},
                    {2, 8, 7, 4, 1, 9, 6, 3, 5},
                    {3, 4, 5, 2, 8, 6, 1, 7, 9}
            };

            int size = 9;
            BitSet mask = new BitSet(size * size);

            // Example: mask out some positions (0,0), (0,1), (1,1)
            mask.set(0);  // Position (0,0)
            mask.set(1);  // Position (0,1)
            mask.set(10); // Position (1,1)

            Sudoku sudoku = new Sudoku(size, grid, mask);
            Sudoku masked = sudoku.applyMask();
            System.out.println(masked);
        }
    }
}