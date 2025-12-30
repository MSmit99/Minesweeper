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
			
			if(e.getButton() == 3) {
				if(!exposed) {
					MineSweeper.flag(row, col);
				}
			}
			
			if(e.getButton()== 1) {
				if(flagged) return;
				
				if(mine) {
					exposed = true;
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

		@Override
		public void mousePressed(MouseEvent e) {
			// Visual feedback on press
			if(!MineSweeper.isPaused && !MineSweeper.gameEnded && !exposed && !flagged) {
				if(e.getButton() == 1) {
					setBackground(Color.LIGHT_GRAY);
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
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