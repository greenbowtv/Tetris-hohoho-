package framework.matrix.model.array;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** Основа класса, изменения объектов которого отслеживаются.
 * Решает задачи регистрации и уведомления слушателей.
 * 
 * @author Игорь
 * @see ChangeListener
 */
public abstract class ChangeListened 
{  
    private ArrayList<ChangeListener> listeners = new ArrayList<>();
       
    /** Регистрирует нового слушателя.
     * @param l Слушатель изменений.
     * @throws IllegalArgumentException Если аргумент null.
     * @throws IllegalStateException Если слушатель уже был зарегистрирован.
     */
    public void addChangeListener(ChangeListener l) {
        if (l == null)
            throw new IllegalArgumentException("слушатель null");
        if (listeners.contains(l))
            throw new IllegalStateException("слушатель уже добавлен");
        
        listeners.add(l);
    }

    /** Отменяет регистрацию слушателя.
     * @param l Слушатель изменений.
     * @throws IllegalArgumentException Если аргумент null.
     * @throws IllegalStateException Если такого слушателя не регистрировалось ранее.
     */
    public void removeChangeListener(ChangeListener l) {
        if (l == null)
            throw new IllegalArgumentException("слушатель null");
        if (!listeners.contains(l))
            throw new IllegalStateException("такого слушателя нет");
        
        listeners.remove(l);
    }
    
    /** Уведомляет слушателей.
     * Создает и отправляет объект ChangeEvent, источник this.
     */
    protected void fireStateChanged() {
        fireStateChanged(this);
    }
    
    /** Уведомляет слушателей.
     * Создает и отправляет объект ChangeEvent.
     * @param source Источник события.
     * @throws IllegalArgumentException Если аргумент null.
     */
    protected void fireStateChanged(Object source) {
        ChangeEvent e = new ChangeEvent(source);
        listeners.forEach((l) -> l.stateChanged(e));
    }
    
    /** Уведомляет слушателей.
     * @param e Отправляемое событие.
     * @throws IllegalArgumentException Если аргумент null.
     */
    protected void fireStateChanged(ChangeEvent e) {
        if (e == null)
            throw new IllegalArgumentException("событие null");
    	
        listeners.forEach((l) -> l.stateChanged(e)); 
    }
}
