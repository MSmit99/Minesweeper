import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class MineSweeper extends JFrame{
	
	JPanel boardPanel = new JPanel();
	JPanel northPanel = new JPanel();
	JPanel centerPanel = new JPanel();
	CardLayout cardLayout = new CardLayout();
	JPanel pausePanel = new JPanel();
	
	JLabel mineLabel = new JLabel("Mines: ");
	static JTextField mine = new JTextField(5);
	JLabel timerLabel = new JLabel("Time: ");
	static JTextField timerField = new JTextField(5);
	JButton resetButton = new JButton("Reset");
	JButton pauseButton = new JButton("Pause");
	JButton helpButton = new JButton("Help");
	
	static Timer gameTimer;
	static int elapsedSeconds = 0;
	static boolean gameStarted = false;
	static boolean gameEnded = false;
	static boolean isPaused = false;
	static boolean minesGenerated = false;
	
	static String difficulty;
	static int num;
	static int totalMines;
	static Square[][] board;
	static int flagCount = 0;
	
	{
		mine.setEditable(false);
		mine.setFocusable(false);
		mine.setPreferredSize(new Dimension(50, 25));
		
		timerField.setEditable(false);
		timerField.setFocusable(false);
		timerField.setText("00:00");
		timerField.setPreferredSize(new Dimension(55, 25));
		
		// Style buttons with proper sizes
		resetButton.setFont(new Font("Arial", Font.BOLD, 12));
		resetButton.setPreferredSize(new Dimension(80, 30));
		resetButton.setFocusPainted(false);
		
		pauseButton.setFont(new Font("Arial", Font.BOLD, 12));
		pauseButton.setPreferredSize(new Dimension(80, 30));
		pauseButton.setFocusPainted(false);
		
		helpButton.setFont(new Font("Arial", Font.BOLD, 12));
		helpButton.setPreferredSize(new Dimension(70, 30));
		helpButton.setFocusPainted(false);
	}
	
	public static class GameSettings {
		String difficulty;
		int size;
		int mines;
		
		public GameSettings(String diff, int s, int m) {
			difficulty = diff;
			size = s;
			mines = m;
		}
	}
	
	public static void showHelp() {
		String helpText = "<html><body style='width: 320px; padding: 10px;'>" +
			"<h2>How to Play MineSweeper</h2>" +
			"<p><b>Goal:</b> Clear all non-mine squares without hitting a mine!</p>" +
			"<br><p><b>Controls:</b></p>" +
			"<ul>" +
			"<li><b>Left Click:</b> Reveal a square</li>" +
			"<li><b>Right Click:</b> Place/remove a flag</li>" +
			"<li><b>Middle Click (or Left+Right):</b> Chord click - reveal all surrounding squares if correct number of flags are placed</li>" +
			"</ul>" +
			"<br><p><b>Numbers:</b> Show how many mines are in the 8 surrounding squares</p>" +
			"<br><p><b>Buttons:</b></p>" +
			"<ul>" +
			"<li><b>Reset Button:</b> Restart with new difficulty</li>" +
			"<li><b>Pause Button:</b> Pause/resume the game</li>" +
			"<li><b>Help Button:</b> Show this help dialog</li>" +
			"</ul>" +
			"<br><p><b>Tips:</b></p>" +
			"<ul>" +
			"<li>First click is always safe!</li>" +
			"<li>Use flags to mark suspected mines</li>" +
			"<li>Empty squares auto-reveal neighbors</li>" +
			"<li>Chord clicking speeds up gameplay!</li>" +
			"</ul>" +
			"</body></html>";
		
		JOptionPane.showMessageDialog(null, 
			helpText,
			"MineSweeper Help", 
			JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static GameSettings getDifficulty() {
		Object[] options = {"Easy (9x9, 10 mines)", "Medium (16x16, 40 mines)", 
							"Hard (30x30, 99 mines)", "Custom"};
		int choice = JOptionPane.showOptionDialog(null,
			"Select Difficulty Level:",
			"MineSweeper - Difficulty",
			JOptionPane.DEFAULT_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[1]);
		
		if(choice == -1) {
			System.exit(0);
		}
		
		switch(choice) {
			case 0:
				return new GameSettings("Easy", 9, 10);
			case 1:
				return new GameSettings("Medium", 16, 40);
			case 2:
				return new GameSettings("Hard", 30, 99);
			case 3:
				return getCustomSettings();
			default:
				return new GameSettings("Medium", 16, 40);
		}
	}
	
	public static GameSettings getCustomSettings() {
		while(true) {
			String sizeInput = JOptionPane.showInputDialog(null,
				"Enter board size (5-50):",
				"Custom Size",
				JOptionPane.QUESTION_MESSAGE);
			
			if(sizeInput == null) {
				System.exit(0);
			}
			
			try {
				int size = Integer.parseInt(sizeInput.trim());
				if(size < 5 || size > 50) {
					JOptionPane.showMessageDialog(null,
						"Size must be between 5 and 50!",
						"Invalid Input",
						JOptionPane.ERROR_MESSAGE);
					continue;
				}
				
				String mineInput = JOptionPane.showInputDialog(null,
					"Enter number of mines (1-" + (size*size - 10) + "):",
					"Custom Mines",
					JOptionPane.QUESTION_MESSAGE);
				
				if(mineInput == null) {
					System.exit(0);
				}
				
				int mines = Integer.parseInt(mineInput.trim());
				int maxMines = size * size - 10;
				if(mines < 1 || mines > maxMines) {
					JOptionPane.showMessageDialog(null,
						"Mines must be between 1 and " + maxMines + "!",
						"Invalid Input",
						JOptionPane.ERROR_MESSAGE);
					continue;
				}
				
				return new GameSettings("Custom", size, mines);
				
			} catch(NumberFormatException e) {
				JOptionPane.showMessageDialog(null,
					"Please enter valid numbers!",
					"Invalid Input",
					JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public static void updateMineCounter() {
		int remaining = totalMines - flagCount;
		mine.setText("" + remaining);
	}
	
	public static void startTimer() {
		if(!gameStarted && !gameEnded) {
			gameStarted = true;
			elapsedSeconds = 0;
			gameTimer = new Timer(1000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(!gameEnded && !isPaused) {
						elapsedSeconds++;
						updateTimerDisplay();
					}
				}
			});
			gameTimer.start();
		}
	}
	
	public static void pauseTimer() {
		isPaused = true;
	}
	
	public static void resumeTimer() {
		isPaused = false;
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
	
	public static void generateMines(int avoidRow, int avoidCol) {
		minesGenerated = true;
		
		// Clear any existing mines
		for(int r = 1; r < board.length-1; r++) {
			for(int c = 1; c < board.length-1; c++) {
				board[r][c].mine = false;
				board[r][c].mineCount = 0;
			}
		}
		
		// Generate mines avoiding first click and surrounding area
		int minesPlaced = 0;
		while(minesPlaced < totalMines) {
			int r = (int)(Math.random() * (board.length - 2)) + 1;
			int c = (int)(Math.random() * (board.length - 2)) + 1;
			
			// Don't place mine if it's in the 3x3 area around first click
			if(Math.abs(r - avoidRow) <= 1 && Math.abs(c - avoidCol) <= 1) {
				continue;
			}
			
			// Don't place mine if there's already one there
			if(!board[r][c].mine) {
				board[r][c].mine = true;
				minesPlaced++;
			}
		}
		
		// Calculate mine counts
		for(int r = 1; r < board.length-1; r++) {
			for(int c = 1; c < board.length-1; c++) {
				for(int x = (r-1); x<=r+1; x++) {
					for(int y = (c-1); y<=c+1; y++) {
						if(board[x][y].mine) board[r][c].mineCount++;
					}
				}
			}
		}
	}
	
	public static void lose() {
		stopTimer();
		
		for(int r = 1; r < board.length-1; r++) {
			for(int c = 1; c < board.length-1; c++) {
				if(board[r][c].mine) {
					board[r][c].setFont(new Font("Arial", Font.BOLD, 12));
					board[r][c].setText("X");
					board[r][c].setBackground(Color.red);
					board[r][c].setOpaque(true);
					board[r][c].setBorderPainted(false);
				}
			}
		}
		
		Object[] options = {"Yes (same difficulty)", "Yes (new difficulty)", "No"};
		int response = JOptionPane.showOptionDialog(null,
			"You lost!!! Time: " + timerField.getText() + "\nPlay again?",
			"Game Over",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[0]);
		
		if(response == 0) {
			JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(board[1][1]);
			topFrame.dispose();
			resetGame(false);
		} else if(response == 1) {
			JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(board[1][1]);
			topFrame.dispose();
			resetGame(true);
		}
	}
	
	public static void flag(int r, int c) {
		if(!board[r][c].flagged) {

			if(flagCount >= totalMines) return;

			board[r][c].flagged = true;
			board[r][c].setBackground(Color.orange);
			board[r][c].setText("F");
			board[r][c].setOpaque(true);
			board[r][c].setBorderPainted(false);
			flagCount++;
			updateMineCounter();
		} else {
			board[r][c].flagged = false;
			board[r][c].setBackground(null);
			board[r][c].setText("");
			board[r][c].setOpaque(false);
			board[r][c].setBorderPainted(true);
			flagCount--;
			updateMineCounter();
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
			
			Object[] options = {"Yes (same difficulty)", "Yes (new difficulty)", "No"};
			int response = JOptionPane.showOptionDialog(null,
				"YOU WIN!!! Time: " + timerField.getText() + "\nPlay again?",
				"Victory!",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]);
			
			if(response == 0) {
				JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(board[1][1]);
				topFrame.dispose();
				resetGame(false);
			} else if(response == 1) {
				JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(board[1][1]);
				topFrame.dispose();
				resetGame(true);
			}
		}
	}
	
	public static void resetGame(boolean newDifficulty) {
		flagCount = 0;
		gameStarted = false;
		gameEnded = false;
		elapsedSeconds = 0;
		minesGenerated = false;
		isPaused = false;
		
		if(newDifficulty) {
			GameSettings settings = getDifficulty();
			difficulty = settings.difficulty;
			num = settings.size;
			totalMines = settings.mines;
		}
		
		board = new Square[num+2][num+2];
		new MineSweeper();
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
				board[r][c].setBorder(new LineBorder(Color.DARK_GRAY, 1));
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
		setTitle("MineSweeper - " + difficulty);
		setSize(700,700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		
		// Setup center panel with CardLayout
		centerPanel.setLayout(cardLayout);
		boardPanel.setLayout(new GridLayout(num,num));
		
		// Setup pause panel
		pausePanel.setLayout(new BorderLayout());
		JLabel pauseLabel = new JLabel("PAUSED", SwingConstants.CENTER);
		pauseLabel.setFont(pauseLabel.getFont().deriveFont(48f));
		pausePanel.add(pauseLabel, BorderLayout.CENTER);
		pausePanel.setBackground(Color.DARK_GRAY);
		pauseLabel.setForeground(Color.WHITE);
		
		centerPanel.add(boardPanel, "game");
		centerPanel.add(pausePanel, "pause");
		add(centerPanel, BorderLayout.CENTER);
		
		// Make sure game board is showing
		cardLayout.show(centerPanel, "game");
		
		// Setup north panel with proper spacing
		northPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));
		
		northPanel.add(mineLabel);
		northPanel.add(mine);
		northPanel.add(timerLabel);
		northPanel.add(timerField);
		northPanel.add(resetButton);
		northPanel.add(pauseButton);
		northPanel.add(helpButton);
		add(northPanel, BorderLayout.NORTH);
		
		// Ensure pause button shows correct text
		pauseButton.setText("Pause");
		
		// Help button action
		helpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showHelp();
			}
		});
		
		// Reset button action
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopTimer();
				dispose();
				resetGame(true);
			}
		});
		
		// Pause button action
		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(gameEnded) return;
				
				if(!isPaused) {
					isPaused = true;
					if(gameStarted) {
						pauseTimer();
					}
					cardLayout.show(centerPanel, "pause");
					pauseButton.setText("Resume");
				} else {
					isPaused = false;
					if(gameStarted) {
						resumeTimer();
					}
					cardLayout.show(centerPanel, "game");
					pauseButton.setText("Pause");
				}
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
			}
		}
		
		updateMineCounter();
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		GameSettings settings = getDifficulty();
		difficulty = settings.difficulty;
		num = settings.size;
		totalMines = settings.mines;
		board = new Square[num+2][num+2];
		new MineSweeper();
	}
}