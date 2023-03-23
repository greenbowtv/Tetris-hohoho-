package framework.matrix.view;

import java.awt.*;

/** Инструмент разделения ячеек Матрицы линиями.
 * Переопределяет {@link #actualPaint} для рисования сплошных линий одинаковых
 * цвета и толщины, с 4-х сторон обводящих каждый блок. Толщина линии = толщина
 * разделителя.
 */
public class LineSeparator extends EmptySeparator
{
	private Color color;
	
    /** Конструирует разделитель со светлосерыми линиями толщины 1.
     * @param matrixSize Размеры Матрицы.
     * @throws NullPointerException размеры null
     * @throws IllegalArgumentException размеры не натуральные числа
     */
    public LineSeparator(Dimension matrixSize) {
        this(matrixSize, 1, Color.LIGHT_GRAY);
    }

    /** Конструирует разделитель со светлосерыми линиями.
     * @param matrixSize Размеры Матрицы.
     * @param thickness Толщина линий.
     * @throws NullPointerException размеры null
     * @throws IllegalArgumentException размеры не натуральные, толщина отрицательная
     */
    public LineSeparator(Dimension matrixSize, int thickness) {
        this(matrixSize, thickness, Color.LIGHT_GRAY);
    }
        
    /** Конструирует разделитель.
     * @param matrixSize Размеры Матрицы.
     * @param thickness Толщина линий.
     * @param color Цвет линий.
     * @throws NullPointerException размеры, цвет null
     * @throws IllegalArgumentException размеры не натуральные, толщина отрицательная
     */
	public LineSeparator(Dimension matrixSize, int thickness, Color color) {
        super(matrixSize, thickness);
        setColor(color);
    }     
        
    /** Возвращает цвет линий.
     * @return
     */
	public Color getColor() {
        return color;
	}

    /** Устанавливает цвет линий.
     * После изменения цвета вызовете {@link MatrixView#repaint}.
     * @param color
     * @throws NullPointerException цвет null
     */
	public void setColor(Color color) {
        if (color == null)
            throw new NullPointerException();   
        this.color = color;
	}

    /** Рисует линии.
     */
    @Override
    protected void actualPaint(Graphics2D g, Rectangle area) 
    {
        g.setClip(area);
        g.setColor(color);
        g.setStroke(new BasicStroke(getThickness()));
        int x1, y1, x2, y2;
        int halfThickness = (int)Math.ceil(getThickness() / 2.0);

        // горизонтальные линии
        x1 = area.x;
        x2 = area.x + area.width;
        for (int y : cellY) {
            y1 = y2 = y - halfThickness;
            //g.draw(new Line2D.Float(x1, y1, x2, y2));
            g.drawLine(x1, y1, x2, y2);
        }
        y1 = y2 = area.y + area.height - halfThickness;
        g.drawLine(x1, y1, x2, y2);

        // вертикальные линии
        y1 = area.y;
        y2 = area.y + area.height;
        for (int x : cellX) {
            x1 = x2 = x - halfThickness;
            g.drawLine(x1, y1, x2, y2);
        }
        x1 = x2 = area.x + area.width - halfThickness;
        g.drawLine(x1, y1, x2, y2);
        
        g.setClip(null);
    }       
}
