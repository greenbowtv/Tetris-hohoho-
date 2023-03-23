package framework.matrix.model.pieces;

import java.awt.Dimension;
import java.awt.Point;
import java.util.*;

import framework.matrix.model.array.OutOfBoundsException;
import framework.matrix.model.bricks.BrickMatrixN;
import framework.matrix.model.bricks.Cells;

/** Матрица с поддержкой фигур.
 * Фигура создается с помощью {@link #create(Object)}, дальнейшие манипуляции над ней
 * уже через интерфейс фигуры. Блоки фигур защищены от модификаций, только сама фигура
 * может манипулировать ими. Для этого она устанавливает переменной unlocker ссылку на
 * себя, чтобы {@link #set(Point, Object)} пропустил изменение элемента фигуры. Пустая
 * ячейка, исходя из определения, не может относиться к фигуре. Проверить, является ли
 * ячейка нейтральной можно с помощью {@link #isNeutral(Point)}.
 * 
 * @author Игорь
 * @param <B> Тип блоков. Рекомендуется перечисление или неизменяемый тип.
 * @param <P> Тип фигур.
 */
public class PieceMatrix<B, P> extends BrickMatrixN<B>
{
	private PieceCreator<B, P> creator;		// делегат создания фигур
		    List<Piece<B>> pieces;			// фигуры матрицы
		    Piece<B> unlocker;				// фигура, разблокировавшая свои ячейки
	
	/** Конструирует матрицу стандартных размеров.
	 * @param brickType Описывает тип блоков.
	 * @param creator Делегат создания фигур.
	 * @throws NullPointerException аргументы null
	 * @throws IllegalArgumentException описывается примитив, размеры < 1x1
	 */
	public PieceMatrix(Class<B> brickType, PieceCreator<B, P> creator) {
		this(brickType, new Dimension(STANDARD_WIDTH, STANDARD_HEIGHT), creator);
	}
	
	/** Конструирует матрицу.
	 * @param brickType Описывает тип блоков.
	 * @param size Размеры матрицы.
	 * @param creator Делегат создания фигур.
	 * @throws NullPointerException аргументы null
	 * @throws IllegalArgumentException описывается примитив, размеры < 1x1
	 */
	public PieceMatrix(Class<B> brickType, Dimension size, PieceCreator<B, P> creator) {
		super(brickType, size);
		if (creator == null)
			throw new NullPointerException();
		
		this.creator = creator;
		this.pieces = new ArrayList<>();
		this.unlocker = null;
	}
	
	/** Создает фигуру в матрице.
	 * @param kind Разновидность фигуры.
	 * @return Созданная фигура.
	 * @throws NotEnoughSpaceException не хватило места в матрице
	 * @throws NullPointerException разновидность null
	 * @throws IllegalArgumentException разновидность недопустимое значение
	 */
	public Piece<B> create(P kind) throws NotEnoughSpaceException {
		return creator.create(this, kind);
	}

	/** Проверяет, является ли ячейка нейтральной.
	 * @param cell Координаты проверяемой ячейки.
	 * @return true, если ячейка не содержит блока ни одной из фигур.
	 */
	public boolean isNeutral(Point cell) {
		for (Piece<B> p : pieces)
			if (p.cells.contains(cell)) return false;
		return true;
	}
	
	/** Подсчитывает фигуры в матрице.
	 * @return Число фигур.
	 */
	public int countPieces() {
		return pieces.size();
	}
	
	/** Устанавливает элемент массива.
	 * Атомарное модифицирующее действие.
	 * @param cell Координаты элемента.
	 * @param elem Новое значение элемента.
	 * @return Старое значение элемента.
	 * @throws NullPointerException координаты null
	 * @throws OutOfBoundsException координаты выходят за границы
	 * @throws UnsupportedOperationException изменение элемента фигуры
	 */
	@Override
	protected B set(Point cell, B elem) {
		if (isNeutral(cell) || unlocker != null && unlocker.cells.contains(cell))
			return super.set(cell, elem);
		throw new UnsupportedOperationException("изменение элемента фигуры");
	}

	/** Очищает матрицу.
	 * Удаляет все фигуры и блоки.
	 * Завершает текущую серию модификаций.
	 */
	@Override
	public void clear() {
		for (Piece<B> p : new ArrayList<>(pieces))
			p.destroy();
		unlocker = null;
		super.clear();
	}
	
  	/** Возвращает строковое представление матрицы.
    * Пример вывода:
    * <pre>
    * {@code 
    *     1    2    3 null null null
    *    10   20   30 null null null
    *   100 null null null null null
    *  null null null null null null
    * blocks: 7
    * pieces: 3
    * unlocker: Piece [
    *  cells 3: (0, 0) (1, 0) (2, 0)
    *  разблокировка вкл
    * ]
    * }
    * </pre>
    * @return Форматированная строка для вывода на консоль.
    */
	@Override
	public String toString() {
		return super.toString() + "pieces: " + countPieces() + '\n' +
		  "unlocker: " + unlocker + '\n';
	}

	public static void main(String[] args) {
		PieceMatrix<Integer, Integer> matrix = new PieceMatrix<>(Integer.class, new Dimension(6, 4), new CreatorStub());
		
		try {
			matrix.create(0);	System.out.println(matrix);
			matrix.create(1);	System.out.println(matrix);
			matrix.create(2);	System.out.println(matrix);		
			
		} catch (NotEnoughSpaceException e) {
			System.out.println(e);
		}
		
		try {
		matrix.remove(new Point(0, 0));	System.out.println(matrix);
		} catch (RuntimeException e) {
			System.out.println(e);
		}
		matrix.clear();					System.out.println(matrix);
	}
}

class CreatorStub implements PieceCreator<Integer, Integer> 
{
	@SuppressWarnings("serial")
	@Override
	public Piece<Integer> create(PieceMatrix<Integer, Integer> matrix, Integer kind) throws NotEnoughSpaceException {
		switch (kind) {
			case 0:
				return new Piece<>(matrix, Cells.create(0, 0, 1, 0, 2, 0), new ArrayList<Integer>(){{add(1); add(2); add(3);}});
			case 1:
				return new Piece<>(matrix, Cells.create(0, 1, 1, 1, 2, 1), new ArrayList<Integer>(){{add(10); add(20); add(30);}});
			case 2:
				return new Piece<>(matrix, Cells.create(0, 2), 			   new ArrayList<Integer>(){{add(100);}});
			default:
				throw new IllegalArgumentException();
		}
	}
}
