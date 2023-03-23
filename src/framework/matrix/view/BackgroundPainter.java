package framework.matrix.view;

import java.awt.*;

/** Инструмент рисования фона компонента визуализации.
 * Поддерживает оптимизацию рисования. Чтобы нарисовать часть, нужно
 * знать размеры компонента, а они сообщаются {@link #paint}. Допускается
 * выброс исключения если {@link #paintPart} вызван первым.
 * 
 * @author Игорь
 */
public interface BackgroundPainter
{
	/** Рисует весь фон целиком.
	 * Область рисования - вся поверхность компонента начиная с (0, 0).
	 * @param g Контекст рисования буфера, не null.
	 * @param size Размеры компонента, не null, натуральные числа.
	 */
	void paint(Graphics2D g, Dimension size);
     
	/** Рисует часть фона: область одной ячейки.
	 * @param g Контекст рисования буфера, не null.
	 * @param area Область рисования, не null, размеры не отрицательные.
	 * @throws IllegalStateException {@link #paint} не вызывался ни разу
	 */
	void paintPart(Graphics2D g, Rectangle area);
}
