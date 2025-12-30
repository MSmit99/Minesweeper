import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Square extends JButton {
	public boolean mine = false;
	public int mineCount = 0;
	private int row, col;
	public boolean flagged = false;
	public boolean exposed = false;
	private Color originalBackground;
	private boolean leftPressed = false;
	private boolean rightPressed = false;
	
	public Square(int r, int c) {
		row = r;
		col = c;
		setFont(new Font("Arial", Font.BOLD, 18));
		originalBackground = getBackground();
		addMouseListener(new ClickMe());
	}
	
	class ClickMe implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			// Don't allow clicks while paused or game ended
			if(MineSweeper.isPaused || MineSweeper.gameEnded) return;
			
			// Generate mines on first click (avoiding this position)
			if(!MineSweeper.minesGenerated) {
				MineSweeper.generateMines(row, col);
			}
			
			// Start timer on first click
			MineSweeper.startTimer();
			
			// Middle click (chord clicking) on revealed numbers
			if(e.getButton() == 2 && exposed && mineCount > 0) {
				chordClick();
				return;
			}
			
			// Right click - flag
			if(e.getButton() == 3) {
				if(!exposed) {
					MineSweeper.flag(row, col);
				}
			}
			
			// Left click - reveal
			if(e.getButton()== 1) {
				if(flagged) return;
				
				if(mine) {
					exposed = true;
					setFont(new Font("Arial", Font.BOLD, 12));
					setText("X");
					setBackground(Color.red);
					setOpaque(true);
					setBorderPainted(false);
					MineSweeper.lose();
				} else {	
					MineSweeper.expose(row,col);
					MineSweeper.win();
				}
			}
		}
		
		// Chord clicking: reveal all surrounding squares if correct number of flags
		private void chordClick() {
			if(!exposed || mineCount == 0) return;
			
			// Count flags around this square
			int flagsAround = 0;
			for(int r = row-1; r <= row+1; r++) {
				for(int c = col-1; c <= col+1; c++) {
					if(r == 0 || c == 0) continue;
					if(r == MineSweeper.board.length-1 || c == MineSweeper.board.length-1) continue;
					if(MineSweeper.board[r][c].flagged) {
						flagsAround++;
					}
				}
			}
			
			// If flag count matches mine count, reveal all non-flagged squares
			if(flagsAround == mineCount) {
				for(int r = row-1; r <= row+1; r++) {
					for(int c = col-1; c <= col+1; c++) {
						if(r == 0 || c == 0) continue;
						if(r == MineSweeper.board.length-1 || c == MineSweeper.board.length-1) continue;
						
						Square sq = MineSweeper.board[r][c];
						if(!sq.flagged && !sq.exposed) {
							if(sq.mine) {
								// Hit a mine - incorrectly flagged!
								sq.exposed = true;
								sq.setFont(new Font("Arial", Font.BOLD, 12));
								sq.setText("X");
								sq.setBackground(Color.red);
								sq.setOpaque(true);
								sq.setBorderPainted(false);
								MineSweeper.lose();
								return;
							} else {
								MineSweeper.expose(r, c);
							}
						}
					}
				}
				MineSweeper.win();
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// Track which buttons are pressed
			if(e.getButton() == 1) leftPressed = true;
			if(e.getButton() == 3) rightPressed = true;
			
			// Visual feedback on press
			if(!MineSweeper.isPaused && !MineSweeper.gameEnded && !exposed && !flagged) {
				if(e.getButton() == 1) {
					setBackground(Color.LIGHT_GRAY);
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// Check if both buttons were pressed (chord clicking)
			if(leftPressed && rightPressed && exposed && mineCount > 0) {
				if(!MineSweeper.isPaused && !MineSweeper.gameEnded) {
					chordClick();
				}
			}
			
			// Reset button tracking
			if(e.getButton() == 1) leftPressed = false;
			if(e.getButton() == 3) rightPressed = false;
			
			// Reset visual feedback
			if(!exposed && !flagged) {
				setBackground(originalBackground);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// Hover effect for unrevealed tiles
			if(!MineSweeper.isPaused && !MineSweeper.gameEnded && !exposed && !flagged) {
				setBackground(new Color(220, 220, 220));
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// Remove hover effect
			if(!exposed && !flagged) {
				setBackground(originalBackground);
			}
		}
	}
}