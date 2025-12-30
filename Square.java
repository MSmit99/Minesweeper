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
	
	public Square(int r, int c) {
		row = r;
		col = c;
		setFont(new Font("Arial", Font.BOLD, 18));
		addMouseListener(new ClickMe());
	}
	
	class ClickMe implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			// Start timer on first click
			MineSweeper.startTimer();
			
			if(e.getButton() == 3) {
				MineSweeper.flag(row, col);
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
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
}