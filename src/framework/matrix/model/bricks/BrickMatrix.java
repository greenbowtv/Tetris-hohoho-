package framework.matrix.model.bricks;

import java.awt.Dimension;
import java.awt.Point;

import framework.matrix.model.array.*;

/** Матрица для манипулирования отдельными блоками.
 * Взамен универсальному {@link #set(Point, Object)} предлагаются
 * исх.  нов. 
 * null  null	-
 * null  block	{@link #add(Point, Object)}
 * block null	{@link #remove(Point)}
 * block block2 {@link #replace(Point, Object)}
 * Они не возвращают флаг успешного завершения. Отсутствие исключений
 * вот гарантия успешности выполнения.
 * 
 * Проверит состояние ячейки метод {@link #contains(Point)}, для удобства
 * добавлен {@link #transfer(Point, Point)}. Если ячейка назначения не 
 * вакантна (содержит блок и не является исходной) или ее координаты выходят
 * за границы, это очевидные ситуации при реализации сдвига/поворота в дальнейшем, 
 * ошибок не возникает. Вместо этого возвращается false.
 * 
 * Серия - последовательность модификаций при выключенном флаге немедленной
 * отправки слушателям событий изменения. Вместо явного манипулирования
 * флагом предлагается начинать серию с вызова {@link #startSeries()}, завершать 
 * - {@link #stopSeries()}.
 * 
 * 1 метод 1 событие - правило, согласно которому вызов любого модифицирующего
 * метода не должен приводить к отправке более одного события изменения.
 * Каждый метод с 2+ модификациями выполняет код:
 * 
 * boolean innerSeries = isFiringImmediately();
 * if (innerSeries) setFiringImmediately(false); 
 * try 
 *   // ...
 * }
 * finally {
 *   if (innerSeries) setFiringImmediately(true);
 * }
 * Это позволит соблюсти правило с одной стороны. С другой, не дробить внешнюю серию.
 * 
 * @author Игорь
 * @param <B> Тип блоков. Рекомендуется перечисление или неизменяемый тип.
 */
public class BrickMatrix<B> extends ArrayAdapter<B>
{         
	public static final int STANDARD_WIDTH  = 10;
	public static final int STANDARD_HEIGHT = 20;
   
	/** Конструирует матрицу стандартных размеров.
	 * @param brickType Описывает тип блоков.
	 * @throws NullPointerException аргумент null
	 * @throws IllegalArgumentException описывается примитив
	 */
	public BrickMatrix(Class<B> brickType) {
		super(brickType, new Dimension(STANDARD_HEIGHT, STANDARD_WIDTH));
	}  

	/** Конструирует матрицу.
	 * @param brickType Описывает тип блоков.
	 * @param size Размеры матрицы.
	 * @throws NullPointerException аргументы null
	 * @throws IllegalArgumentException описывается примитив, размеры < 1x1
	 */
	public BrickMatrix(Class<B> brickType, Dimension size) {
		super(brickType, size);
	}      

	/** Проверяет, содержит ли ячейка матрицы блок.
	 * @param cell Координаты проверяемой ячейки.
	 * @return true, если ячейка содержит блок. Иначе false.
	 * @throws NullPointerException координаты null
	 * @throws OutOfBoundsException координаты выходят за границы
	 */
	public boolean contains(Point cell) {
		return get(cell) != null;
	}
	
	/** Добавляет блок в ячейку матрицы.
	 * Ячейка должна быть пустой. Для замены используйте {@link #replace(Point, Object)}
	 * Значение блока не может быть null. Для удаления используйте {@link #remove(Point)}.
	 * @param cell Координаты целевой ячейки.
	 * @param brick Добавляемый блок.
 	 * @throws NullPointerException координаты null
	 * @throws OutOfBoundsException координаты выходят за границы
	 * @throws IllegalStateException ячейка не пуста
	 * @throws IllegalArgumentException блок null
	 */
	public void add(Point cell, B brick) {
		if (contains(cell))
			throw new IllegalStateException(String.format("ячейка (%d, %d) не пуста", cell.x, cell.y));
		if (brick == null)
			throw new IllegalArgumentException("блок null");
       
		set(cell, brick);
	}

	/** Удаляет блок из ячейки матрицы.
	 * Ячейка должна содержать блок.
	 * @param cell Координаты целевой ячейки.
	 * @return Удаленный блок.
	 * @throws NullPointerException координаты null
	 * @throws OutOfBoundsException координаты выходят за границы
	 * @throws IllegalStateException ячейка пуста
	 */
	public B remove(Point cell) {
		if (!contains(cell))
			throw new IllegalStateException(String.format("ячейка (%d, %d) пуста", cell.x, cell.y));
	       
       	return set(cell, null);
	}

	/** Заменяет блок в ячейке матрицы.
	 * Ячейка должна содержать блок. Для добавления используйте {@link #add(Point, Object)}.
	 * Значение блок не может быть null. Для удаления используйте {@link #remove(Point)}.
	 * Заменяющий и заменяемый блок должна различаться.
	 * @param cell Координаты целевой ячейки.
	 * @param newBrick Заменяющий блок.
 	 * @throws NullPointerException координаты null
	 * @throws OutOfBoundsException координаты выходят за границы
	 * @throws IllegalStateException ячейка пуста
	 * @throws IllegalArgumentException блок null, равен заменяемому
	 */
	public void replace(Point cell, B newBrick) {
		if (!contains(cell))
			throw new IllegalStateException(String.format("ячейка (%d, %d) пуста", cell.x, cell.y));
		if (newBrick == null)
			throw new IllegalArgumentException("блок null");
		if (newBrick.equals(set(cell, newBrick)))
			throw new IllegalArgumentException("блок равен заменямому");
	}
      
	/** Переносит блок из одной ячейки матрицы в другую.
	 * Исходная ячейка должна содержать блок, ячейка назначения быть вакантной.
	 * @param fromCell Координаты исходной ячейки.
	 * @param toCell Координаты ячейки назначения.
	 * @return true, если удалось перенести. Иначе false.
	 * @throws NullPointerException координаты null
	 * @throws OutOfBoundsException координаты исходной ячейки выходят за границы
	 * @throws IllegalStateException исходная ячейка пуста
	 */
	public boolean transfer(Point fromCell, Point toCell) {
		boolean innerSeries = isFiringImmediately();
		if (innerSeries) setFiringImmediately(false); 
       
		try {
			B brick = remove(fromCell);
			try {
				add(toCell, brick);
				return true;
			} catch (OutOfBoundsException | IllegalStateException e) {
				add(fromCell, brick);
				return false;
			}
		}
		finally {
           if (innerSeries) setFiringImmediately(true);
		}
	}
   
	/** Начинает новую серию модификаций.
	 * Текущая серия будет завершена.
	 */
	public void startSeries() {
		if (!isFiringImmediately())
			setFiringImmediately(true);
		
		setFiringImmediately(false);
	}
	
	/** Завершает текущую серию модификаций.
	 * Слушателям получат не более 1 объекта события изменения.
	 */
	public void stopSeries() {
		setFiringImmediately(true);
	}

   	public static void main(String[] args) {
   		BrickMatrix<Integer> matrix = new BrickMatrix<>(Integer.class, new Dimension(6, 4));
   		matrix.addChangeListener((e) -> {
   			System.out.println(e);
   			System.out.println(matrix);
   		});
   		
   		System.out.println("3 по отдельности");
   		matrix.add(new Point(0, 0), 1);
   		matrix.add(new Point(1, 0), 2);
   		matrix.add(new Point(2, 0), 3);
   		
   		System.out.println("3 серией");
   		matrix.startSeries();
   		matrix.add(new Point(0, 1), 10);
   		matrix.add(new Point(1, 1), 20);
   		matrix.add(new Point(2, 1), 30);
   		matrix.stopSeries();
   		
   		System.out.println("1 серией");
   		matrix.startSeries();
   		matrix.add(new Point(0, 2), 100);
   		matrix.stopSeries();
   		
   		System.out.println("Взаимные компенсации");
   		matrix.startSeries();
   		matrix.add(new Point(1, 2), 200);
   		matrix.add(new Point(2, 2), 300);
   		matrix.remove(new Point(1, 2));
   		matrix.remove(new Point(2, 2));
   		matrix.stopSeries();
   		
   		System.out.println("Cерии подряд");
   		matrix.startSeries();
   		matrix.remove(new Point(0, 0));
   		matrix.remove(new Point(1, 0));
   		matrix.remove(new Point(2, 0));
   		matrix.startSeries();
   		matrix.remove(new Point(0, 1));
   		matrix.remove(new Point(1, 1));
   		matrix.remove(new Point(2, 1));
   		matrix.startSeries();
   		matrix.remove(new Point(0, 2));
   		
   		System.out.println("Серия не завершена, очистка");
   		matrix.clear();
   		System.out.println("флаг " + matrix.isFiringImmediately());
   	}
}
