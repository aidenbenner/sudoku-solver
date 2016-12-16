import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JFrame;

public class Sudoku {
	//setup everything we need for later 
	int[][] boardStart = new int[9][9];
	int[][] board = new int[9][9];
	static boolean show = true;
	public static SudokuPanel sp = new SudokuPanel(9, 9);
	public static BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
	public static JFrame jf = new JFrame();
	public static void main(String args[]) throws InterruptedException, IOException {
		int[][] solvedBoard = new int[9][9];
		System.out
				.println("Enter a sudoku board to be solved or type 'archive' to get archived boards");
		System.out.println("Grids through entered through the console consist of 9 lines of 9 digits each between 0-9");
		System.out.println("A 0 signifies a grid that hasn't been filled");
		System.out.println("Example Grid :");
		System.out.println("000900002" + "\n" +
		"050123400" +"\n" +
		"030000160" +"\n" + 
		"908000000" +"\n" + 
		"070000090" +"\n" + 
		"000000205" +"\n" + 
		"091000050" +"\n" + 
		"007439020" +"\n" + 
		"400007000" +"\n");
		System.out
		.println("Enter a sudoku board to be solved or type 'archive' to get archived boards");
		JFrame jf = new JFrame();
		jf.add(sp);
		jf.setSize(470, 490);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		boolean exit = false;
		while (!exit) {
			String input = null;
			//parse our data, check if the user wants to exit 
			for (int i = 0; i < 9; i++) {
				try {
					input = bf.readLine();
				} catch (Exception e) {
					continue;
				}
				if (input.equals("exit")) {
					System.exit(0);
				}
				if(input.equals("archive")){
					jf.toFront();
					jf.setState(Frame.NORMAL);
					archiveMenu();
					System.out
					.println("Enter a sudoku board to be solved or type 'archive' to get archived boards or 'exit' to quit");
					i--;
					continue;
				}
				if (input.length() != 9) {
					System.out.println("please only enter lines of 9 digits ");
				}
				for (int k = 0; k < input.length(); k++) {
					if (!Character.isDigit(input.charAt(k))) {
						System.out
								.println("Something went wrong, make sure you entered digits only");
					}
					solvedBoard[i][k] = Character.getNumericValue((input
							.charAt(k)));
				}
			}
			// System.out.println(accept(solvedBoard));
			System.out
					.println("Press any key to solve, type hide to hide steps");
			Scanner s = new Scanner(System.in);
			printArray(solvedBoard);
			int[][] startState = new int[9][9];
			for (int i = 0; i < startState.length; i++){
				startState[i] = Arrays.copyOf(solvedBoard[i], solvedBoard[i].length);
			}
			if (s.next().equals("hide")) {
				show = false;
			}
			
			diffCounter = 0;
			jf.toFront();
			jf.setState(Frame.NORMAL);
			//solve! 
			if (solve(solvedBoard)) {
				saveBoard(diffCounter, startState);
				System.out.println("This board can be solved!");
			} else {
				System.out.println("This board cannot be solved");
			}
			sortArchive();
			sp.displayBoard(solvedBoard);
			System.out.println("Enter the next board or \"exit\" to quit or type 'archive' to see archived boards");
		}
	}

	public static void archiveMenu(){
		System.out.println("ARCHIVE : ");
		System.out.println("Puzzle #: Hash \t Difficulty");
		try{
			sortArchive();
			BufferedReader r = new BufferedReader(new FileReader(
					"src/PuzzleArchive.txt"));
			Board b = getNextBoard(r);
			LinkedList<Board> boards = new LinkedList<Board>();
			Set<Integer> hashes = new HashSet<Integer>();
			while(b != null){
				if(!hashes.contains(b.hash)){
				boards.add( b );
				hashes.add(b.hash);
				//System.out.println("Found duplicate");
				}
				b = getNextBoard(r);
			}
			int counter = 1;
			for(Board z : boards){
				System.out.println(counter + ": " + z.hash + "\t" + z.diff);
				counter++;
			}
			System.out.println("Enter the puzzle number to view in window or type exit to quit");
			String input = bf.readLine();
			if(input.equals("exit")){
				return;
			}

			else{
				int ind = Integer.parseInt(input);
				if(ind > 0 && ind <= boards.size()){
					printArray(boards.get(ind - 1).data);
					System.out.println("Solve or continue : ");
					input = bf.readLine();
					jf.toFront();
					jf.setState(Frame.NORMAL);
					if(input.equals("solve")){
						solve(boards.get(ind - 1).data);
					}
				}
				else{
					System.out.println("The puzzle index you've entered was out of bounds, please try again");
				}
				archiveMenu();
			}
		}
		catch(Exception e){
			
		}
	}
	
	
	public static void saveBoard(int diff, int[][] board) {
		//System.out.println("SAVE BOARD CALLED");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					"src/PuzzleArchive.txt", true));
			int hash = Arrays.deepHashCode(board);
			bw.write(hash + " " + diff + "\n");
			for (int i = 0; i < board.length; i++) {
				for (int k = 0; k < board.length; k++) {
					bw.write(board[i][k] + "");
				}
				bw.write("\n");
			}
			bw.flush();
			bw.close();
		
		} catch (IOException e) {
			System.out.println("error reading from puzzle archive");
		}
	}

	static class Board {
		public int[][] data;
		public int hash;
		public int diff;
	}

	public static void sortArchive() throws IOException {
		// puzzles are stored in archive as 10 line chunks, the first has hash
		// and diff
		//System.out.println("CALLED SORT");
		BufferedReader r = new BufferedReader(new FileReader(
				"src/PuzzleArchive.txt"));
		Board b = getNextBoard(r);
		LinkedList<Board> boards = new LinkedList<Board>();
		Set<Integer> hashes = new HashSet<Integer>();
		while(b != null){
			if(!hashes.contains(b.hash)){
			boards.add( b );
			hashes.add(b.hash);
			//System.out.println("Found duplicate");
			}
			b = getNextBoard(r);
		}
		sortArchive(boards);
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				"src/PuzzleArchive.txt"));
		bw.write("");
		bw.close();
		for(Board z : boards){
			saveBoard(z.diff, z.data);
		}
	}
	//sort a linked list of boards 
	public static void sortArchive(LinkedList<Board> b){
		boolean sorted = false;
		while(!sorted){
			boolean changesMade = false;
			for(int i = 0; i<b.size()-1; i++){
				if(b.get(i).diff < b.get(i + 1).diff){
					Board temp = b.get(i);
					b.set(i, b.get(i+1));
					b.set(i+1, temp);
					changesMade = true;
				}
			}
			if(!changesMade){
				sorted = true;
			}
		}
		return;
	}

	public static Board getNextBoard(BufferedReader r) {
		try {
			String[] input = r.readLine().split(" ");
			Board curr = new Board();
			curr.hash = Integer.parseInt(input[0]);
			curr.diff = Integer.parseInt(input[1]);
			curr.data = new int[9][9];
			for (int i = 0; i < 9; i++) {
				String in = r.readLine();
				for (int k = 0; k < 9; k++) {
					if (!Character.isDigit(in.charAt(k))) {
						System.out
								.println("Something went wrong, make sure you entered digits only");
					}
					curr.data[i][k] = Character.getNumericValue((in.charAt(k)));
				}
			}
			return curr;
		} catch (Exception e) {
			return null;
		}
	}

	public static int diffCounter = 0;
	public static boolean solve(int[][] startState) {
		if (!reject(startState)) {
			// System.out.println("skipping");
			return false;
		}
		boolean filled = true;
		// System.out.println("called solve");
		int row = 0;
		int col = 0;
		for (int i = 0; i < 9; i++) {
			for (int k = 0; k < 9; k++) {
				if (startState[i][k] == 0) {
					row = i;
					col = k;
					filled = false;
					break;
				}
			}
		}
		int[][] initial = Arrays.copyOf(startState, startState.length);
		if (!filled) {
			for (int i = 0; i < 9; i++) {
				// System.out.println(row + "  " + col + " " + (i + 1));
				diffCounter++;
				startState[row][col] = i + 1;
				try {
					printArray(startState);
				} catch (Exception e) {

				}
				if (solve(startState)) {
					return true;
				}
				startState[row][col] = 0;
				startState = Arrays.copyOf(initial, startState.length);
			}
			startState = Arrays.copyOf(initial, startState.length);
			return false;
		} else {
			// System.out.println("Checking");
			if (accept(startState)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static boolean reject(int[][] b) {
		// checks if board is solved
		// each box, each line and each column contain numbers 1-9
		// rows,
		for (int i = 0; i < 9; i++) {
			boolean[] data = new boolean[9];
			Arrays.fill(data, false);
			// row check
			for (int k = 0; k < b[i].length; k++) {

				if (b[i][k] == 0) {
					continue;
				}

				// System.out.println(k + " " + i);
				if (data[b[i][k] - 1]) {
					// duplicate, return false
					return false;
				} else {
					data[b[i][k] - 1] = true;
				}
			}
		}

		// column check
		for (int i = 0; i < 9; i++) {
			boolean[] data = new boolean[9];
			Arrays.fill(data, false);
			// row check
			for (int k = 0; k < b[i].length; k++) {
				if (b[k][i] == 0) {
					continue;
				}
				if (data[b[k][i] - 1]) {
					// duplicate, return false
					return false;
				} else {
					data[b[k][i] - 1] = true;
				}
			}
		}

		// box check
		// rows

		for (int j = 0; j < 9; j++) {
			boolean[] data = new boolean[9];
			Arrays.fill(data, false);
			for (int i = 0; i < 3; i++) {

				for (int k = 0; k < 3; k++) {
					// System.out.println(i + (3 * (j/3)) + " " + (3 * (j%3) +
					// k) + b[i + (3 * (j/3))][3 * (j%3) + k]);

					if (b[i + (3 * (j / 3))][3 * (j % 3) + k] == 0) {
						continue;
					}

					for (int l = 0; l < data.length; l++) {
						// System.out.print(l + ":" + data[l] + ", ");
					}
					// System.out.println();
					if (data[b[i + (3 * (j / 3))][3 * (j % 3) + k] - 1]) {
						// duplicate, return false
						return false;
					} else {
						data[b[i + (3 * (j / 3))][3 * (j % 3) + k] - 1] = true;
					}
				}
			}
		}
		return true;
	}

	public static boolean accept(int[][] b) {
		// checks if board is solved
		// each box, each line and each column contain numbers 1-9
		// rows,
		for (int i = 0; i < 9; i++) {
			boolean[] data = new boolean[9];
			Arrays.fill(data, false);
			// row check
			for (int k = 0; k < b[i].length; k++) {
				// System.out.println(k + " " + i);
				if (data[b[i][k] - 1]) {
					// duplicate, return false
					return false;
				} else {
					data[b[i][k] - 1] = true;
				}
			}
		}

		// column check
		for (int i = 0; i < 9; i++) {
			boolean[] data = new boolean[9];
			Arrays.fill(data, false);
			// row check
			for (int k = 0; k < b[i].length; k++) {
				if (data[b[k][i] - 1]) {
					// duplicate, return false
					return false;
				} else {
					data[b[k][i] - 1] = true;
				}
			}
		}

		// box check
		// rows

		for (int j = 0; j < 9; j++) {
			boolean[] data = new boolean[9];
			Arrays.fill(data, false);
			for (int i = 0; i < 3; i++) {

				for (int k = 0; k < 3; k++) {
					// System.out.println(i + (3 * (j/3)) + " " + (3 * (j%3) +
					// k) + b[i + (3 * (j/3))][3 * (j%3) + k]);

					for (int l = 0; l < data.length; l++) {
						// System.out.print(l + ":" + data[l] + ", ");
					}
					// System.out.println();
					if (data[b[i + (3 * (j / 3))][3 * (j % 3) + k] - 1]) {
						// duplicate, return false
						return false;
					} else {
						data[b[i + (3 * (j / 3))][3 * (j % 3) + k] - 1] = true;
					}
				}
			}
		}
		return true;
	}

	public static void printArray(int[][] a) throws InterruptedException {
		if (show) {
			sp.displayBoard(a);
			Thread.sleep(1);
		}
	}
}