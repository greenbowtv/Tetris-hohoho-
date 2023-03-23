package framework.matrix.view;

import java.awt.*;
import java.awt.geom.*;
import java.util.Map;

/** Рисовальщик настраиваемых разноцветных прямоугольников.
 * У подобных прямоугольников скруглены углы и имеется контур. Цвет внутреннего -
 * цвет рисования блок, цвет контура выводится из предыдущего. Поддерживается сглаживание
 * кривых.
 */
public class RichRectangles extends MulticolorBricks
{
    private int     arcRadius;          // внешний радиус скругления углов
    private int     contourThickness;   // толщина контура
    private int     contourDarkness ;   // показатель затемнения контура
    private boolean antialising;        // флаг использования сглаживания
    private int     minimumSize;        // минимальный размер для корректной отрисовки

    /** Конструктор рисовальщика с оптимальными параметрами.
     * @param colors Цветовые настройки блоков.
     * @throws NullPointerException коллекция или элемент коллекции null
     * @throws IllegalArgumentException размер коллекции, нет null ключа
     */
    public RichRectangles(Map<Object, Color> colors) {
        super(colors);
        arcRadius        = 4;
        contourThickness = 4;
        contourDarkness  = 2;
        antialising      = true;
        minimumSize      = 2 * Math.max(arcRadius, contourThickness);
    }

    /** Возвращает внешний радиус скругления углов.
     * @return 
     */
    public int getArcRadius() {
        return arcRadius;
    }

    /** Устанавливает внешний радиус скругления углов.
     * Вызовете {@link MatrixView#repaint()} после изменения радиуса.
     * @param radius
     * @throws IllegalArgumentException радиус < 0
     */
    public void setArcRadius(int radius) {
        if (radius < 0)
            throw new IllegalArgumentException();
        
        this.arcRadius = radius;
        minimumSize = 2 * Math.max(arcRadius, contourThickness);
    }

    /** Возвращает толщину контура.
     * @return 
     */
    public int getContourThickness() {
        return contourThickness;
    }

    /** Устанавливает толщину контура.
     * Вызовете {@link MatrixView#repaint()} после изменения толщины.
     * @param thickness
     * @throws IllegalArgumentException толщина < 0
     */
    public void setContourThickness(int thickness) {
        if (thickness < 0)
            throw new IllegalArgumentException();
        
        this.contourThickness = thickness;
        minimumSize = 2 * Math.max(arcRadius, contourThickness);
    }

    /** Возвращает показатель затемнения контура.
     * @return 
     */
    public int getContourDarkness() {
        return contourDarkness;
    }

    /** Устанавливает показатель затемнения контура.
     * Вызовете {@link MatrixView#repaint()} после изменения показателя.
     * Положительное значение N сводится к N вызовам {@link Color#darker()},
     * отрицательно к вызовам {@link Color#brighter()}. Иметь в виду, что
     * brighter() наращивает компоненты цвета медленнее и только в диапазоне 
     * [1, 255], т.е. например красный цвет нужно задавать {@code new Color(255, 1, 1)}.
     * @param darkness
     */
    public void setContourDarkness(int darkness) {
        this.contourDarkness = darkness;
    }
    
    /** Возвращает флаг сглаживания.
     * @return 
     */
    public boolean isAntialising() {
        return antialising;
    }
    
    /** Изменяет флаг сглаживания.
     * Вызовете {@link MatrixView#repaint()} после изменения флага.
     * @param aFlag
     */
    public void setAntialising(boolean aFlag) {
        this.antialising = aFlag;
    }   
    
    /** {@inheritDoc}
     * @throws IllegalStateException для блока цвет не задан
     */
    @Override
    public void paint(Graphics2D g, Rectangle area, Object brick) {
        if (area.width < minimumSize || area.height < minimumSize)
            return;
        
        Color innerColor = colorOf(brick);
        if (innerColor == null)
            throw new IllegalStateException("для " + brick + " цвет не задан");
        if (innerColor.getAlpha() == 0)
            return;
        Color contourColor = innerColor;
        for (int i = 0; i < contourDarkness; i++)
            contourColor = contourColor.darker();
        for (int i = 0; i > contourDarkness; i--)
            contourColor = contourColor.brighter();
        
        Shape bounds = new RoundRectangle2D.Float(
          area.x,
          area.y,
          area.width,
          area.height,
          2 * arcRadius,
          2 * arcRadius
        );
        int innerDiam = 2 * arcRadius - contourThickness;
        if (innerDiam < 0) innerDiam = 0;
        Shape innerSpace = new RoundRectangle2D.Float(
          area.x + contourThickness,
          area.y + contourThickness,
          area.width - 2 * contourThickness,
          area.height - 2 * contourThickness,
          innerDiam,
          innerDiam
        );

        if (antialising)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Path2D path = new Path2D.Double(Path2D.WIND_EVEN_ODD);
        path.append(bounds, false);
        path.append(innerSpace, false);
        g.clip(path);
        g.setColor(contourColor);
        g.fill(bounds);
        
        g.setClip(null);
        g.setColor(innerColor);
    	g.fill(innerSpace);
    }      
}
