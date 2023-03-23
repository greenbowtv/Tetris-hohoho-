package framework.matrix.model.pieces;

/** Интерфейс создателя фигуры.
 * 
 * @author Игорь
 * @param <B> Тип блоков.
 * @param <S> Тип фигур.
 */
public interface PieceCreator<B, S>
{
	/** Создает фигуру.
	 * @param matrix Матрица, в которую добавляется фигура.
	 * @param kind Разновидность создаваемой фигуры.
	 * @return Созданная фигура.
	 * @throws NotEnoughSpaceException не хватило места в матрице
	 * @throws NullPointerException разновидность null
	 * @throws IllegalArgumentException разновидность недопустимое значение
	 */
	Piece<B> create(PieceMatrix<B, S> matrix, S kind) throws NotEnoughSpaceException;
}
