package framework.matrix.model.array;

import java.awt.Dimension;
import java.awt.Point;
import java.lang.reflect.Array;
import java.util.*;

/** Адаптер 2D массива, основа иерархии модели Матрицы.
 *  автоматически уведомляет слушателей об изменении элементов
 *  оптимизирует отправку слушателям событий
 *  осуществляет переход от задания элементов индексами к координатам Point
 *  
 * Прямоугольный массив фиксированной структуры. Для создания и работы с массивом используются 
 * механизмы рефлексии. Тип элементов произвольный, однако рекомендуется перечисление
 * или неизменяемый.
 *  
 * Операция чтения выполняется в методе {@link #get(Point)}, операция записи в
 * {@link #set(Point, Object)}. Это атомарные действия, к их выполнению сводятся
 * вызовы всех методов иерархии модели Матрицы.
 * 
 * В случае фактического изменения элемента в результате модифицирующего действия
 * слушатели изменений получают объект {@link ElementsChangeEvent} с индексами
 * измененных элементов. Если установлен флаг немедленного уведомления слушателей.
 * В противном случае, изменения только запоминаются до момента установки флага вновь.
 * Это и есть оптимизация.
 * 
 * Для задания элемента используются объекты Point, вместо размерностей - Dimension.
 * Ошибка выхода ArrayIndexOutOfBounds заменена на OutOfBounds. Более ничего не напоминает
 * об индексах.
 * 
 * @param <E> Тип элементов.
 */
public class ArrayAdapter<E> extends ChangeListened
{     
	private Object      array;           	// 2D массив элементов
	private Class<E>    elemType;        	// описывает тип элементов
	private boolean     firingImmediately;	// флаг немедленного уведомления слушателей
											//  в случае фактического изменения элемента
	private Set<Point> 	modifiedCells;    	// индексы измененных элементов
	private List<E>     initialElems;     	// исходные значения этих элементов    
  
	/** Конструирует адаптера заданных размеров.
	 * Позже их изменить нельзя.
	 * Флаг немедленного уведомления слушателей установлен.
	 * @param elemType Описывает тип элементов.
	 * @param size Размеры массива.
	 * @throws NullPointerException аргументы null
	 * @throws IllegalArgumentException описывается примитив, размеры < 1x1
	 */
	public ArrayAdapter(Class<E> elemType, Dimension size) {
		if (elemType.isPrimitive())
			throw new IllegalArgumentException("примитивный тип");
       	if (size.width < 1 || size.height < 1)
       		throw new IllegalArgumentException("размеры < 1x1");
         
       	this.elemType = elemType;
       	this.array = Array.newInstance(elemType, size.height, size.width); 
       	this.firingImmediately = true;
       	this.modifiedCells = new LinkedHashSet<>();
       	this.initialElems = new ArrayList<>();  
	}
   
	/** Возвращает элемент массива.
	 * Атомарное не модифицирующее действие.
	 * @param cell Координаты элемента.
	 * @return Значение элемента.
	 * @throws NullPointerException координаты null
	 * @throws OutOfBoundsException координаты выходят за границы
	 */
	public E get(Point cell) {
		try {
			Object line = Array.get(array, cell.y);
			Object elem = Array.get(line, cell.x);
			return elemType.cast(elem);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new OutOfBoundsException(cell);
		}
	}
   
	/** Устанавливает элемент массива.
	 * Атомарное модифицирующее действие.
	 * @param cell Координаты элемента.
	 * @param elem Новое значение элемента.
	 * @return Старое значение элемента.
	 * @throws NullPointerException координаты null
	 * @throws OutOfBoundsException координаты выходят за границы
	 */
	protected E set(Point cell, E elem) {
		try {
			Object line = Array.get(array, cell.y);
			E oldElem = elemType.cast(Array.get(line, cell.x));
			if (!Objects.equals(oldElem, elem)) {
				Array.set(line, cell.x, elem);
				if (firingImmediately) {
					fireStateChanged(new ElementsChangeEvent(this, new Point(cell)));
				}
				else if (modifiedCells.add(new Point(cell))) {
					initialElems.add(oldElem);
				}     
			}
			return oldElem;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new OutOfBoundsException(cell);
		}
	}    

	/** Возвращает флаг немедленного уведомления слушателей.
	 * @return
	 */
	protected boolean isFiringImmediately() {
       return firingImmediately;
	}    
   
	/** Устанавливает флаг немедленного уведомления слушателей. 
	 * @param aFlag true, чтобы в случае фактического изменения элемента в результате
	 * вызова {@link #set(Point, Object)} слушатели сразу получали объект события
	 * {@link ElementsChangeEvent}. false, чтобы изменения лишь запоминались в ожидании
	 * пока вновь не будет установлен флаг.
	 */
	protected void setFiringImmediately(boolean aFlag) {
       if (!this.firingImmediately && aFlag) {
           // взаимные компенсации?
           int count = 0;
           for (Iterator<Point> cellIterator = modifiedCells.iterator(); cellIterator.hasNext(); ) {
               if (Objects.equals(get(cellIterator.next()), initialElems.get(count))) {
                   cellIterator.remove();
                   initialElems.remove(count);
               }
               else {
                   count++;
               }
           }
           
           if (modifiedCells.size() > 0) {
               fireStateChanged(new ElementsChangeEvent(this, modifiedCells.toArray(new Point[0])));                    
               modifiedCells.clear();
               initialElems.clear();
           }
       }
       
       this.firingImmediately = aFlag;
   }

	/** Очищает массив.
	 * Всем элементам присваивается null значение.
	 * Устанавливается флаг немедленного уведомления слушателей.
	 */
	public void clear() {
		setFiringImmediately(false);
       
		Dimension size = size();
		Point cell = new Point();
		for (cell.y = 0; cell.y < size.height; cell.y++)
			for (cell.x = 0; cell.x < size.width; cell.x++)
				set(cell, null);
       
       	setFiringImmediately(true);
	}
   
	/** Проверяет, все ли элементы массива имеют null значения.
	 * @return
	 */
	public boolean isEmpty() {
		Dimension size = size();
		Point cell = new Point();
		for (cell.y = 0; cell.y < size.height; cell.y++)
			for (cell.x = 0; cell.x < size.width; cell.x++)
				if (get(cell) != null) return false;
		
		return true;
	}
	
	/** Возвращает описатель типа элементов.
	 * @return
	 */
  	public Class<E> getElemType() {
       return elemType;
  	}     
   
  	/** Предоставляет размеры массива.
  	 * @return
  	 */
  	public Dimension size() {
  		return new Dimension(
		  Array.getLength(Array.get(array, 0)),
  		  Array.getLength(array));
  	}

  	/** Возвращает строковое представление массива.
    * Пример вывода для массива (Integer.class, new Dimension(6, 4)):
    * <pre>
    * {@code 
    *    1    2    3 null null null
    *   10   20   30 null null null
    *  100 null null null null null
    * null null null null null null
    * }
    * </pre>
    * @return Форматированная строка для вывода на консоль.
    */
  	@Override
   	public String toString() {
  		Dimension size = size();
		Point cell = new Point();
		
		// длиннейшее строковое представление?
		int width = 0;
		for (cell.y = 0; cell.y < size.height; cell.y++) {
			for (cell.x = 0; cell.x < size.width; cell.x++) {
				E elem = get(cell);
				width = Math.max(Objects.toString(elem).length(), width);
			}
		}
	   
		// формирование строки
		String format = "%" + ++width + "s";
		StringBuilder bldr = new StringBuilder((size.width * width + 1) * size.height);
		for (cell.y = 0; cell.y < size.height; cell.y++) {
			for (cell.x = 0; cell.x < size.width; cell.x++) {
				E elem = get(cell);
				bldr.append(String.format(format, Objects.toString(elem)));      
			}
			bldr.append('\n');
		}
		return bldr.toString();
  	}
   
   	public static void main(String[] args) {
   		ArrayAdapter<Integer> array = new ArrayAdapter<>(Integer.class, new Dimension(6, 4));
   		array.addChangeListener((e) -> {
   			System.out.println(e);
   			System.out.println(array);
   		});
   		
   		System.out.println("флаг " + array.isFiringImmediately());
   		array.set(new Point(0, 0), 1);
   		array.set(new Point(1, 0), 2);
   		array.set(new Point(2, 0), 3);
   		
   		array.setFiringImmediately(false);
   		System.out.println("флаг " + array.isFiringImmediately());
   		array.set(new Point(0, 1), 10);
   		array.set(new Point(1, 1), 20);
   		array.set(new Point(2, 1), 30);

   		array.setFiringImmediately(false);
   		array.set(new Point(0, 2), 100);
   		array.setFiringImmediately(true);
   		
   		array.clear();
   	}
}
