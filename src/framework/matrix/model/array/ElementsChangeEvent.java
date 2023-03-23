package framework.matrix.model.array;

import java.awt.Point;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.swing.event.ChangeEvent;

/** Событие изменения элементов 2D массива.
 * Предоставляет координаты измененных элементов.
 * 
 * @author Игорь
 */
public class ElementsChangeEvent extends ChangeEvent implements Iterable<Point>
{
	private static final long serialVersionUID = 2736915267736448905L;

	private class CellIterator implements Iterator<Point> 
	{      
       int count = 0;
       
       @Override
       public boolean hasNext() {
           return count != cells.length;
       }

       @Override
       public Point next() {
           if (count == cells.length)
               throw new NoSuchElementException();
           return cells[count++];
       }
   }

	private Point[] cells;

	/** Конструирует событие.
    * @param source Источник событий.
    * @param cells Координаты измененных элементов.
    * @throws NullPointerException Если массив null.
    * @throws IllegalArgumentException Если источник null, либо массив пустой.
    */
   	public ElementsChangeEvent(ArrayAdapter<?> source, Point... cells) {
       super(source);
       if (cells.length == 0)
           throw new IllegalArgumentException("массив пустой");   
       
       this.cells = cells;
   }
        
   	/** Возвращает итератор.
    * @return Итератор по координатам измененных элементов.
    */
   	@Override
   	public Iterator<Point> iterator() {
   		return new CellIterator();
   	}

   	/** Возвращает строковое представление.
    * Примеры вывода:
    * <pre>
    * {@code
    * ElementsChangeEvent 1: (0, 0)
    * ElementsChangeEvent 2: (0, 0) (1, 0)
    * ElementsChangeEvent 3: (0, 0) (1, 0) (2, 0)
    * }
    * </pre>
    * @return Форматированная строка для вывода на консоль.
    */
   	@Override
   	public String toString() {
   		String str = getClass().getSimpleName() + ' ' + cells.length + ':';
   		for (Point c : cells)
   			str += String.format(" (%d, %d)", c.x, c.y);
   		return str;           
   	}
   	
   	public static void main(String[] args) {
   		ArrayAdapter<Integer> array = new ArrayAdapter<>(Integer.class,  new java.awt.Dimension(6, 4));
   		ChangeEvent e;
   		e = new ElementsChangeEvent(array, new Point(0, 0));
   		System.out.println(e);
   		e = new ElementsChangeEvent(array, new Point(0, 0), new Point(1, 0));
   		System.out.println(e);
   		e = new ElementsChangeEvent(array, new Point(0, 0), new Point(1, 0), new Point(2, 0));
   		System.out.println(e);
   	}
}
