package framework.matrix.view;

import java.awt.*;
import java.util.*;

/** Основа рисовальщика разноцветных блоков.
 * Каждый блок рисуется своим цветом. Цвета должны быть заданы для всех возможных
 * значений, даже для null. В случае перечисления выполняется проверка полноты карты
 * цветов, в остальных случаях можно лишь утверждать что карта содержит не менее 2 записей. 
 */
public abstract class MulticolorBricks implements BrickPainter
{
	private Map<Object, Color> colors;
        
    /** Конструктор рисовальщика.
     * @param colors Цветовые настройки блоков.
     * @throws NullPointerException коллекция или элемент коллекции null
     * @throws IllegalArgumentException размер коллекции, нет null ключа
     */
	public MulticolorBricks(Map<Object, Color> colors) {
        setColors(colors);
	}

    /** Возвращает цветовые настройки блоков.
     * @return
     */
	public Map<Object, Color> getColors() {
        return new HashMap<>(colors);
	}

    /** Устанавливает цветовые настройки блоков.
     * Вызовете {@link MatrixView#repaint()} после изменения настроек.
     * @param colors
     * @throws NullPointerException коллекция или элемент коллекции null
     * @throws IllegalArgumentException размер коллекции, нет null ключа
     */
	public void setColors(Map<Object, Color> colors) {
        if (colors.containsValue(null))
            throw new NullPointerException("цвет");
        if (!colors.containsKey(null))
            throw new IllegalArgumentException("нет null ключа");
        if (colors.size() < 2)
            throw new IllegalArgumentException("недопустимый размер");
        
        java.util.Iterator<Object> it = colors.keySet().iterator();
        it.next();		// null ключ на 0й позиции
        Class<?> cl = it.next().getClass();
        if (cl.isEnum() && colors.size() != (cl.getEnumConstants().length + 1)) {
        	throw new IllegalArgumentException("недопустимый размер");
        }
         
        this.colors = new HashMap<>(colors);
	}
        
    /** Предоставляет цвет, которым должен рисоваться блок.
     * @param brick Значение блока, м.б. null.
     * @return Цвет блока, null если нет записи.
     */
    protected Color colorOf(Object brick) {
        return colors.get(brick);
    }
}
