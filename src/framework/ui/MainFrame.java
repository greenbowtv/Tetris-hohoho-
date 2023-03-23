package framework.ui;

import javax.swing.JFrame;

import framework.game.Game;

/** Фрейм приложения.
 * Содержит меню и панель.
 * 
 * @author Игорь
 */
public class MainFrame extends JFrame 
{
	private static final long serialVersionUID = -9088620572478499741L;

	public MainFrame(Game<?, ?> game) {
		setJMenuBar(new MainMenu(game));
		add(new MainPanel(game));
		pack();
		setTitle(game.toString());
	}
}
