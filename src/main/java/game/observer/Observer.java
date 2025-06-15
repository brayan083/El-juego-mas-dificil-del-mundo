package game.observer;

import game.model.GameModel;

// La interfaz que cada "suscriptor" debe implementar.
public interface Observer {
    // El método que el Sujeto llamará para notificar un cambio.
    // Pasamos el modelo para que el observador pueda consultar el estado actualizado.
    void onNotify(GameModel model, EventType event);
}