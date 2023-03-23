package framework.game.event;

import java.util.EventObject;

/** Игровое событие.
 * Доставляется слушателям Игры всякий раз по ее окончании.
 * Инкапсулирует флаг результата окончания.
 * 
 * @author Игорь
 */
public class GameEvent extends EventObject 
{
	private static final long serialVersionUID = -7947125748502440138L;
	
	private final boolean win;
	
	/** Конструирует событие.
	 * @param source Источник события.
	 * @param win Результат завершения.
	 */
	public GameEvent(Object source, boolean win) {
		super(source);
		this.win = win;
	}
	
	/** Возвращает результат завершения.
	 * @return true, если победили. Иначе false.
	 */
	public boolean isWin() {
		return win;
	}
}
