package start;

import javax.swing.*;

import framework.ui.MainFrame;
import games.Tetris;

public class StartTetris 
{
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new MainFrame(new Tetris());
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		});
	}
}
