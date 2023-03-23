package framework.matrix.model.pieces;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.*;

import framework.matrix.model.array.OutOfBoundsException;
import framework.matrix.model.bricks.Cells;

/** Вращаемая фигура.
 * 
 * Если только ширина или только высота фигуры четные, тогда координаты ее центра будут
 * кратны 0.5, при поворотах 0 → 90* и 180* → 270* координаты блоков также станут дробными.
 * Но исходя из определения, блок не может одновременно находиться сразу в 4-х ячейках
 * матрицы! Координаты должны быть округлены, т. е. для углов 90 и 270 должна быть внесена
 * ошибка значений, которая живет до следующего поворота. Возможны 4 режима округления:
 * 1.5, 1.5 → 1.0, 1.0	ceilX- ceilY-
 * 1.5, 1.5 → 1.0, 2.0  ceilX- ceilY+
 * 1.5, 1.5 → 2.0, 1.0  ceilX+ ceilY-
 * 1.5, 1.5 → 2.0, 2.0  ceilX+ ceilY+
 * Однако и этого может оказаться недостаточно. Вглядевшись в диаграммы поворота фигурок
 * классического тетриса заметим, что на углах 180 градусов, т.е. когда ошибки быть не может,
 * фигурка располагается не совсем там где ей следует с математической точки зрения. Поэтому 
 * в общем случае коррекция должна задаваться для всех углов и является коррекцией сдвига-поворота.
 * 
 * Расчетные формулы:
 * X' = ((Y - error.Y) - center.Y) + center.X + offset.X
 * Y' = ((X - error.X) - center.X) + center.Y + offset.Y
 * error = offset

 * @param <B> Тип блоков.
 */
public class RotatablePiece<B> extends MoveablePiece<B>
{
    private Point2D.Double   center;	// координаты центра фигуры
    private Point2D.Double[] offsets;   // корректирующие значения координат для углов 90, 180, 270 и 0
    private Point2D.Double   error;     // ошибка значений координат, существует до следующего поворота
    private int              angle;     // [град] угол поворота
    
	/** Конструктор пустой фигуры.
	 * Исходя из определения, такая фигура не существует с самого начала.
	 */
    public RotatablePiece() {
    	super();
    }
     
    /** Конструктор фигуры, координаты округляются в меньшую сторону на углах 90* и 270*.
     * Округление имеет смысл только если координаты центра дробные.
     * @param matrix Матрица, в которую добавляется фигура.
     * @param cells Координаты целевых ячеек.
     * @param bricks Добавляемые блоки.
	 * @throws NotEnoughSpaceException не хватило места в матрице
	 * @throws NullPointerException аргументы null, координаты-элемент коллекции null
	 * @throws OutOfBoundsException координаты выходят за границы
	 * @throws IllegalArgumentException коллекции пусты, размеры различаются, блоки null
     */
    public RotatablePiece(PieceMatrix<B, ?> matrix, Set<Point> cells, List<B> bricks) 
	  throws NotEnoughSpaceException 
    {
    	this(matrix, cells, bricks, false, false);
    }
    
    /** Конструктор фигуры, задаются корректоры поворота для углов 90* и 270*.
     * Округление имеет смысл только если координаты центра дробные.
     * @param matrix Матрица, в которую добавляется фигура.
     * @param cells Координаты целевых ячеек.
     * @param bricks Добавляемые блоки.
     * @param ceilX Округлять X в большую сторону?
     * @param ceilY Округлять Y в большую сторону?
	 * @throws NotEnoughSpaceException не хватило места в матрице
	 * @throws NullPointerException аргументы null, координаты-элемент коллекции null
	 * @throws OutOfBoundsException координаты выходят за границы
	 * @throws IllegalArgumentException коллекции пусты, размеры различаются, блоки null
     */
    public RotatablePiece(PieceMatrix<B, ?> matrix, Set<Point> cells, List<B> bricks,
      boolean ceilX, boolean ceilY) throws NotEnoughSpaceException
    {
    	super(matrix, cells, bricks);
    	
    	Rectangle area = getArea();
    	this.center = new Point2D.Double(area.x + 0.5 * (area.width - 1), area.y + 0.5 * (area.height - 1));
    	boolean round = (center.x + center.y) % 1.0 == 0.5;
    	this.offsets = new Point2D.Double[] {
            new Point2D.Double(round ? ceilX ? 0.5 : -0.5 : 0, round ? ceilX ? 0.5 : -0.5 : 0),
            new Point2D.Double(0, 0),
            new Point2D.Double(round ? ceilX ? 0.5 : -0.5 : 0, round ? ceilX ? 0.5 : -0.5 : 0),
            new Point2D.Double(0, 0)
        }; 
        this.error = new Point2D.Double();
        this.angle = 0;	
	}
    
    /** Конструктор фигуры, задаются корректоры сдвига-поворота для всех углов.
     * Корректирующие значения используются в независимости от значений координат центра.
     * @param matrix Матрица, в которую добавляется фигура.
     * @param cells Координаты целевых ячеек.
     * @param bricks Добавляемые блоки.
     * @param offsets Корректоры сдвига-поворота.
	 * @throws NotEnoughSpaceException не хватило места в матрице
	 * @throws NullPointerException аргументы null, координаты-элемент коллекции null
	 * @throws OutOfBoundsException координаты выходят за границы
	 * @throws IllegalArgumentException коллекции пусты, размеры различаются, блоки null
     */
    public RotatablePiece(PieceMatrix<B, ?> matrix, Set<Point> cells, List<B> bricks,
      Point2D.Double[] offsets) throws NotEnoughSpaceException
    {
        super (matrix, cells, bricks);

        if (offsets.length != 4 ||
            offsets[0].x % 0.5 != 0 || offsets[0].y % 0.5 != 0 ||
            offsets[1].x % 0.5 != 0 || offsets[1].y % 0.5 != 0 ||
            offsets[2].x % 0.5 != 0 || offsets[2].y % 0.5 != 0 ||
            offsets[3].x % 0.5 != 0 || offsets[3].y % 0.5 != 0)
        {
            throw new IllegalArgumentException();
        }
        Rectangle area = getArea();
        this.center = new Point2D.Double(area.x + 0.5 * (area.width - 1), area.y + 0.5 * (area.height - 1));
        this.offsets = offsets;
        this.error = new Point2D.Double();
        this.angle = 0;
    }

	/**{@inheritDoc}
	 * @throws IllegalStateException фигура не существует
	 */
    @Override
    public boolean rotate() { 
		if (!isExist())
			throw new IllegalStateException("фигура не существует");

        Set<Point> toCells = new LinkedHashSet<>(cells.size(), 1);
        Point2D.Double offset = offsets[angle / 90];
        cells.forEach((c) -> { 
            double x =  ((c.y - error.y) - center.y) + center.x + offset.x;
            double y = -((c.x - error.x) - center.x) + center.y + offset.y;
            if (x % 1.0 != 0) throw new ArithmeticException("x = " + x);
            if (y % 1.0 != 0) throw new ArithmeticException("y = " + y);
            toCells.add(new Point((int)x, (int)y));
        });
        
		unlock(true);
		boolean rotated = matrix.transferAll(cells, toCells);
		unlock(false);
            
        if (rotated) {
        	cells = toCells;
            angle = (angle + 90) % 360;
            error = offset;
            return true;
        }
        return false;
    }

	/**{@inheritDoc}
	 * @throws IllegalStateException фигура не существует
	 */
    @Override
    public boolean move(Direction dir) {
        if (super.move(dir)) {
            switch (dir) {
                case LEFT:  center.x --; break;
                case RIGHT: center.x ++; break;
                case UP:    center.y --; break;
                case DOWN:  center.y ++; break;
            }
            return true;
        }
        return false;
    }
    
  	/** Возвращает строковое представление фигуры.
    * Примеры вывода:
    * <pre>
    * {@code
    * RotatablePiece [
    *  cells 5: (0, 1) (1, 1) (2, 1) (0, 2) (2, 2)
    *  разблокировка выкл
    *  center (1.0, 1.5)
    *  offsets (-0.5, -0.5) (0.0, 0.0) (-0.5, -0.5) (0.0, 0.0)
    *  error (0.0, 0.0)
    *  angle 0
    * ]
    * 
    * RotatablePiece [
    *  cells 5: (0, 2) (0, 1) (0, 0) (1, 2) (1, 0)
    *  разблокировка выкл
    *  center (1.0, 1.5)
    *  offsets (-0.5, -0.5) (0.0, 0.0) (-0.5, -0.5) (0.0, 0.0)
    *  error (-0.5, -0.5)
    *  angle 90
    * ]
    * 
    * RotatablePiece [
    *  cells 5: (2, 2) (1, 2) (0, 2) (2, 1) (0, 1)
    *  разблокировка выкл
    *  center (1.0, 1.5)
    *  offsets (-0.5, -0.5) (0.0, 0.0) (-0.5, -0.5) (0.0, 0.0)
    *  error (0.0, 0.0)
    *  angle 180
    * ]
    * }
    * </pre>
    * @return Форматированная строка для вывода на консоль.
    */
    @Override
    public String toString() {
    	if (!isExist())
    		return super.toString();
    	
    	String str = super.toString();
    	return str.substring(0, str.length() - 1) +
		  String.format(Locale.ROOT, " center (%.1f, %.1f)\n", center.x, center.y) +
		  String.format(Locale.ROOT, " offsets (%.1f, %.1f) (%.1f, %.1f) (%.1f, %.1f) (%.1f, %.1f)\n", 
		    offsets[0].x, offsets[0].y, offsets[1].x, offsets[1].y, offsets[2].x, offsets[2].y, offsets[3].x, offsets[3].y) +
		  String.format(Locale.ROOT, " error (%.1f, %.1f)\n", error.x, error.y) +
		  String.format(" angle %d\n]", angle);
    }
        
	public static void main(String[] args) {
		PieceMatrix<Integer, Integer> matrix = new PieceMatrix<>(Integer.class, new Dimension(6, 4), new CreatorStub3());
		
		Piece<Integer> piece = null;
		try {
			piece = matrix.create(0);	System.out.println(matrix);
		}
		catch (NotEnoughSpaceException e) {
			System.out.println(e);
		}
		
		piece.move(Direction.DOWN);		System.out.println(piece);	System.out.println(matrix);
		piece.rotate();					System.out.println(piece);	System.out.println(matrix);
		piece.rotate();					System.out.println(piece);	System.out.println(matrix);
		piece.rotate();					System.out.println(piece);	System.out.println(matrix);
		piece.rotate();					System.out.println(piece);	System.out.println(matrix);
	}
}

class CreatorStub3 implements PieceCreator<Integer, Integer> 
{
	@Override
	public Piece<Integer> create(PieceMatrix<Integer, Integer> matrix, Integer kind) throws NotEnoughSpaceException {
		switch (kind) {
			case 0:
				return new RotatablePiece<>(matrix, Cells.create(0, 0, 1, 0, 2, 0, 0, 1, 2, 1), Collections.nCopies(5, 0));
			default:
				throw new IllegalArgumentException();
		}
	}
}
