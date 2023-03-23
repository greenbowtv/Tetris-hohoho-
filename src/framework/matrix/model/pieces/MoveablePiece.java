package framework.matrix.model.pieces;

import java.awt.Dimension;
import java.awt.Point;
import java.util.*;

import framework.matrix.model.array.OutOfBoundsException;
import framework.matrix.model.bricks.Cells;

public class MoveablePiece<B> extends Piece<B>
{
	/** Конструирует пустую фигуру.
	 * Исходя из определения, такая фигура не существует с самого начала.
	 */
	public MoveablePiece() {
		super();
	}
	
	/** Конструирует фигуру.
     * @param matrix Матрица, в которую добавляется фигура.
     * @param cells Координаты целевых ячеек.
     * @param bricks Добавляемые блоки.
	 * @throws NotEnoughSpaceException не хватило места в матрице
	 * @throws NullPointerException аргументы null, координаты-элемент коллекции null
	 * @throws OutOfBoundsException координаты выходят за границы
	 * @throws IllegalArgumentException коллекции пусты, размеры различаются, блоки null
	 */
	public MoveablePiece(PieceMatrix<B, ?> matrix, Set<Point> cells, List<B> bricks) 
	  throws NotEnoughSpaceException 
	{
		super(matrix, cells, bricks);
	}	
	
	/**{@inheritDoc}
	 * @throws IllegalStateException фигура не существует
	 */
	@Override
	public boolean move(Direction dir) {
		if (!isExist())
			throw new IllegalStateException("фигура не существует");
        
		Set<Point> toCells = copyCells();
		toCells.forEach((c) -> {
            switch (dir) {
                case LEFT:  c.x --; break;
                case RIGHT: c.x ++; break;
                case UP:    c.y --; break;
                case DOWN:  c.y ++; break;
            }
        });
		
		unlock(true);
		boolean moved = matrix.transferAll(cells, toCells);
		unlock(false);
		
		if (moved) {
			cells = toCells;
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		PieceMatrix<Integer, Integer> matrix = new PieceMatrix<>(
		  Integer.class, new Dimension(6, 4), new CreatorStub2());
		
		Piece<Integer> piece = null;
		try {
			piece = matrix.create(0);	System.out.println(matrix);	
			
		} catch (NotEnoughSpaceException e) {
			System.out.println(e);
		}
		piece.move(Direction.DOWN);		System.out.println(matrix);
		piece.move(Direction.DOWN);		System.out.println(matrix);
		piece.move(Direction.DOWN);		System.out.println(matrix);
		piece.move(Direction.DOWN);		System.out.println(matrix);
	}
}

class CreatorStub2 implements PieceCreator<Integer, Integer> 
{
	@SuppressWarnings("serial")
	@Override
	public Piece<Integer> create(PieceMatrix<Integer, Integer> matrix, Integer kind) throws NotEnoughSpaceException {
		switch (kind) {
			case 0:
				return new MoveablePiece<>(matrix, Cells.create(0, 0, 1, 0, 2, 0), new ArrayList<Integer>(){{add(1); add(2); add(3);}});
			default:
				throw new IllegalArgumentException();
		}
	}
}
