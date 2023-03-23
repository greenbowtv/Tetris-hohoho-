package framework.matrix.view;

import java.awt.*;
import java.util.Map;

/** Рисовальщик простейших разноцветных прямоугольников.
 * Прямоугольники без контура, без скругления углов, просто монохромная заливка области.
 * Каждый блок рисуется своим цветом, даже для null значений определен цвет.
 */
public class PlainRectangles extends MulticolorBricks
{
    /** Конструктор рисовальщика.
     * @param colors Цветовые настройки.
     * @throws NullPointerException коллекция или элемент коллекции null
     * @throws IllegalArgumentException размер коллекции, нет null ключа
     */
    public PlainRectangles(Map<Object, Color> colors) {
        super(colors);
    }

    /** {@inheritDoc}
     * @throws IllegalStateException для блока цвет не задан
     */
    @Override
    public void paint(Graphics2D g, Rectangle area, Object brick) {
        if (area.width < 0 || area.height < 0)
            return;

        Color color = colorOf(brick);
        if (color == null)
            throw new IllegalStateException("для " + brick + " цвет не задан");
        if (color.getAlpha() == 0)
            return;
        g.setColor(color);
        g.fillRect(area.x, area.y, area.width, area.height);
    }
}
