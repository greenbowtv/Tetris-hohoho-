package framework.matrix.view;

import java.awt.*;

/** Основа двухцветного рисовальщика блоков Матрицы.
 * Все блоки должны визуализируются одним цвета, отсутствие блока (null) - другим.
 * Всегда заданы оба цвета, IllegalState ошибка в {@link #paint} невозможна.
 */
public abstract class BicolourBricks implements BrickPainter
{
    private Color brickColor;
    private Color nullColor;

    /** Конструктор с серым и прозрачным цветами.
     */
    public BicolourBricks() {
        brickColor = Color.GRAY;
        nullColor = new Color(0, 0, 0, 0);
    }
    
    /** Конструктор рисовальщика.
     * @param brickColor Цвет рисования блоков.
     * @param nullColor Цвет визуализации null значения.
     * @throws IllegalArgumentException цвета null
     */
    public BicolourBricks(Color brickColor, Color nullColor) {
        setBrickColor(brickColor);
        setNullColor(nullColor);
    }

    /** Возвращает цвет блоков.
     * @return 
     */
    public Color getBrickColor() {
        return brickColor;
    }

    /** Устанавливает цвет блоков.
     * Вызовете {@link MatrixView#repaint()} после изменения цвета.
     * @param c
     * @throws IllegalArgumentException цвет null
     */
    public void setBrickColor(Color c) {
        if (c == null)
            throw new IllegalArgumentException();
        this.brickColor = c;
    }

    /** Возвращает цвет визуализации null значения.
     * @return 
     */
    public Color getNullColor() {
        return nullColor;
    }

    /** Устанавливает цвет визуализации null значения.
     * Вызовете {@link MatrixView#repaint()} после изменения цвета.
     * @param c
     * @throws IllegalArgumentException цвет null
     */
    public void setNullColor(Color c) {
        if (c == null)
            throw new IllegalArgumentException();
        this.nullColor = c;
    }
}
