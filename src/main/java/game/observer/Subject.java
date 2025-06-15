package game.observer;

import java.util.ArrayList;
import java.util.List;
import game.model.GameModel;

public class Subject {
    // La lista de suscriptores (observadores).
    private final List<Observer> observers = new ArrayList<>();

    // Método para que un observador se suscriba.
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    // Método para que un observador se desuscriba.
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    // Notifica a TODOS los observadores suscritos.
    public void notifyObservers(GameModel model, EventType event) {
        for (Observer observer : observers) {
            observer.onNotify(model, event);
        }
    }
}