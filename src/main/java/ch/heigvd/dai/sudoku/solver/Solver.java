package ch.heigvd.dai.sudoku.solver;

import java.util.*;

public class Solver {
	private int[][] sudoku;
	private Map<Integer,Set<Integer>> columnMap;
	private Map<Integer,Set<Integer>> rowMap;
	private Map<Integer,Set<Integer>> boxMap;
	/*
	 * Initialize the column, row, and box HashMap
	 * Key: Integer Value: HashSet of Integer
	 * High memory usage to check if a number is valid 3O(n^2) but O(1) speed
	 */
	public Solver() {
		sudoku = new int[9][9];
		columnMap = new HashMap<Integer, Set<Integer>>() {{
			for (int i = 0; i < 9; i++) {
				put(i,new HashSet<Integer>());
			}
		}}; 
		rowMap = new HashMap<Integer, Set<Integer>>() {{
			for (int i = 0; i < 9; i++) {
				put(i,new HashSet<Integer>());
			}
		}}; 
		boxMap = new HashMap<Integer, Set<Integer>>() {{
			for (int i = 0; i < 9; i++) {
				put(i,new HashSet<Integer>());
			}
		}};
	}
	/*
	 * Assign a number value, between 0 to 9, to a 3x3 box on the sudoku board.
	 * If it is within the range of the x and y coordinates return its value.
	 * Uses integer truncation to find the beginning coordinate
	 * From left to right
	 * Top: 0-2
	 * Middle: 3-5
	 * Bottom: 6-8
	 */
	private int boxNum(int x, int y) {
		int xRange = (x/3)*3;
		int yRange = (y/3)*3;
		if(xRange==0 && yRange==0)
			return 0;
		else if(xRange==3 && yRange==0)
			return 1;
		else if (xRange==6 && yRange==0)
			return 2;
		else if (xRange==0 && yRange==3)
			return 3;
		else if(xRange==3 && yRange==3)
			return 4;
		else if (xRange==6 && yRange==3)
			return 5;
		else if (xRange==0 && yRange==6)
			return 6;
		else if(xRange==3 && yRange==6)
			return 7;
		else 
			return 8;
	}
	/*
	 * Check if a number is valid 
	 * @param int column (x), int row (y), int number (to check)
	 * @return boolean True if not contained False otherwise
	 */
	public boolean otherValid(int x, int y, int num) {
		//Check a row
		for(int i = 0; i < 9; i++) {
			if(sudoku[y][i] == num) {
				return false;
			}
		}
		//Check a column
		for(int j = 0; j < 9; j++) {
			if (sudoku[j][x] == num) {
				return false;
			}
		}
		//Check a 3x3 box
		int x_start = (x/3)*3;
		int y_start = (y/3)*3;
		for(int k = y_start; k < y_start+3; k++) {
			for(int l = x_start; l < x_start+3; l++) {
				if(sudoku[k][l] == num) {
					return false;
				}
			}
		}
		return true;
	}
	public boolean isValid(int x, int y, int num) {
		return !columnMap.get(x).contains(num) && !rowMap.get(y).contains(num)
				&&!boxMap.get(boxNum(x,y)).contains(num);
	}
	/*
	 * Add the number to the Sudoku board
	 * Add to the appropriate column, row, and box HashMap
	 * @param integer column (x), integer row (y), integer number (to add)
	 */
	public void addNum(int x, int y, int num) {
		sudoku[y][x] = num;
		columnMap.get(x).add(num);
		rowMap.get(y).add(num);
		boxMap.get(boxNum(x,y)).add(num);
	}
	/*
	 * Remove the number to the Sudoku board
	 * Remove from the appropriate column, row, and box HashMap
	 * @param integer column (x), integer row (y), integer number (to remove)
	 */
	public void removeNum(int x, int y, int num) {
		sudoku[y][x] = 0;
		columnMap.get(x).remove(num);
		rowMap.get(y).remove(num);
		boxMap.get(boxNum(x,y)).remove(num);
	}
	/*
	 * Find the next empty sudoku box 
	 * @return an integer array of size 2 
	 *  containing in index 0 column (x) 
	 *  and index 1 row (y)
	 * If the board is filled return an empty array 
	 * the sudoku is solved!!!
	 */
	private int[] findEmpty() {
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(sudoku[i][j] == 0) {
					return new int[] {j,i};
				}
			}
		}
		return new int[] {};
	}
	/*
	 * Print the Sudoku board to console
	 */
	public void printSudoku() {
		for (int i = 0; i < sudoku.length; i++) {
			for (int j = 0; j < sudoku[i].length; j++) {
				System.out.print(sudoku[i][j] + " ");
			}
			System.out.print("\n");
		}
	}
	public boolean solveSudoku() {
		int[] emptyCoord = findEmpty();
		//Terminate case if findEmpty returns an empty array
		if(emptyCoord.length == 0) {
			printSudoku();
			return true;
		}
		int x = emptyCoord[0];
		int y = emptyCoord[1];
		for(int i = 1; i<=9; i++) {
			if(otherValid(x,y,i)) {
			//if(isValid(x,y,i)) {
				addNum(x,y,i);
				if(solveSudoku())
					return true;
				else 
					removeNum(x,y,i);
			}
		}
		return false;
	}
	
	//Accessor method(s)
	/*
	 * @return the value at sudoku board coordinate
	 * @param integer column (x) integer row (y)
	 */
	public int value(int x, int y) {
		return sudoku[y][x];
	}
	//Testing main method
	public static void main(String[] args) {
		Solver board = new Solver();
		Scanner stdin = new Scanner(System.in);
		int y = 0;
		while (y<9) {
			String input = stdin.nextLine();
			String[] token = input.trim().split(",");
			for(int i = 0; i < token.length; i++) {
				if(Integer.parseInt(token[i])==0)
					continue;
				board.addNum(i, y, Integer.parseInt(token[i]));
			}
			y++;
		}
		stdin.close();
		if(!board.solveSudoku()) {
			System.out.println("NO SOLUTIONS!");
		}
	}

}
