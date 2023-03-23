package framework.matrix.view;

import java.awt.*;

/** Рисовальщик пикселей ЖК-дисплей консоли.
 * Структура: контур, зазор, центральный квадрат. Задаются относительные величины
 * первых двух, их сумма не должна превышать 0.5. По умолчанию, оба цвета черные: 
 * с почти максимальной и минимальной alpha. Используя цвета по умолчания, используйте
 * и рекомендованный цвета фона.
 */
public class LCDPixel extends BicolourBricks
{
    public static final Color BACKGROUND_COLOR = new Color(160, 180, 170);

    private double contourThickness;    // толщина контура
    private double gapThickness;        // ширина зазора между контуром и центральной частью
    private int    minimumSize;         // минимальный размер для корректной отрисовки

    /** Коструирует рисовальщик с оптимальными цветами и параметрами геометрии.
     */
    public LCDPixel() {
        this(new Color(0, 0, 0, 230), new Color(0, 0, 0, 30), 0.15, 0.15);
    }

    /** Конструирует рисовальщик.
     * @param onColor Цвет включенного пикселя.
     * @param offColor Цвет выключенного пикселя.
     * @param contourThickness Толщина контура.
     * @param gapThickness Ширина зазора.
     * @throws IllegalArgumentException цвета null; параметры не положительные,
     * сумма толщины и ширины > 0.5
     */
    public LCDPixel(Color onColor, Color offColor, double contourThickness, double gapThickness) {
        super(onColor, offColor);
        setContourThickness(contourThickness);
        setGapThickness(gapThickness);
    }

    /** Возвращает толщину контура.
     * @return 
     */
    public double getContourThickness() {
        return contourThickness;
    }

    /** Устанавливает толщину контура.
     * Вызовете {@link MatrixView#repaint()} после изменения толщины.
     * @param thickness
     * IllegalArgumentException толщина не положительная, 
     * сумма с шириной зазора > 0.5
     */
    public void setContourThickness(double thickness) {
        if (thickness <= 0 || (thickness + gapThickness) > 0.5)
            throw new IllegalArgumentException();
        
        this.contourThickness = thickness;
        minimumSize = (int)Math.ceil(Math.max(1/contourThickness, 1/gapThickness));
    }

    /** Возвращает ширину зазора.
     * @return 
     */
    public double getGapThickness() {
        return gapThickness;
    }

    /** Устанавливает ширину зазора.
     * Вызовете {@link MatrixView#repaint()} после изменения ширины.
     * @param thickness
     * IllegalArgumentException ширина не положительная,
     * сумма с толщина контура > 0.5
     */
    public void setGapThickness(double thickness) {
        if (thickness <= 0 || (thickness + contourThickness) > 0.5)
            throw new IllegalArgumentException();
        
        this.gapThickness = thickness;
        minimumSize = (int)Math.ceil(Math.max(1/contourThickness, 1/gapThickness));
    } 

    @Override
    public void paint(Graphics2D g2d, Rectangle area, Object brick) {
        int min = Math.min(area.width, area.height);
        if (min < minimumSize)
            return;

        int contour = (int)Math.round(contourThickness * min);
        int gap = (int)Math.round(gapThickness * min);

        g2d.setColor(brick != null ? getBrickColor() : getNullColor());
        g2d.setStroke(new BasicStroke(contour));
        g2d.drawRect(
          area.x + contour/2, 
          area.y + contour/2, 
          area.width - contour, 
          area.height - contour);
        g2d.fillRect(
          area.x + (contour + gap),
          area.y + (contour + gap),
          area.width - 2 * (contour + gap),
          area.height - 2 * (contour + gap)); 
    }
}
