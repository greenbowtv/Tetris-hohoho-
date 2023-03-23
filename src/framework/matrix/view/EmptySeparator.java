package framework.matrix.view;

import java.awt.*;

/** Инструмент разделения ячеек Матрицы пустым пространством. 
 * Решает задачу расчета геометрии, но ничего не рисует.
 * Область ячейки зависит от размеров Матрицы, толщины разделителя и области
 * отведенной под рисунок матрицы. Последняя передается в {@link #paint},
 * в котором выполняются все расчеты, а фактически рисование выполняется в
 * методе {@link #actualPaint}.
 * Из-за требований одинаковых ширины столбцов (высоты строк) и не выхода рисунка
 * за границы, в общем случае справа-снизу остается пустое пространство.
 * 
 * @author Игорь
 */
public class EmptySeparator implements CellsSeparator 
{
    private   Dimension matrixSize;         // размеры Матрицы
	private   int       thickness;          // толщина разделителя
    private   Rectangle paintingArea;       // область рисования
    private   Rectangle actualPaintingArea; // фактическая область рисования
    private   boolean   needActualPaint;    // флаг разрешающий фактическое рисование                   
	protected int[]     cellX;				// абсциссы ячеек
	protected int[]     cellY;				// ординаты ячеек 
	protected Dimension cellSize;			// размеры ячеек

    /** Конструирует разделитель единичной толщины.
     * @param arraySize Размеры Матрицы.
     * @throws NullPointerException размеры null
     * @throws IllegalArgumentException размеры не натуральные
     */
	public EmptySeparator(Dimension arraySize) {
        this(arraySize, 1);
    }

    /** Конструирует разделитель.
     * @param matrixSize Размеры Матрицы.
     * @param thickness Толщина разделителя.
     * @throws NullPointerException размеры null
     * @throws IllegalArgumentException размеры не натуральные, толщина отрицательная
     */
	public EmptySeparator(Dimension matrixSize, int thickness) {
        if (matrixSize.width < 1 || matrixSize.height < 1)
            throw new IllegalArgumentException();
        
        this.matrixSize = matrixSize;
        setThickness(thickness);
        paintingArea = null;
        actualPaintingArea = new Rectangle();
        needActualPaint = false;
        cellX = new int[matrixSize.width];
        cellY = new int[matrixSize.height];
        cellSize = new Dimension();  
    }
     
    /** {@inheritDoc}
     */
    @Override
	public int getThickness() {
        return thickness;
	}

    /** Устанавливает толщину разделителя.
     * Вызовете {@link MatrixView#repaint()} после изменения толщины.
     * Выполните  перекомпоновку контейнера компонента визуализации.
     * @param thickness
     * @throws IllegalArgumentException толщина отрицательная
     */
	public void setThickness(int thickness) {
        if (thickness < 0)
            throw new IllegalArgumentException();                  
        this.thickness = thickness;
        calcCellAreas();
	}
        
    /** Метод рисования.
     * Выполняет расчеты и вызывает {@link #actualPaint}.
     */
    @Override
    public void paint(Graphics2D g, Rectangle area) {
        if (!area.equals(this.paintingArea)) {
            this.paintingArea = new Rectangle(area);
            calcCellAreas();
        }
        if (needActualPaint)
            actualPaint(g, actualPaintingArea);
    }
     
    /** {@inheritDoc}
     */
    @Override
    public Rectangle getArea(Point cell) {
        if (paintingArea == null)
            throw new IllegalStateException();
        
        return new Rectangle(
            cellX[cell.x], cellY[cell.y],
            cellSize.width, cellSize.height);
    }
  
    /** Фактически выполняет рисование.
     * Заглушка метода, переопределите если требуется что-то нарисовать.
     * @param g Графический контекст буфера.
     * @param area Фактическая область рисования.
    */
    protected void actualPaint(Graphics2D g, Rectangle area) {}     
        
    /** Выполняет расчеты.
     * Разрешает фактическое рисование если толщина разделителя натуральное число,
     * либо размеры области, отведенной под рисование, не меньше минимально необходимых. 
     */
    private void calcCellAreas() {
        if (paintingArea == null)   // изменена толщина разделителя, paint() еще не вызывался 
            return;
        
        cellSize.width = (paintingArea.width - (matrixSize.width + 1) * thickness) / matrixSize.width;
        cellSize.height = (paintingArea.height - (matrixSize.height + 1) * thickness) / matrixSize.height;  
        if (cellSize.width < 0) cellSize.width = 0;
        if (cellSize.height < 0) cellSize.height = 0;
        
        cellX[0] = paintingArea.x + thickness;
        for (int i = 1; i < cellX.length; i++)
            cellX[i] = cellX[i - 1] + cellSize.width + thickness;     
        cellY[0] = paintingArea.y + thickness;
        for (int j = 1; j < cellY.length; j++)
            cellY[j] = cellY[j - 1] + cellSize.height + thickness;
        
        actualPaintingArea.x = paintingArea.x;
        actualPaintingArea.y = paintingArea.y;
        actualPaintingArea.width = matrixSize.width * (thickness + cellSize.width) + thickness;
        actualPaintingArea.height = matrixSize.height * (thickness + cellSize.height) + thickness;
        
        // разрешить actualPaint()?
        if (actualPaintingArea.width > paintingArea.width || actualPaintingArea.height > paintingArea.height)
            needActualPaint = false;
        else if (thickness == 0)
            needActualPaint = false;
        else
            needActualPaint = true;
    }
}
