package games;

import java.awt.Rectangle;

import framework.game.Game;
import framework.matrix.model.pieces.Direction;
import framework.matrix.model.pieces.NotEnoughSpaceException;
import framework.matrix.model.pieces.Piece;
import framework.matrix.model.bricks.Cells;

public class Tetris extends Game<TetrisBrick, TetrisPiece> 
{
	private Piece<TetrisBrick> piece;		// текущая фигура
	private TetrisPiece 	   nextPiece;	// разновидность следующей фигуры
	
	public Tetris() {
		super(TetrisBrick.class, new TetrisPieceCreator());
		piece = new Piece<>();
		nextPiece = draw();
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void moveLeft() {
		if (getState() == State.ACTIVE && piece.isExist())
			piece.move(Direction.LEFT);		
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void moveRight() {
		if (getState() == State.ACTIVE && piece.isExist())
			piece.move(Direction.RIGHT);	
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void moveDown() {
		if (getState() == State.ACTIVE) {
			if (piece.isExist()) {
				boolean moved = piece.move(Direction.DOWN);
				if (!moved) {
					Rectangle area = piece.getArea();
					piece.destroy();
					
					int count = 0;
					for (int y = area.y; y < area.y + area.height; y++) {
						if (matrix.containsAll(Cells.row(y))) {
							matrix.removeAll(Cells.row(y));
							matrix.transferSome(
							  Cells.area(0, count, matrix.size().width, y - count),
							  Cells.area(0, count + 1, matrix.size().width, y - count));
							count++;
						}
					}
					
					if (count == 0)
						moveDown();
				}
			}
			else {
				try {
					piece = matrix.create(nextPiece);
					nextPiece = draw();
				} catch (NotEnoughSpaceException e) {
					stop();
					fireGameOver(false);
				}
			}
		}
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void rotate() {
		if (getState() == State.ACTIVE && piece.isExist())
			piece.rotate();	
	}
	
	/** {@inheritDoc} 
	 * Двигает фигуру вниз.
	 */
	@Override
	protected void onTimerTick() {
		moveDown();
	}
	
	/** Разыгрывает фигуру.
	 * @return Разновидность следующей фигуру.
	 */
	private TetrisPiece draw() {
		return TetrisPiece.values()[random.nextInt(TetrisPiece.values().length)];
	}
	
	/** Возвращает название игры.
	 * @return
	 */
	@Override
	public String toString() {
		return "Тетрис";
	}
}
