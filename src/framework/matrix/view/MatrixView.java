package framework.matrix.view;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import framework.matrix.model.array.ArrayAdapter;
import framework.matrix.model.array.ElementsChangeEvent;

/** Компонент визуализации Матрицы.
 * Изображение компонента формируется в 2 слоя:
 *  фон (заливка {@link #getBackground()} цветом не выполняется)
 *  рисунок Матрицы = блоки + разделитель
 * Для этого используются 3 инструмента:
 *  рисовальщик фона {@link BackgroundPainter}
 *  разделитель ячеек {@link CellsSeparator}
 *  рисовальщик блоков {@link BlockPainter}
 * А также надо задать отступы рисунка от краев компонента и блоков от границ ячеек.
 * Поскольку размеры блоков это зависимая от множества параметров характеристика,
 * главным образом от размеров компонента, задайте рекомендованные размеры блоков
 * и перекомпонуйте контейнер содержащий компонент визуализации.
 * 
 *
 * @author Игорь
 */
public class MatrixView extends JComponent
{
	private static final long serialVersionUID = -2820922808098625861L;

	// слушатель изменений модели
	private class ModelHandler implements ChangeListener
    {
		ArrayAdapter<?> array;		// визуализируемая модель
        ElementsChangeEvent event;	// последнее событие

        ModelHandler(ArrayAdapter<?> array) {
            this.array = array;
            this.event = null;
            array.addChangeListener(this);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            event = (ElementsChangeEvent)e;
            updateBuffer = true;
            // нельзя repaint(), т.к. имеется риск пропуска рисования при замене блоков кучи (Тетрис)
            MatrixView.this.paint(MatrixView.this.getGraphics());
        }
	}
        
    // слушатель резайза компонента
    private class ResizeHandler extends ComponentAdapter 
    {
        @Override
        public void componentResized(ComponentEvent e) {
            Dimension size = MatrixView.this.getSize();
            if (size.height > 0) {
            	buffer = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
                updateBuffer = true;
                MatrixView.this.repaint();
            } 
        } 
    }
    
    private BufferedImage     buffer;				// буфер изображения компонента
    private boolean           updateBuffer;			// признак "обновить содержимое буфера"
	private ModelHandler   	  modelHandler;			// приемник событий от модели Матрицы
    private BackgroundPainter backgroundPainter;	// инструмент рисования фона компонента
	private CellsSeparator    cellsSeparator;		// инструмент разделения ячеек Матрицы
	private BrickPainter	  brickPainter;			// инструмент рисования блоков Матрицы
    private Insets            draftInsets;          // отступы рисунка от краев компонента
    private Insets            brickInsets;          // отступы блока от границ ячейки
    private Dimension         preferredBrickSize;   // рекомендованные размеры блока

    /** Конструирует визуализатор Матрицы.
     * @param model
     */
	public MatrixView(ArrayAdapter<?> model) {
        buffer = null;
        updateBuffer = false;
        modelHandler = new ModelHandler(model);
        backgroundPainter = new ColorBackground();
        cellsSeparator = new LineSeparator(modelHandler.array.size());
        brickPainter = new PlainRectangle();
        draftInsets = new Insets(0, 0, 0, 0);
        brickInsets = new Insets(0, 0, 0, 0);
        preferredBrickSize = new Dimension(40, 40);

        addComponentListener(new ResizeHandler());
	}
       
	/** Возвращает инструмент рисования фона.
	 * @return
	 */
    public BackgroundPainter getBackgroundPainter() {
        return backgroundPainter;
	}
      
    /** Устанавливает инструмент рисования фона.
     * @param tool
     * @throws NullPointerException инструмент null
     */
    public void setBackgroundPainter(BackgroundPainter tool) {
        if (tool == null)
            throw new NullPointerException();
        this.backgroundPainter = tool;
        repaint();
	}

    /** Возвращает инструмент разделения ячеек.
     * @return
     */
	public CellsSeparator getCellsSeparator() {
        return cellsSeparator;
	}

	/** Устанавливает инструмент разделения ячеек.
	 * @param tool
	 * @throws NullPointerException инструмент null
	 */
	public void setCellsSeparator(CellsSeparator tool) {
        if (tool == null)
            throw new NullPointerException();
        this.cellsSeparator = tool;
        repaint();
	}

	/** Возвращает инструмент рисования блока.
	 * @return
	 */
	public BrickPainter getBrickPainter() {
        return brickPainter;
	}
	
	/** Устанавливает инструмент рисования блока.
	 * @param tool
	 * @throws NullPointerException инструмент null
	 */
	public void setBrickPainter(BrickPainter tool) {
        if (tool == null)
            throw new NullPointerException();
        this.brickPainter = tool;
        repaint();
	}
         
	/** Возвращает отступы рисунка от краев компонента.
	 * Выполните перекомпоновку контейнера компонента.
	 * @return
	 */
    public Insets getDraftInsets() {
        return draftInsets;
    }

    /** Устанавливает отступы рисунка от краев компонента.
     * @param insets
     * @throws NullPointerException отступы null
     * @throws IllegalArgumentException отступы отрицательные
     */
    public void setDraftInsets(Insets insets) {
        if (insets.left < 0 || insets.top < 0 || insets.right < 0 || insets.bottom < 0)
            throw new IllegalArgumentException(); 
        this.draftInsets = insets;
        repaint();
    }
         
    /** Возвращает отступы блока от границ ячейки.
     * Выполните перекомпоновку контейнера компонента.
     * @return
     */
    public Insets getBrickInsets() {
        return brickInsets;
    }

    /** Устанавливает отступы блока от границ ячейки.
     * @param insets
     * @throws NullPointerException отступы null
     * @throws IllegalArgumentException отступы отрицательные
     */
    public void setBrickInsets(Insets insets) {
        if (insets.left < 0 || insets.top < 0 || insets.right < 0 || insets.bottom < 0)
            throw new IllegalArgumentException(); 
        this.brickInsets = insets;
        repaint();
    }
     
    /** Возвращает рекомендованные размеры блока.
     * @return
     */
    public Dimension getPreferredBrickSize() {
        return preferredBrickSize;
    }
        
    /** Устанавливает рекомендованные размеры блока.
     * Выполните перекомпоновку контейнера компонента.
     * @param size
     * @throws NullPointerException размеры null
     * @throws IllegalArgumentException размеры отрицательные
     */
    public void setPreferredBrickSize(Dimension size) {
        if (size.width < 0 || size.height < 0)
            throw new IllegalArgumentException();   
        this.preferredBrickSize = size;
    }     
     
    /** Возвращает рекомендованные размеры компонента.
     * Рассчитываются отталкиваясь от рекомендованных размеров блока.
     * @return
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension prefferedCellSize = new Dimension(
            preferredBrickSize.width + (brickInsets.left + brickInsets.right),
            preferredBrickSize.height + (brickInsets.top + brickInsets.bottom));    
        return new Dimension(
            modelHandler.array.size().width * (prefferedCellSize.width + cellsSeparator.getThickness()) 
              + cellsSeparator.getThickness() + draftInsets.left + draftInsets.right,
            modelHandler.array.size().height * (prefferedCellSize.height + cellsSeparator.getThickness()) 
              + cellsSeparator.getThickness() + draftInsets.top + draftInsets.bottom);
    }
             
    /** Формирует изображение компонента.
     * 
     */
    @Override
	public void paint(Graphics g) 
    {
    	if (updateBuffer) 
        {
            Graphics2D g2d = buffer.createGraphics();
            
            // обновить буфер целиком?
            if (modelHandler.event == null)		
            {
            	Dimension size = getSize();
                backgroundPainter.paint(g2d, size);         
                cellsSeparator.paint(g2d, new Rectangle(
                    draftInsets.left,
                    draftInsets.top,
                    size.width - (draftInsets.left + draftInsets.right),
                    size.height - (draftInsets.top + draftInsets.bottom)
                ));
                
                Point cell = new Point();
                for (cell.y = 0; cell.y < modelHandler.array.size().height; cell.y++) {
                    for (cell.x = 0; cell.x < modelHandler.array.size().width; cell.x++) {
                        Object brick = modelHandler.array.get(cell);
                        Rectangle area = cellsSeparator.getArea(cell);
                        area.x += brickInsets.left;
                        area.y += brickInsets.top;
                        area.width -= brickInsets.left + brickInsets.right;
                        area.height -= brickInsets.top + brickInsets.bottom;
                        
                        brickPainter.paint(g2d, area, brick);
                    }
                }
            }
            else
            {
            	 for (Point cell : modelHandler.event) {
                     Object brick = modelHandler.array.get(cell);
                     Rectangle cellArea = cellsSeparator.getArea(cell);
                     Rectangle brickArea = new Rectangle(
                         cellArea.x + brickInsets.left,
                         cellArea.y + brickInsets.top,
                         cellArea.width - (brickInsets.left + brickInsets.right),
                         cellArea.height - (brickInsets.top + brickInsets.bottom)       
                     );
                     
                     backgroundPainter.paintPart(g2d, cellArea);
                     brickPainter.paint(g2d, brickArea, brick);
                 }
            	 
                 modelHandler.event = null;
            }
            
            updateBuffer = false;
        }

        g.drawImage(buffer, 0, 0, this);
        
        paintBorder(g);
	}   
}
