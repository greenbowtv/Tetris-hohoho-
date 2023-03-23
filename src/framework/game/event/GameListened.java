package framework.game.event;

import java.util.ArrayList;

/** Основа Игры как класса, объекты которого слушаются.
 * Выполняет задачи регистрации и уведомления слушателей.
 *
 * @author Игорь
 */
public abstract class GameListened 
{
    private ArrayList<GameListener> listeners = new ArrayList<>();
 
    /** Регистрирует нового слушателя.
     * @param l Игровой слушатель.
     * @throws IllegalArgumentException слушатель null
     * @throws IllegalStateException слушатель уже добавлен
     */
    public void addGameListener(GameListener l) {
        if (l == null)
            throw new IllegalArgumentException("слушатель null");
        if (listeners.contains(l))
            throw new IllegalStateException("слушатель уже добавлен");
        
        listeners.add(l);
    }

    /** Отменяет регистрацию слушателя.
     * @param l Игровой слушатель.
     * @throws IllegalArgumentException слушатель null
     * @throws IllegalStateException такого слушателя нет
     */
    public void removeGameListener(GameListener l) {
        if (l == null)
            throw new IllegalArgumentException("слушатель null");
        if (!listeners.contains(l))
            throw new IllegalStateException("такого слушателя нет");
        
        listeners.remove(l);
    }

    /** Уведомляет слушателей.
     * Создает и отправляет объект {@link GameEvent}.
     * @param win Результат завершения игры.
     */
    protected void fireGameOver(boolean win) {
        GameEvent e = new GameEvent(this, win);
        listeners.forEach((l) -> l.gameOver(e));
    } 
}