package framework.ui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import framework.game.Game;
import framework.matrix.view.MatrixView;

/** Панель фрейма приложения.
 * Содержит визуализатор Матрицы, управляет фигурой Игры.
 * 
 * @author Игорь
 */
public class MainPanel extends JPanel 
{
	private static final long serialVersionUID = 2426773129735804153L;

	public MainPanel(Game<?, ?> game) {
		setLayout(new BorderLayout());
		add(new MatrixView(game.getMatrix()));
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				try {
					switch (event.getKeyCode()) {
						case KeyEvent.VK_LEFT:
						case KeyEvent.VK_A:
							game.moveLeft();
							break;
						case KeyEvent.VK_RIGHT:
						case KeyEvent.VK_D:
							game.moveRight();
							break;
						case KeyEvent.VK_UP:
						case KeyEvent.VK_W:
							game.moveUp();
							break;
						case KeyEvent.VK_DOWN:
						case KeyEvent.VK_S:
							game.moveDown();
							break;
						case KeyEvent.VK_SPACE:
							game.rotate();
							break;
					}
				} catch (UnsupportedOperationException exc) {
					Toolkit.getDefaultToolkit().beep();
				}
			}
		});
		setFocusable(true);
	}
}
