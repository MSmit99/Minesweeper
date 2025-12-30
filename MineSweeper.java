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
	
	static String str = JOptionPane.showInputDialog("Enter the size of the board: ");
	static int num = Integer.parseInt(str);
	static Square[][] board = new Square[num+2][num+2];
	static int totalMines = (int)(num*num*0.15);
	static int mineCounter = 0;
	
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
		
		JOptionPane.showMessageDialog(null, "You lost!!! The game is over!!! Wanna play again?");
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
			JOptionPane.showMessageDialog(null, "YOU WIN!!!");
			new MineSweeper();
		}
	}
	
	public static void expose(int r, int c) {
		if(board[r][c].mine) return;
		if(r == 0 || c == 0) return;
		if(r == board.length-1 || c == board.length-1) return;
		if(board[r][c].exposed) return; 
		
		if(!board[r][c].mine) {
			board[r][c].exposed = true;
			board[r][c].setBackground(Color.cyan);
			board[r][c].setOpaque(true);
			board[r][c].setBorderPainted(false);
			
			if(board[r][c].mineCount != 0) {
				board[r][c].setText("" + board[r][c].mineCount);
				return;
			}
		}
		
		expose(r,c-1);
		expose(r,c+1);
		
		expose(r-1,c-1);
		expose(r-1,c+1);
		
		expose(r-1,c-1);
		expose(r-1,c+1);
		
		expose(r+1,c-1);
		expose(r+1,c+1);
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
		add(northPanel, BorderLayout.NORTH);
		
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