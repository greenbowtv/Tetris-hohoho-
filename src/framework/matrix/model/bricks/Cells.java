package framework.matrix.model.bricks;

import java.awt.Dimension;
import java.awt.Point;
import java.util.*;

/** Фабрика коллекций координат для нужд {@link BrickMatrixN}.
 * Координаты создаваемых {@link #create(int...)} коллекций задают произвольные ячейки
 * матрицы и сохраняют пользовательский порядок следования пар. Остальные методы являются
 * производными от {@link #area(int, int, int, int)}, задают прямоугольные диапазоны ячеек,
 * упорядочение построчно слева-направо.
 * 
 * Полезности: {@link Comparator}, {@link #toString(Set)}
 * 
 * @author Игорь
 */
public class Cells 
{
    public class Comparator implements java.util.Comparator<Point> 
	{
    	@Override
	   	public int compare(Point p1, Point p2) {
    		if (p1.y < p2.y) return -1;
    		if (p1.y > p2.y) return  1;
    		if (p1.x < p2.x) return -1;
    		if (p1.x > p2.x) return  1;
    		return 0;  
	   	}   
	}
	
	private Cells() {}
	
	/** Произвольные ячейки матрицы.
	 * @param xy Координаты попарно.
	 * @return Коллекция координат.
	 * @throws NullPointerException массив null
	 * @throws ArrayIndexOutOfBoundsException массив нечетной длины
	 */
	public static Set<Point> create(int... xy) {
        Set<Point> cells = new LinkedHashSet<>(xy.length / 2, 1);
        for (int i = 0; i < xy.length; i += 2)        
            cells.add(new Point(xy[i], xy[i + 1]));
    	return cells;
	}    

	/** Строка матрицы стандартной ширины.
	 * @param y Ордината ячеек.
	 * @return Коллекция координат.
	 */
    public static Set<Point> row(int y) {           
        return row(y, BrickMatrix.STANDARD_WIDTH);
    }

    /** Строка матрицы.
     * @param y Ордината ячеек.
     * @param width Ширина матрицы.
     * @return Коллекция координат.
     * @throws IllegalArgumentException ширина < 0
     */
    public static Set<Point> row(int y, int width) {
        return area(0, y, width, 1);
    }

    /** Столбец матрицы стандартной высоты.
     * @param x Абсцисса ячеек.
     * @return Коллекция координат.
     */
    public static Set<Point> col(int x) {
        return col(x, BrickMatrix.STANDARD_HEIGHT);
    }   
    
    /** Столбец матрицы.
     * @param x Абсцисса ячеек.
     * @param height Высота матрицы.
     * @return Коллекция координат.
     * @throws IllegalArgumentException высота < 0
     */
	public static Set<Point> col(int x, int height) {
        return area(x, 0, 1, height);  
	}

	/** Вся матрица стандартных размеров целиком.
	 * @return Коллекция координат.
	 */
    public static Set<Point> all() {
        return all(BrickMatrix.STANDARD_WIDTH, BrickMatrix.STANDARD_HEIGHT);
    }

    /** Вся матрица целиком. 
     * @param size Размеры матрицы.
     * @return Коллекция координат.
     * @throws NullPointerException размеры null 
     * @throws IllegalArgumentException размеры отрицательные
     */
    public static Set<Point> all(Dimension size) {
    	return all(size.width, size.height);
    }
    
    /** Вся матрица целиком. 
     * @param width Ширина матрицы.
     * @param height Высота матрица.
     * @return Коллекция координат.
     * @throws IllegalArgumentException размеры отрицательные
     */
    public static Set<Point> all(int width, int height) {
        return area(0, 0, width, height);
    }
    
    /** Прямоугольный диапазон ячеек матрицы.
     * @param x Левая абсцисса.
     * @param y Верхняя ордината.
     * @param width Ширина диапазона.
     * @param height Высота диапазона.
     * @return Коллекция координат.
     * @throws IllegalArgumentException размеры отрицательные
     */
    public static Set<Point> area(int x, int y, int width, int height) {
        if (width < 0) 
            throw new IllegalArgumentException();
    	
    	Set<Point> cells = new LinkedHashSet<>(width * height, 1);
        for (int yind = y; yind < y + height; yind++)
            for (int xind = x; xind < x + width; xind++)
                cells.add(new Point(xind, yind));
    	return cells;
    }

  	/** Возвращает строковое представление коллекции координат.
    * Пример вывода для массива (Integer.class, new Dimension(6, 4)):
    * <pre>
    * {@code
    * cells 3: (1, 2) (3, 4) (5, 6)
    * cells 6: (1, 1) (2, 1) (3, 1) (1, 2) (2, 2) (3, 2)
    * cells 10: (0, 0) (1, 0) (2, 0) (3, 0) (4, 0) (5, 0) (6, 0) (7, 0) (8, 0) (9, 0)
    * }
    * </pre>
    * @param cells Коллекция координат.
    * @return Форматированная строка для вывода на консоль.
    */
    public static String toString(Set<Point> cells) {
    	String str = "cells " + cells.size() + ':';
    	for (Point c : cells)
    		str += String.format(" (%d, %d)", c.x, c.y);
    	return str;
    }  

    public static void main(String[] args) {
    	System.out.println(toString(create(1, 2, 3, 4, 5, 6)));
    	System.out.println(toString(area(1, 1, 3, 2)));
    	System.out.println(toString(row(0)));
    }
}
