package games;

import java.util.Collections;

import framework.matrix.model.bricks.Cells;
import framework.matrix.model.pieces.NotEnoughSpaceException;
import framework.matrix.model.pieces.Piece;
import framework.matrix.model.pieces.PieceCreator;
import framework.matrix.model.pieces.PieceMatrix;
import framework.matrix.model.pieces.RotatablePiece;

public class TetrisPieceCreator implements PieceCreator<TetrisBrick, TetrisPiece>
{
	@Override
	public Piece<TetrisBrick> create(PieceMatrix<TetrisBrick, TetrisPiece> matrix, TetrisPiece kind)
	  throws NotEnoughSpaceException 
	{
		switch (kind) {
			case I:
				return new RotatablePiece<>(matrix, Cells.create(5, 0, 5, 1, 5, 2, 5, 3), Collections.nCopies(4, TetrisBrick.I));
			case J:
				return new RotatablePiece<>(matrix, Cells.create(5, 0, 5, 1, 5, 2, 4, 2), Collections.nCopies(4, TetrisBrick.J));
			case L:
				return new RotatablePiece<>(matrix, Cells.create(4, 0, 4, 1, 4, 2, 5, 2), Collections.nCopies(4, TetrisBrick.L));
			case O:
				return new RotatablePiece<>(matrix, Cells.create(4, 0, 5, 0, 4, 1, 5, 1), Collections.nCopies(4, TetrisBrick.O));
			case S:
				return new RotatablePiece<>(matrix, Cells.create(5, 0, 6, 0, 4, 1, 5, 1), Collections.nCopies(4, TetrisBrick.S));
			case T:
				return new RotatablePiece<>(matrix, Cells.create(4, 0, 5, 0, 6, 0, 5, 1), Collections.nCopies(4, TetrisBrick.T));
			case Z:
				return new RotatablePiece<>(matrix, Cells.create(4, 0, 5, 0, 5, 1, 6, 1), Collections.nCopies(4, TetrisBrick.Z));
			default:
				throw new RuntimeException("сюда не попадем");
		}
		
	}

}
