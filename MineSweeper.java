import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class MineSweeper extends JFrame{
	
	JPanel boardPanel = new JPanel();
	JPanel northPanel = new JPanel();
	JLabel mineLabel = new JLabel("Number of mines: ");
	static JTextField mine = new JTextField(8);
	JLabel timerLabel = new JLabel("Time: ");
	static JTextField timerField = new JTextField(8);
	JButton resetButton = new JButton("Reset Game");
	
	static Timer gameTimer;
	static int elapsedSeconds = 0;
	static boolean gameStarted = false;
	static boolean gameEnded = false;
	
	{
		mine.setEditable(false);
		mine.setFocusable(false);
		timerField.setEditable(false);
		timerField.setFocusable(false);
		timerField.setText("00:00");
	}
	
	static String str = getBoardSize();
	static int num = Integer.parseInt(str);
	static Square[][] board = new Square[num+2][num+2];
	static int totalMines = (int)(num*num*0.15);
	static int mineCounter = 0;
	
	public static String getBoardSize() {
		while(true) {
			String input = JOptionPane.showInputDialog(null, 
				"Enter the size (N) of the board (5-20 recommended):\n" +
				"This will create a square grid of NxN tiles.",
				"MineSweeper - Board Size",
				JOptionPane.QUESTION_MESSAGE);
			
			if(input == null) {
				System.exit(0);
			}
			
			try {
				int size = Integer.parseInt(input.trim());
				if(size < 3) {
					JOptionPane.showMessageDialog(null, 
						"Board size too small! Please enter a number of at least 3.",
						"Invalid Input",
						JOptionPane.ERROR_MESSAGE);
				} else if(size > 50) {
					JOptionPane.showMessageDialog(null, 
						"Board size too large! Please enter a number no greater than 50.",
						"Invalid Input",
						JOptionPane.ERROR_MESSAGE);
				} else {
					return input.trim();
				}
			} catch(NumberFormatException e) {
				JOptionPane.showMessageDialog(null, 
					"Invalid input! Please enter a valid number between 3 and 50.",
					"Invalid Input",
					JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public static void startTimer() {
		if(!gameStarted && !gameEnded) {
			gameStarted = true;
			elapsedSeconds = 0;
			gameTimer = new Timer(1000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(!gameEnded) {
						elapsedSeconds++;
						updateTimerDisplay();
					}
				}
			});
			gameTimer.start();
		}
	}
	
	public static void stopTimer() {
		gameEnded = true;
		if(gameTimer != null) {
			gameTimer.stop();
		}
	}
	
	public static void updateTimerDisplay() {
		int minutes = elapsedSeconds / 60;
		int seconds = elapsedSeconds % 60;
		timerField.setText(String.format("%02d:%02d", minutes, seconds));
	}
	
	public static void lose() {
		stopTimer();
		
		for(int r = 1; r < board.length-1; r++) {
			for(int c = 1; c < board.length-1; c++) {
				if(board[r][c].mine) {
					board[r][c].setText("X");
					board[r][c].setBackground(Color.red);
					board[r][c].setOpaque(true);
					board[r][c].setBorderPainted(false);
				}
			}
		}
		
		Object[] options = {"Yes (same size)", "Yes (new size)", "No"};
		int response = JOptionPane.showOptionDialog(null,
			"You lost!!! Time: " + timerField.getText() + "\nPlay again?",
			"Game Over",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[0]);
		
		if(response == 0) {  // Yes (same size)
			JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(board[1][1]);
			topFrame.dispose();
			
			mineCounter = 0;
			gameStarted = false;
			gameEnded = false;
			elapsedSeconds = 0;
			// Keep same size - don't ask for new size
			board = new Square[num+2][num+2];
			totalMines = (int)(num*num*0.15);
			new MineSweeper();
		} else if(response == 1) {  // Yes (new size)
			JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(board[1][1]);
			topFrame.dispose();
			
			mineCounter = 0;
			gameStarted = false;
			gameEnded = false;
			elapsedSeconds = 0;
			str = getBoardSize();
			num = Integer.parseInt(str);
			board = new Square[num+2][num+2];
			totalMines = (int)(num*num*0.15);
			new MineSweeper();
		}
		// If response == 2 or CLOSED_OPTION, do nothing (no)
	}
	
	public static void flag(int r, int c) {
		if(!board[r][c].flagged) {
			board[r][c].flagged = true;
			board[r][c].setBackground(Color.orange);
			board[r][c].setText("|>");
			board[r][c].setOpaque(true);
			board[r][c].setBorderPainted(false);
			mineCounter--;
			mine.setText("" + mineCounter);
		} else {
			board[r][c].flagged = false;
			board[r][c].setBackground(null);
			board[r][c].setText("");
			board[r][c].setOpaque(false);
			board[r][c].setBorderPainted(true);
			mineCounter++;
			mine.setText("" + mineCounter);
		}
	}
	
	public static void win() {
		int count = 0;
		int totalTiles = (num*num) - totalMines;
		
		for(int r = 1; r < board.length-1; r++) {
			for(int c = 1; c < board.length-1; c++) {
				if(!board[r][c].mine && board[r][c].exposed) {
					count++;
				}
			}
		}
		if(count == totalTiles) {
			stopTimer();
			
			Object[] options = {"Yes (same size)", "Yes (new size)", "No"};
			int response = JOptionPane.showOptionDialog(null,
				"YOU WIN!!! Time: " + timerField.getText() + "\nPlay again?",
				"Victory!",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]);
			
			if(response == 0) {  // Yes (same size)
				JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(board[1][1]);
				topFrame.dispose();
				
				mineCounter = 0;
				gameStarted = false;
				gameEnded = false;
				elapsedSeconds = 0;
				// Keep same size - don't ask for new size
				board = new Square[num+2][num+2];
				totalMines = (int)(num*num*0.15);
				new MineSweeper();
			} else if(response == 1) {  // Yes (new size)
				JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(board[1][1]);
				topFrame.dispose();
				
				mineCounter = 0;
				gameStarted = false;
				gameEnded = false;
				elapsedSeconds = 0;
				str = getBoardSize();
				num = Integer.parseInt(str);
				board = new Square[num+2][num+2];
				totalMines = (int)(num*num*0.15);
				new MineSweeper();
			}
			// If response == 2 or CLOSED_OPTION, do nothing (no)
		}
	}
	
	public static void expose(int r, int c) {
		if(board[r][c].mine) return;
		if(r == 0 || c == 0) return;
		if(r == board.length-1 || c == board.length-1) return;
		if(board[r][c].exposed) return; 
		
		if(!board[r][c].mine) {
			board[r][c].exposed = true;
			board[r][c].setOpaque(true);
			board[r][c].setBorderPainted(false);
			
			if(board[r][c].mineCount != 0) {
				board[r][c].setBackground(Color.lightGray);
				board[r][c].setText("" + board[r][c].mineCount);
				board[r][c].setForeground(getNumberColor(board[r][c].mineCount));
				return;
			} else {
				board[r][c].setBackground(Color.cyan);
			}
		}
		
		expose(r,c-1);
		expose(r,c+1);
		
		expose(r-1,c-1);
		expose(r-1,c);
		expose(r-1,c+1);
		
		expose(r+1,c-1);
		expose(r+1,c);
		expose(r+1,c+1);
	}
	
	public static Color getNumberColor(int count) {
		switch(count) {
			case 1: return Color.blue;
			case 2: return new Color(0, 128, 0);
			case 3: return Color.red;
			case 4: return new Color(0, 0, 128);
			case 5: return new Color(128, 0, 0);
			case 6: return new Color(0, 128, 128);
			case 7: return Color.black;
			case 8: return Color.gray;
			default: return Color.black;
		}
	}
	
	public MineSweeper() {
		int r,c;
		setTitle("MineSweeper");
		setSize(700,700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		boardPanel.setLayout(new GridLayout(num,num));
		add(boardPanel, BorderLayout.CENTER);
		
		northPanel.add(mineLabel);
		northPanel.add(mine);
		northPanel.add(timerLabel);
		northPanel.add(timerField);
		northPanel.add(resetButton);
		add(northPanel, BorderLayout.NORTH);
		
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopTimer();
				dispose();
				mineCounter = 0;
				gameStarted = false;
				gameEnded = false;
				elapsedSeconds = 0;
				str = getBoardSize();
				num = Integer.parseInt(str);
				board = new Square[num+2][num+2];
				totalMines = (int)(num*num*0.15);
				new MineSweeper();
			}
		});
		
		for(r = 0; r < board.length; r++) {
			for(c = 0; c < board.length; c++) {
				board[r][c] = new Square(r,c);
			}
		}
		
		for(r = 1; r < board.length-1; r++) {
			for(c = 1; c < board.length-1; c++) {
				boardPanel.add(board[r][c]);
				if(Math.random()<0.15 && mineCounter < totalMines) {
					board[r][c].mine = true;
					mineCounter++;
				}
			}
		}
		
		mine.setText("" + mineCounter);
		
		for(r = 1; r < board.length-1; r++) {
			for(c = 1; c < board.length-1; c++) {
				for(int x = (r-1); x<=r+1; x++) {
					for(int y = (c-1); y<=c+1; y++) {
						if(board[x][y].mine) board[r][c].mineCount++;
					}
				}
			}
		}
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new MineSweeper();
	}
}