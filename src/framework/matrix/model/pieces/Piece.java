package framework.matrix.model.pieces;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.*;

import framework.matrix.model.array.OutOfBoundsException;
import framework.matrix.model.bricks.Cells;

/** Фигура блоков в матрице.
 * Фигура - совокупность блоков, рассматриваемых как единое целое. Фигура состоит
 * по крайней мере из одного блока, пустые ячейки не могут относиться к фигуре.
 * Создание фигуры сводится к добавление в целевые ячейки матрицы блоков. Если
 * хотя бы одна ячейка занята, выбрасывается исключение.
 * 
 * Метод {@link #delete()} удаляет фигуру из матрицы, [@link {@link #destroy()}
 * только разрушает. При этом блоки фигуры остаются в ячейках матрицы, которые
 * теперь становятся нейтральными. В обоих случае фигура прекращает свое существование.
 * Любое обращение к фигуре приведет к ошибке, всегда проверяйте состояние с помощью
 * {@link #isExist()}.
 * 
 * Класс содержит заглушки методов {@link #move(Direction)} и {@link #rotate()}.
 * Переопределите их если требуются действия сдвига или поворота.
 * 
 * Чтобы выполнить модифицирующие действия над блоками фигуры, окружите их вызовами
 * {@link #unlock(boolean)}. Каждое действие должно начитаться с проверки состояния
 * фигуры.
 * 
 * @author Игорь
 * @param <B> Тип блоков.
 */
public class Piece<B>
{
	protected PieceMatrix<B, ?> matrix;	// рабочая матрица
	protected Set<Point> cells;			// координаты ячеек фигуры
	private boolean exist;				// признак "фигура существует"
	
	/** Конструирует пустую фигуру.
	 * Исходя из определения, такая фигура не существует с самого начала.
	 */
	public Piece() {}
	
	/** Конструирует фигуру.
	 * @param matrix Матрица, в которую добавляется фигура.
	 * @param cells Координаты целевых ячеек.
	 * @param bricks Добавляемые блоки.
	 * @throws NotEnoughSpaceException не хватило места в матрице
	 * @throws NullPointerException аргументы null, координаты-элемент коллекции null
	 * @throws OutOfBoundsException координаты выходят за границы
	 * @throws IllegalArgumentException коллекции пусты, размеры различаются, блоки null
	 */
	public Piece(PieceMatrix<B, ?> matrix, Set<Point> cells, List<B> bricks) 
	  throws NotEnoughSpaceException 
	{
		try {
			matrix.addAll(cells, bricks);
		}
		catch (IllegalStateException e) {
			throw new NotEnoughSpaceException();
		}
		matrix.pieces.add(this);
		this.matrix = matrix;
		this.cells = cells;
		this.exist = true;	
	}
	
	/** Удаляет фигуру из матрицы.
	 * @throws IllegalStateException фигура не существует
	 */
	public void delete() {
		if (!exist)
			throw new IllegalStateException("фигура не существует");
		
		unlock(true);
		matrix.removeAll(cells);
		unlock(false);
		
		destroy();
	}
	
	/** Разрушает фигуру.
	 * Оставляет блоки в матрице.
	 * @throws IllegalStateException фигура не существует
	 */
	public void destroy() {
		if (!exist)
			throw new IllegalStateException("фигура не существует");
		
		matrix.pieces.remove(this);
		exist = false;	
	}
		
	/** Проверяет, существует ли фигура.
	 * @return
	 */
	public boolean isExist() {
		return exist;
	}
	
	/** Сдвигает фигуру на 1 строку/столбец.
	 * @param dir Направление перемещения.
	 * @return true, если удалось сдвинуть.
	 * @throws IllegalStateException фигура не существует
	 * @throws UnsupportedOperationException если действие не поддерживается
	 */
	public boolean move(Direction dir) {
		throw new UnsupportedOperationException();
	}
	
	/** Поворачивает фигуру на угол 90* против часовой стрелки.
	 * @return true, если удалось повернуть.
	 * @throws IllegalStateException фигура не существует
	 * @throws UnsupportedOperationException если действие не поддерживается
	 */
	public boolean rotate() {
		throw new UnsupportedOperationException();
	}
	
	/** Возвращает описанную область.
	 * @return
	 * @throws IllegalStateException фигура не существует
	 */
	public Rectangle getArea() {
		if (!exist)
			throw new IllegalStateException("фигура не существует");
		
		int left = matrix.size().width - 1;
        int right  = 0;
        int top  = matrix.size().height - 1;
        int bottom = 0;
        for (Point c : cells) {
            left   = Math.min(left, c.x);
            right  = Math.max(right, c.x);
            top    = Math.min(top, c.y);
            bottom = Math.max(bottom, c.y);
        }    
        return new Rectangle(left, top, right - left + 1, bottom - top + 1);  
	}
	
	/** Заменяет блоки фигуры.
	 * @param brick Тиражируемый заменитель.
	 * @throws IllegalStateException фигура не существует
	 * @throws IllegalArgumentException блок null, равен любому заменяемому
	 */
	public void fill(B brick) {
		if (!exist)
			throw new IllegalStateException("фигура не существует");
		
		unlock(true);
		try {	
			matrix.replaceAll(cells, Collections.nCopies(cells.size(), brick));
		} finally {
			unlock(false);
		}
	}
		
	/** Разблокирует ячейки фигуры.
	 * @param aFlag true, чтобы снять защиту с ячеек фигуры.
	 * @throws IllegalStateException фигура не существует
	 */
	protected void unlock(boolean aFlag) {
		if (!exist)
			throw new IllegalStateException("фигура не существует");
		
		matrix.unlocker = aFlag? this : null;
	}

    /** Предоставляет копию координат ячеек фигуры.
     * @return
     * @throws IllegalStateException фигура не существует
     */
    protected Set<Point> copyCells() {
    	if (!exist)
			throw new IllegalStateException("фигура не существует");
        
        Set<Point> copy = new LinkedHashSet<>(cells.size(), 1);
        cells.forEach((c) -> copy.add((Point)c.clone()));
        return copy;
    }
	
  	/** Возвращает строковое представление фигуры.
    * Пример вывода:
    * <pre>
    * {@code
    * Piece [
    *  cells 3: (0, 0) (1, 0) (2, 0)
    *  разблокировка вкл
    * ]
    * }
    * </pre>
    * @return Форматированная строка для вывода на консоль.
    */
	@Override
	public String toString() {
		if (!exist)
			return "фигура не существует";
		return getClass().getSimpleName() + " [\n " +
		  Cells.toString(cells) + '\n' +
		  " разблокировка " + (matrix.unlocker == this ? "вкл\n]" : "выкл\n]");
	}
}
