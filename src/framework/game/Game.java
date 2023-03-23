package framework.game;

import java.util.Random;

import javax.swing.Timer;

import framework.game.event.GameListened;
import framework.matrix.model.pieces.PieceCreator;
import framework.matrix.model.pieces.PieceMatrix;

/** Основа тетрисной игры-аркады.
 * 
 * @author Игорь
 *
 * @param <B> Тип блоков.
 * @param <S> Тип фигур.
 */
public abstract class Game<B, S> extends GameListened implements PieceControl
{
	/** Возможные состояния игры.
	 * (прикрепить диаграмму переходов)
	 * 
	 * @author Игорь
	 */
	public enum State
	{
		NOT_STARTED, ACTIVE, PAUSED
	}
	
	protected PieceMatrix<B, S> matrix;
	protected Random 			random;
	private   Timer 			timer;
	private   State 			state;
	
	public Game(Class<B> brickType, PieceCreator<B, S> creator) {
		matrix = new PieceMatrix<>(brickType, creator);
		random = new Random();
		timer = new Timer(1000, (e) -> onTimerTick());
		timer.setInitialDelay(0);
		state = State.NOT_STARTED;
	}
	
	/** Начинает новую игру.
	 */
	public void start() {
		timer.stop();
		
		matrix.clear();
		timer.start();
		state = State.ACTIVE;
	}
	
	/** Приостанавливает текущую игру.
	 * @throws IllegalStateException игра не активна
	 */
	public void pause() {
		if (state != State.ACTIVE)
			throw new IllegalStateException();
		
		timer.stop();
		state = State.PAUSED;
	}
	
	/** Возобновляет приостановленную игру.
	 * @throws IllegalStateException игра не на паузе
	 */
	public void resume() {
		if (state != State.PAUSED)
			throw new IllegalStateException();	
		
		timer.restart();
		state = State.ACTIVE;
	}
	
	/** Останавливает запущенную игру.
	 * Игровое событие не отправляется слушателям.
	 * @throws IllegalStateException игра не запущена
	 */
	public void stop() {
		if (state == State.NOT_STARTED)
			throw new IllegalStateException();
		
		timer.stop();
		state = State.NOT_STARTED;
	}
	
	/** Возвращает состояние игры.
	 * @return
	 */
	public State getState() {
		return state;
	}

	/** Возвращает матрицу игру.
	 * @return
	 */
	public PieceMatrix<B, S> getMatrix() {
		return matrix;
	}
	
	/** Обработчик тика таймера.
	 * 
	 */
	protected abstract void onTimerTick();
}
