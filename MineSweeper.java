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
	JButton resetButton = new JButton("Reset Game");
	
	{
		mine.setEditable(false);
		mine.setFocusable(false);
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
			
			// Check if user cancelled
			if(input == null) {
				System.exit(0);
			}
			
			// Validate input
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
	
	public static void lose() {
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
		
		int response = JOptionPane.showConfirmDialog(null, "You lost!!! The game is over!!! Play again?", "Game Over", JOptionPane.YES_NO_OPTION);
		if(response == JOptionPane.YES_OPTION) {
			// Close all existing frames
			JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(board[1][1]);
			topFrame.dispose();
			
			mineCounter = 0;
			str = getBoardSize();
			num = Integer.parseInt(str);
			board = new Square[num+2][num+2];
			totalMines = (int)(num*num*0.15);
			new MineSweeper();
		}
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
			int response = JOptionPane.showConfirmDialog(null, "YOU WIN!!! Play again?", "Victory!", JOptionPane.YES_NO_OPTION);
			if(response == JOptionPane.YES_OPTION) {
				// Close all existing frames
				JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(board[1][1]);
				topFrame.dispose();
				
				mineCounter = 0;
				str = getBoardSize();
				num = Integer.parseInt(str);
				board = new Square[num+2][num+2];
				totalMines = (int)(num*num*0.15);
				new MineSweeper();
			}
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
		
		// Expose all 8 surrounding squares
		expose(r,c-1);      // left
		expose(r,c+1);      // right
		
		expose(r-1,c-1);    // top-left
		expose(r-1,c);      // top (THIS WAS MISSING - BUG FIX)
		expose(r-1,c+1);    // top-right
		
		expose(r+1,c-1);    // bottom-left
		expose(r+1,c);      // bottom (THIS WAS MISSING - BUG FIX)
		expose(r+1,c+1);    // bottom-right
	}
	
	public static Color getNumberColor(int count) {
		switch(count) {
			case 1: return Color.blue;
			case 2: return new Color(0, 128, 0); // dark green
			case 3: return Color.red;
			case 4: return new Color(0, 0, 128); // dark blue
			case 5: return new Color(128, 0, 0); // dark red
			case 6: return new Color(0, 128, 128); // teal
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
		northPanel.add(resetButton);
		add(northPanel, BorderLayout.NORTH);
		
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				mineCounter = 0;
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
					//board[r][c].setText("X");
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