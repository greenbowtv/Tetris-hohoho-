package framework.matrix.view;

import java.awt.*;

/** Рисовальщик монохромного фона.
 * 
 * @author Игорь
 */
public class ColorBackground implements BackgroundPainter 
{
	private Color color;
        
    /** Конструктор белого фона.
     */
    public ColorBackground() {
        color = Color.WHITE;
    }
    
    /** Конструктор фона пользовательского цвета.
     * @param c
     * @throws IllegalArgumentException аргумент null
     */
    public ColorBackground(Color c) {
        setColor(c);
    }
        
    /** Возвращает цвет фона.
     * @return
     */
    public Color getColor() {
        return color;
    }
    
    /** Устанавливает цвет фона.
     * Вызовете {@link MatrixView#repaint()} после изменения цвета.
     * @param c
     * @throws IllegalArgumentException аргумент null
     */
    public void setColor(Color c) {
        if (c == null) 
            throw new IllegalArgumentException();
        this.color = c;
    }
 
    /** {@inheritDoc}
     */
    @Override
    public void paint(Graphics2D g, Dimension size) {
        g.setColor(color);
        g.fillRect(0, 0, size.width, size.height);
    }

    /** {@inheritDoc}
     */
    @Override
    public void paintPart(Graphics2D g, Rectangle area) {
        g.setColor(color);
        g.fillRect(area.x, area.y, area.width, area.height);
    }   
}