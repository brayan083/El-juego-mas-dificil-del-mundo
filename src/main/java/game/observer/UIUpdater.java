package game.observer;

import game.model.GameModel;

public class UIUpdater implements Observer {
    @Override
    public void onNotify(GameModel model, EventType event) {
        switch (event) {
            case PLAYER_DEATH:
                System.out.println("[OBSERVER] UI: Actualizando contador de muertes a " + model.getDeathCount());
                break;
            case COIN_COLLECTED:
                System.out.println("[OBSERVER] UI: Actualizando contador de monedas.");
                break;
            case LEVEL_COMPLETE:
                System.out.println("[OBSERVER] UI: Mostrando mensaje de 'Nivel Completado!'");
                break;
            case GAME_COMPLETE:
                 System.out.println("[OBSERVER] UI: Mostrando pantalla de 'Juego Terminado!'");
                 break;
            default:
                break;
        }
    }
}