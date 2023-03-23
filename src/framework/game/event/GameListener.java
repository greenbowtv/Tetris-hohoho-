package framework.game.event;

import java.util.EventListener;

/** Интерфейс слушателя Игры.
 * 
 * @author Игорь
 */
public interface GameListener extends EventListener
{
	/** Вызывается по окончании Игры.
	 * Событие инкапсулирует результат завершения.
	 * @param e Игровое событие.
	 */
	void gameOver(GameEvent e);
}
