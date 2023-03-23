package framework.matrix.model.bricks;

import java.awt.Dimension;
import java.awt.Point;
import java.util.*;

import framework.matrix.model.array.OutOfBoundsException;

/** Матрица для манипуляции группами блоков.
 * Методы суперкласса получают аналоги для работы с N блоками за один вызов.
 * Координаты задаются не пустой коллекцией Set, блоки коллекцией List 
 * соответствующего размера. Полезными будут:
 * {@link Cells}
 * {@link Collections}
 *
 * Методы xxxAll выполняют действие над N блоками, методы с префиксом xxxSome
 * над M, M c [0, N].
 * 
 * @author Игорь
 * @param <B> Тип блоков. Рекомендуется перечисление или неизменяемый тип.
 */
public class BrickMatrixN<B> extends BrickMatrix<B> 
{
	/** Конструирует матрицу стандартных размеров.
	 * @param brickType Описывает тип блоков.
	 * @throws NullPointerException аргумент null
	 * @throws IllegalArgumentException описывается примитив
	 */
    public BrickMatrixN(Class<B> brickType) {
        super(brickType);
    }  

	/** Конструирует матрицу.
	 * @param brickType Описывает тип блоков.
	 * @param size Размеры матрицы.
	 * @throws NullPointerException аргументы null
	 * @throws IllegalArgumentException описывается примитив, размеры < 1x1
	 */
    public BrickMatrixN(Class<B> brickType, Dimension size) {
        super(brickType, size);
    }

    /** Возвращает элементы массива.
     * Не модифицирующее действие.
     * @param cells Координаты элементов.
     * @return Значения элементов, в т.ч. null.
     * @throws NullPointerException коллекция, координаты-элемент коллекции null
	 * @throws OutOfBoundsException координаты выходят за границы
	 * @throws IllegalArgumentException коллекция пустая
     */
    public List<B> get(Set<Point> cells) {
        check(cells);

        List<B> bricks = new ArrayList<>(cells.size());
        cells.forEach((c) -> bricks.add(get(c)));
        return bricks;  
    }

    /** Проверяет, содержат ли все ячейки матрицы блоки.
     * @param cells Координаты проверяемых ячеек.
     * @return true, если все ячейки заполнены. Иначе false.
     * @throws NullPointerException коллекция, координаты-элемент коллекции null
	 * @throws OutOfBoundsException координаты выходят за границы
	 * @throws IllegalArgumentException коллекция пустая
     */
    public boolean containsAll(Set<Point> cells) {
    	for (B b : get(cells))
    		if (b == null) return false;
    	return true;
    }
    
    /** Добавляет блоки во все ячейки матрицы.
     * Добавляет N блоков в N ячеек.
     * Каждая ячейка должна быть пустой. Для замены используйте {@link #replaceAll(Set, List)}.
     * Значения блоков не могут быть null. Для удаления используйте {@link #removeAll(Set)}.
     * @param cells Координаты целевых ячеек.
     * @param bricks Добавляемые блоки.
     * @throws NullPointerException коллекции null, координаты-элемент коллекции null
	 * @throws OutOfBoundsException координаты выходят за границы
	 * @throws IllegalStateException любая из ячеек не пуста
	 * @throws IllegalArgumentException коллекции пусты, размеры различаются, 
	 * любой блок null
     */
    public void addAll(Set<Point> cells, List<B> bricks) {
        check (cells, bricks);

        boolean innerSeries = isFiringImmediately();
        if (innerSeries) startSeries();

        try {                
            Iterator<B> b = bricks.iterator();
            cells.forEach((c) -> add(c, b.next()));
        }
        finally {
            if (innerSeries) stopSeries();
        }
    }

    public void addSome(Set<Point> cells, List<B> bricks) {
    	throw new UnsupportedOperationException();
    }
    
    /** Удаляет блоки из всех ячеек матрицы.
     * Удаляет N блоков из N ячеек.
     * Каждая ячейка должна содержать блок.
     * @param cells Координаты целевых ячеек.
     * @return Удаленные блоки.
     * @throws NullPointerException коллекция null, координаты-элемент коллекции null
	 * @throws OutOfBoundsException координаты выходят за границы
	 * @throws IllegalStateException любая из ячеек пуста
	 * @throws IllegalArgumentException коллекция пуста
     */
    public List<B> removeAll(Set<Point> cells) {
        check(cells);

        boolean innerSeries = isFiringImmediately();
        if (innerSeries) startSeries();

        try { 
            List<B> bricks = new ArrayList<>(cells.size());
            cells.forEach((c) -> bricks.add(remove(c)));
            return bricks;
        }
        finally {
            if (innerSeries) stopSeries();
        }
    }

    public List<B> removeSome(Set<Point> cells, List<B> bricks) {
    	throw new UnsupportedOperationException();
    }
    
    /** Заменяет блоки во всех ячейках матрицы.
     * Заменяет N блоков в N ячеек.
     * Каждая ячейка должна содержать блок. Для добавления используйте {@link #addAll(Set, List)}.
     * Значения блоков не могут быть null. Для удаления используйте {@link #removeAll(Set)}.
     * @param cells Координаты целевых ячеек.
     * @param newBricks Заменяющие блоки.
     * @throws NullPointerException коллекции null, координаты-элемент коллекции null
	 * @throws OutOfBoundsException координаты выходят за границы
	 * @throws IllegalStateException любая из ячеек пуста
	 * @throws IllegalArgumentException коллекции пусты, размеры различаются,
	 * любой блок null, равен заменяемому
     */
    public void replaceAll(Set<Point> cells, List<B> newBricks) {         
        check(cells, newBricks);

        boolean innerSeries = isFiringImmediately();
        if (innerSeries) startSeries();

        try {
            Iterator<B> b = newBricks.iterator();
            cells.forEach((c) -> replace(c, b.next()));
        }
        finally {
            if (innerSeries) stopSeries();
        }
    }

    public void replaceSome(Set<Point> cells, List<B> newBricks) {
    	throw new UnsupportedOperationException();
    }
    
    /** Переносит блоки из всех ячеек матрицы в другие ячейки.
     * Переносит N блоков из N ячеек.
     * Каждая исходная ячейка должна содержать блок, соответствующая ей ячейка назначения
     * быть вакантной.
     * @param fromCells Координаты исходных ячеек.
     * @param toCells Координаты целевых ячеек.
     * @return true, если удалось перенести все N блоков. Иначе false.
     * @throws NullPointerException коллекции null, координаты-элемент коллекций null
	 * @throws OutOfBoundsException координаты исходных ячеек выходят за границы
	 * @throws IllegalStateException любая из исходных ячеек пуста
	 * @throws IllegalArgumentException коллекции пусты, размеры различаются
     */
    public boolean transferAll(Set<Point> fromCells, Set<Point> toCells) {
        check(fromCells, toCells);
        
        boolean innerSeries = isFiringImmediately();
        if (innerSeries) startSeries(); 

        try {
            List<B> bricks = removeAll(fromCells);
              
            Iterator<B> bit = bricks.iterator();
            for (Point c : toCells) {
                try {
                    add(c, bit.next());
                } 
                catch (OutOfBoundsException | IllegalStateException e) {
                    for (Point c2 : toCells) {
                        if (c2.equals(c)) {
                            addAll(fromCells, bricks);
                            return false;
                        }
                        remove(c2);
                    }
                }
            }
            return true;
        }
        finally {
            if (innerSeries) stopSeries();
        }
    }

    /** Переносит блоки из некоторых ячеек матрицы в другие ячейки.
     * Переносит M Блоков из N ячеек, M c [0, N].
     * Исходная ячейка может быть пустой, но для каждой ячейки с блоком соответствующая ей
     * ячейка назначения должна быть вакантной.
     * @param fromCells Координаты исходных ячеек.
     * @param toCells Координаты целевых ячеек.
     * @return Число перемещенных блоков.
     * @throws NullPointerException коллекции null, координаты-элемент коллекций null
	 * @throws OutOfBoundsException координаты исходных ячеек выходят за границы
	 * @throws IllegalArgumentException коллекции пусты, размеры различаются
     */
    public int transferSome(Set<Point> fromCells, Set<Point> toCells) {
        check(fromCells, toCells);  
        
        boolean innerSeries = isFiringImmediately();
        if (innerSeries) startSeries(); 
        
        try {  
            Set<Point> fromCells2  = new LinkedHashSet<>();
            Set<Point> toCells2    = new LinkedHashSet<>();   
            Iterator<Point> fromIt = fromCells.iterator();
            Iterator<Point> toIt   = toCells.iterator();
            get(fromCells).forEach((brick) -> {
                Point cfrom = fromIt.next();
                Point cto = toIt.next();
                if (brick != null) {
                    fromCells2.add(cfrom);
                    toCells2.add(cto);
                }
            });
            if (transferAll(fromCells2, toCells2))
                return fromCells2.size();
            return 0;
        }
        finally {
            if (innerSeries) stopSeries();
        }  
    }
    
    /** Подсчитывает блоки в ячейках всей матрицы.
     * @return Число не пустых ячеек.
     */
    public int count() {
    	return count(Cells.all(size()));
    }
    
    /** Подсчитывает блоки в ячейках матрицы.
     * @param cells Координаты проверяемых ячеек.
     * @return Число не пустых ячеек.
     * @throws NullPointerException коллекция, координаты-элемент коллекции null
	 * @throws OutOfBoundsException координаты выходят за границы
	 * @throws IllegalArgumentException коллекция пустая
     */
    public int count(Set<Point> cells) {
        int count = 0;
        for (B b : get(cells))
            if (b != null) count++;
        return count;
    }
    
    /** Подсчитывает определенные блоки в ячейках матрицы.
     * @param cells Координаты проверяемых ячеек.
     * @param block Блок для сравнения.
     * @return Число совпадений.
     * @throws NullPointerException коллекция, координаты-элемент коллекции null
	 * @throws OutOfBoundsException координаты выходят за границы
	 * @throws IllegalArgumentException коллекция пустая, блок null
     */
    public int count(Set<Point> cells, B brick) {
    	if (brick == null)
    		throw new IllegalArgumentException("блок null");
    	
        int count = 0;
        for (B b : get(cells))
            if (brick.equals(b)) count++;
        return count;
    }
    
  	/** Возвращает строковое представление матрицы.
    * Пример вывода для матрицы (Integer.class, new Dimension(6, 4)):
    * <pre>
    * {@code
    *     1    2    3 null null null
    *    10   20   30 null null null
    *   100 null null null null null
    *  null null null null null null
    * blocks: 7
    * }
    * </pre>
    * @return Форматированная строка для вывода на консоль.
    */
    @Override
    public String toString() {
    	return super.toString() + "blocks: " + count() + '\n';
    }
    
    // проверяет аргумент как коллекцию
    private void check(Set<Point> cells) {
    	if (cells == null)   
    		throw new NullPointerException("коллекция координат");
       	if (cells.isEmpty())
       		throw new IllegalArgumentException("коллекция координат: пустая");
    } 

    // проверяет аргументы как коллекции
    private void check(Set<Point> cells, List<B> bricks) {
    	check(cells);

       	if (bricks == null)  
       		throw new NullPointerException("коллекция блоков");
       	if (bricks.size() != cells.size()) 
       		throw new IllegalArgumentException("коллекции: размеры различаются");
    }  

    // проверяет аргументы как коллекции
    private void check(Set<Point> cells1, Set<Point> cells2) {
    	check(cells1);
    	check(cells2);
        
    	if (cells1.size() != cells2.size())
          	throw new IllegalArgumentException("коллекции: размеры различаются");   
    }
   
    public static void main(String[] args) {
   		BrickMatrixN<Integer> matrix = new BrickMatrixN<>(Integer.class, new Dimension(6, 4));
   		matrix.addChangeListener((e) -> {
   			System.out.println(e);
   			System.out.println(matrix);
   		});
   		
   		matrix.addAll(Cells.row(0, 3), Collections.nCopies(3, 0));
   		matrix.transferSome(Cells.row(0, 6), Cells.row(1, 6));
   }            
}
