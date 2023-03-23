package framework.matrix.view;

import java.awt.*;

/** Рисовальщик простейших прямоугольников.
 * Прямоугольники без контура, без скругления углов, просто монохромная заливка области.
 * Значения блоков null никак не визуализируются (прозрачный цвет).
 */
public class PlainRectangle extends BicolourBricks      
{
    /** Конструктор рисовальщика серых блоков.
     */
    public PlainRectangle() {
    	super();
    }

    /** Конструктор с пользовательским цветом.
     * @param c Цвет блоков.
     * @throws IllegalArgumentException цвет null
     */
    public PlainRectangle(Color c) {
    	super();
        setBrickColor(c);
    }

    /** {@inheritDoc}
     */
    @Override @Deprecated
    public final void setNullColor(Color c) {
        throw new UnsupportedOperationException();
    }
    
    /** {@inheritDoc}
     */
    @Override
    public void paint(Graphics2D g, Rectangle area, Object brick) {
        if (area.width < 0 || area.height < 0)
            return;

        if (brick != null) {
            g.setColor(getBrickColor());
            g.fillRect(area.x, area.y, area.width, area.height);
        }
    }
}
