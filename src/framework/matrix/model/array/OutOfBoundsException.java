package framework.matrix.model.array;

import java.awt.Point;

/** Ошибка выхода координат элемента 2D массива за границы.
 * 
 * @author Игорь
 */
public class OutOfBoundsException extends RuntimeException
{
	private static final long serialVersionUID = -519659368780180705L;

	public OutOfBoundsException(Point p) {
		super(String.format("координаты (%d, %d)", p.x, p.y));
	}

}
