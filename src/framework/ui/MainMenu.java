package framework.ui;

/** Меню фрейма приложения.
 * Управляет состоянием игры, отслеживает ее завершение.
 * 
 */
import javax.swing.*;

import framework.game.Game;

public class MainMenu extends JMenuBar 
{
	private static final long serialVersionUID = -3546036285487079630L;
	
	private Game<?, ?> game;

	public MainMenu(Game<?, ?> game) {
		this.game = game;
		
		createFileMenu();
		createGameMenu();
		configurePauseResume();
		
		game.addGameListener((e) -> {
			String message = e.isWin() ? "Вы выиграли." : "Вы проиграли.";
			int answer = JOptionPane.showConfirmDialog(null, message, "Game over", JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.YES_OPTION)
				game.start();
			else
				configurePauseResume();
		});
	}
	
	private void createFileMenu() {
		JMenu menu = new JMenu("Файл");
		menu.add("Выход").addActionListener((e) -> System.exit(0));
		add(menu);
	}
	
	private void createGameMenu() {
		JMenu menu = new JMenu("Игра");
		JMenuItem start = menu.add("Новая");
		start.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
		start.addActionListener((e) -> {
			game.start();
			configurePauseResume();
		});
		JMenuItem pauseResume = menu.add("Пауза/Возобновить");
		pauseResume.setAccelerator(KeyStroke.getKeyStroke("ctrl P"));
		pauseResume.addActionListener((e) -> {
			if (game.getState() == Game.State.ACTIVE)
				game.pause();
			else
				game.resume();
			configurePauseResume();
		});
		add(menu);
	}
	
	private void createSkinMenu() {
		
	}
	
	private void configurePauseResume() {
		JMenu gameMenu = getMenu(1);
		JMenuItem pauseResume = gameMenu.getItem(1);
		switch (game.getState()) {
			case NOT_STARTED:
				pauseResume.setText("Пауза");
				pauseResume.setEnabled(false);
				break;
			case ACTIVE:
				pauseResume.setText("Пауза");
				pauseResume.setEnabled(true);
				break;
			case PAUSED:
				pauseResume.setText("Возобновить");
				pauseResume.setEnabled(true);
				break;
		}
	}	
}
