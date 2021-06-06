import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Minesweeper {

	static Scanner sn = new Scanner(System.in);
	
	public static void main(String[] args) {
		String[][] board = makeBoard();
		int[][] bombBoard = makeBombBoard();
		int[][] numberBoard = makeNumberBoard(bombBoard);
		game(board, numberBoard);
	}

	public static int[][] makeBombBoard() {
		int[][] bombBoard = new int[9][9];

		int numBombs = 0;
		int randRow;
		int randCol;
		while (numBombs < 10) {
			randRow = (int) (Math.random() * 9);
			randCol = (int) (Math.random() * 9);
			if (bombBoard[randRow][randCol] == 0) {
				bombBoard[randRow][randCol] = 1;
				numBombs++;
			}
		}
		//printBoard(bombBoard);
		return bombBoard;
	}

	public static int[][] makeNumberBoard(int[][] bombBoard) {
		int[][] numberBoard = new int[9][9];
		for (int row = 0; row < bombBoard.length; row++) {
			for (int col = 0; col < bombBoard[row].length; col++) {
				numberBoard[row][col] = findAroundBombs(bombBoard, row, col);
			}
		}
		//printBoard(numberBoard);
		return numberBoard;
	}

	public static String[][] makeBoard() {
		String[][] board = new String[9][9];
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				board[row][col] = "-";
			}
		}
		printBoard(board);
		return board;
	}

	public static boolean revealOrFlag() { // click is true, flag is false
		while (true) {
			System.out.println("Click(0) or Flag(1)?");
			int answer = sn.nextInt();
			if (answer == 0) return true;
			else if (answer == 1) return false;
			else System.out.println("Invalid, answer again");
		}
	}

	public static int[] click() {
		boolean valid = false;
		int row = -1;
		int col = -1;
		while (!valid) {
			System.out.println("row?");
			row = sn.nextInt();
			if (row >= 0 && row <= 8) valid = true;
			else System.out.println("Invalid, answer again"); 
		}
		valid = false;
		while (!valid) {
			System.out.println("col?");
			col = sn.nextInt();
			if (col >= 0 && col <= 8) valid = true;
			else System.out.println("Invalid, answer again");
		}
		int[] coordinates = {row, col};
		return coordinates;
	}

	public static void game(String[][] board, int[][] numberBoard) {
		boolean boom = false;
		boolean winner = false;
		while (!boom && !winner) {
			System.out.println(boom);
			boolean invalid;
			do {
				invalid = false;
				boolean shouldReveal = revealOrFlag();
				int[] coordinates = click();
				int row = coordinates[0];
				int col = coordinates[1];
				if (shouldReveal) {
					int aroundBombs = numberBoard[row][col];
					if (aroundBombs == 0) {
						board = clear(board, numberBoard, row, col);
					} else if (aroundBombs == -1) {
						board[row][col] = "X";
						printBoard(board);
						board = gameOver();
						boom = true;
					} else {
						board[row][col] = "" + aroundBombs;
					}
					if (justBombs(board)) {
						winner = true;
						printBoard(board);
						board = winScreen();
					}
				} else {
					if (board[row][col].equals("-")) {
						board[row][col] = ">";
					} else {
						invalid = true;
					}
				}
				printBoard(board);
			} while (invalid);
			printBoard(board);
		}
	}

	public static int findAroundBombs(int[][] bombBoard, int row, int col) {
		int sum = 0;
		if (bombBoard[row][col] == 0) {
			boolean colLeft = false;
			boolean colRight = false;
			if (col - 1 >= 0) {
				colLeft = true;
				sum += bombBoard[row][col - 1];
			}
			if (col + 1 <= 8) {
				colRight = true;
				sum += bombBoard[row][col + 1];
			}
			if (row - 1 >= 0) {
				sum += bombBoard[row - 1][col];
				if (colLeft) sum += bombBoard[row - 1][col - 1];
				if (colRight) sum += bombBoard[row - 1][col + 1];
			}
			if (row + 1 <= 8) {
				sum += bombBoard[row + 1][col];
				if (colLeft) sum += bombBoard[row + 1][col - 1];
				if (colRight) sum += bombBoard[row + 1][col + 1];
			}
		} else {
			sum = -1;
		}
		return sum;
	}

	public static String[][] gameOver() {
		String[][] gameOverBoard = {{ "-", "-", "B", "0", "0", "M", "!", "-", "-" },
									{ "-", "-", "-", "-", "-", "-", "-", "-", "-" }, 
									{ "-", "-", "X", "-", "-", "-", "X", "-", "-" },
									{ "-", "-", "-", "-", "-", "-", "-", "-", "-" }, 
									{ "-", "-", "-", "-", "-", "-", "-", "-", "-" },
									{ "-", "-", "_", "=", "=", "=", "_", "-", "-" }, 
									{ "-", "/", "-", "-", "-", "-", "-", "\\", "-" },
									{ "-", "-", "-", "-", "-", "-", "-", "-", "-" }, 
									{ "-", "-", "-", "-", "-", "-", "-", "-", "-" }};
		return gameOverBoard;
	}

	public static String[][] winScreen() {
		String[][] winBoard = {{ "-", "W", "I", "N", "N", "E", "R", "!", "-" },
							   { "-", "-", "-", "-", "-", "-", "-", "-", "-" }, 
							   { "-", "-", "$", "-", "-", "-", "$", "-", "-" },
							   { "-", "-", "-", "-", "-", "-", "-", "-", "-" }, 
							   { "-", "-", "-", "-", "-", "-", "-", "-", "-" },
							   { "-", "\\", "-", "-", "-", "-", "-", "/", "-" }, 
							   { "-", "-", "*", "=", "=", "=", "*", "-", "-" },
							   { "-", "-", "-", "-", "-", "-", "-", "-", "-" }, 
							   { "-", "-", "-", "-", "-", "-", "-", "-", "-" }};
		return winBoard;
	}

	public static boolean justBombs(String[][] board) {
		int numDashes = 0;
		int numFlags = 0;
		for (String[] row : board) {
			for (String col : row) {
				if (col.equals("-")) numDashes++;
				if (col.equals(">")) numFlags++;
			}
		}
		if (numDashes + numFlags <= 10) return true;
		return false;
	}

	public static String[][] clear(String[][] board, int[][] numberBoard, int row, int col) {
		board[row][col] = " ";
		board = revealAround(board, numberBoard, row, col);
		//printBoard(board);
		List<int[]> coordinates = findZeros(board);
		while (!coordinates.isEmpty()) {
			for (int[] coordinate : coordinates) {
				board = revealAround(board, numberBoard, coordinate[0], coordinate[1]);
			}
			//printBoard(board);
			coordinates = findZeros(board);
		}
		return board;
	}

	public static String[][] revealAround(String[][] board, int[][] numberBoard, int row, int col) {
		boolean colLeft = false;
		boolean colRight = false;
		if (col - 1 >= 0) {
			colLeft = true;
			if (!board[row][col - 1].equals(" ")) board[row][col - 1] = "" + numberBoard[row][col - 1];
		}
		if (col + 1 <= 8) {
			colRight = true;
			if (!board[row][col + 1].equals(" ")) board[row][col + 1] = "" + numberBoard[row][col + 1];
		}
		if (row - 1 >= 0) {
			if (!board[row - 1][col].equals(" ")) board[row - 1][col] = "" + numberBoard[row - 1][col];
			if (colLeft && !board[row - 1][col - 1].equals(" ") && numberBoard[row - 1][col - 1] != 0) board[row - 1][col - 1] = "" + numberBoard[row - 1][col - 1];
			if (colRight && !board[row - 1][col + 1].equals(" ") && numberBoard[row - 1][col + 1] != 0) board[row - 1][col + 1] = "" + numberBoard[row - 1][col + 1];
		}
		if (row + 1 <= 8) {
			if (!board[row + 1][col].equals(" ")) board[row + 1][col] = "" + numberBoard[row + 1][col];
			if (colLeft && !board[row + 1][col - 1].equals(" ") && numberBoard[row + 1][col - 1] != 0) board[row + 1][col - 1] = "" + numberBoard[row + 1][col - 1];
			if (colRight && !board[row + 1][col + 1].equals(" ") && numberBoard[row + 1][col + 1] != 0) board[row + 1][col + 1] = "" + numberBoard[row + 1][col + 1];
		}
		return board;
	}
	
	public static List<int[]> findZeros(String[][] board) {
		List<int[]> coordinates = new ArrayList<int[]>();
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				if (board[row][col].equals("0")) {
					board[row][col] = " ";
					int[] coordinate = {row, col};
					coordinates.add(coordinate);
				}
			}
		}
		return coordinates;
	}

	public static void printBoard(int[][] board) {
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				if (board[row][col] == -1) {
					System.out.print(board[row][col]);
				} else {
					System.out.print(" " + board[row][col]);
				}
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void printBoard(String[][] board) {
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				System.out.print(" " + board[row][col]);
			}
			System.out.println();
		}
		System.out.println();
	}
}