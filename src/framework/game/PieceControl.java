package framework.game;

/** Задает методы управления фигурой в игре.
 * 
 * @author Игорь
 */
public interface PieceControl 
{
	/** Двигает фигуру влево.
	 * Действие выполняется только если игра активна.
	 * @throws UnsupportedOperationException действие не определено для данной игры
	 */
	default void moveLeft() {
		throw new UnsupportedOperationException();
	}
	
	/** Двигает фигуру вправо.
	 * Действие выполняется только если игра активна.
	 * @throws UnsupportedOperationException действие не определено для данной игры
	 */
	default void moveRight() {
		throw new UnsupportedOperationException();
	}
	
	/** Двигает фигуру вверх.
	 * Действие выполняется только если игра активна.
	 * @throws UnsupportedOperationException действие не определено для данной игры
	 */
	default void moveUp() {
		throw new UnsupportedOperationException();
	}
	
	/** Двигает фигуру вниз.
	 * Действие выполняется только если игра активна.
	 * @throws UnsupportedOperationException действие не определено для данной игры
	 */
	default void moveDown() {
		throw new UnsupportedOperationException();
	}
	
	/** Поворачивает фигуру.
	 * Действие выполняется только если игра активна.
	 * @throws UnsupportedOperationException действие не определено для данной игры
	 */
	default void rotate() {
		throw new UnsupportedOperationException();
	}
}
