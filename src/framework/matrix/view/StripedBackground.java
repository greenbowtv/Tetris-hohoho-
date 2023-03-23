package framework.matrix.view;

import java.awt.*;

/** Рисовальщик полосатого фона.
 * Рисуются вертикальные полосы: четные и нечетные разными цветами.
 * Ширина всех полос одинакова если ширина области кратна N (количеству полос), 
 * иначе самая правая занимает дополнительное пространство [1, N) пикселей.
 */
public class StripedBackground implements BackgroundPainter
{
	private final int stripNumber;  	// число полос
    private     Color evenColor;    	// цвет четных полос
    private     Color oddColor;     	// цвет нечетных полос
    private       int stripWidth;   	// ширина N-1 полос, последняя может чуть шире

    /** Конструктор с цветами по умолчанию.
     * @param stripNumber Число полос.
     * @throws IllegalArgumentException ненатуральное N
     */
    public StripedBackground(int stripNumber) {
        this(stripNumber, new Color(0xF4F0EA), new Color(0xFDFCF5)); 
    }
    
    /** Конструктор с пользовательскими цветами.
     * @param stipNumber Число полос.
     * @param evenColor Цвет четных полос.
     * @param oddColor Цвет нечетных полос.
     * @throws IllegalArgumentException ненатуральное N, цвета null
     */
    public StripedBackground(int stipNumber, Color evenColor, Color oddColor) {
        if (stipNumber < 1)
            throw new IllegalArgumentException();
        
        this.stripNumber = stipNumber;
        setEvenColor(evenColor);
        setOddColor(oddColor);
        stripWidth = -1;
    }

    /** Возвращает число полос.
     * @return 
     */
    public int getStripNumber() {
        return stripNumber;
    }

    /** Возвращает цвет четных полос.
     * @return 
     */
    public Color getEvenColor() {
        return evenColor;
    }

    /** Устанавливает цвет четных полос.
     * Вызовете {@link MatrixView#repaint()} после изменения цвета.
     * @param c
     * @throws IllegalArgumentException цвет null
     */
    public void setEvenColor(Color c) {
        if (c == null)
            throw new IllegalArgumentException();  
        this.evenColor = c;
    }

    /** Возвращает цвет нечетных полос.
     * @return
     */
    public Color getOddColor() {
        return oddColor;
    }

    /** Устанавливает цвет нечетных полос.
     * Вызовете {@link MatrixView#repaint()} после изменения цвета.
     * @param c
     * @throws IllegalArgumentException цвет null
     */
    public void setOddColor(Color c) {
        if (c == null)
            throw new IllegalArgumentException();  
        this.oddColor = c;
    }

    /** {@inheritDoc}
     */
    @Override
    public void paint(Graphics2D g, Dimension size) {
        stripWidth = size.width / stripNumber;
        int lastStripWidth = stripWidth + size.width % stripNumber;
        for (int i = 0; i < stripNumber; i++) {
            g.setColor(i % 2 == 0 ? evenColor : oddColor);
            g.fillRect(i * stripWidth, 0, i == (stripNumber-1) ? lastStripWidth : stripWidth, size.height);
        }    
    }

    /** {@inheritDoc}
     * @throws IllegalStateException {@link #paint} не вызывался ни разу
     */
    @Override
    public void paintPart(Graphics2D g, Rectangle area) {
        if (stripWidth == -1)
            throw new IllegalStateException();
        
        int nextX = stripWidth; 	// левая абсцисса следующей полосы
        for (int i = 0; i < stripNumber; i++) {
            if (area.x < nextX) {
                g.setColor(i % 2 == 0 ? evenColor : oddColor);
                g.fillRect(area.x, area.y, area.width, area.height);
                return;
            }
            nextX += stripWidth;
        }
    } 
}
