package game.model;

import game.model.exception.LevelLoadException;
import game.observer.EventType; 
import game.observer.Subject;   

public class GameModel {

    private Level currentLevel;
    private boolean isGameOver;
    private int currentLevelIndex;
    private int deathCount;
    private final int totalLevels; // final, se carga una vez
    private final Subject subject = new Subject(); // Instancia del Sujeto


    public GameModel() throws LevelLoadException {
        this.currentLevelIndex = 0;
        this.deathCount = 0;
        this.isGameOver = false;
        // Esta operación puede fallar, por eso el constructor lanza la excepción
        this.totalLevels = LevelLoader.getTotalLevels(); 
        loadLevel(currentLevelIndex);
    }

    public void update() {
        if (!isGameOver && currentLevel != null) {
            currentLevel.update();
        }
    }
    
    public void loadLevel(int levelIndex) throws LevelLoadException {
        if (levelIndex >= totalLevels) {
            this.isGameOver = true;
            this.currentLevel = null;
            return;
        }
        this.currentLevel = LevelLoader.loadLevel(levelIndex);
        this.currentLevelIndex = levelIndex;
    }
    
    public void resetPlayerPosition() {
        deathCount++;
        if (currentLevel != null) {
            currentLevel.resetCoinsInLevel();
            Player player = currentLevel.getPlayer();
            player.setPosition(currentLevel.getInitialPlayerX(), currentLevel.getInitialPlayerY());
        }
    }

    // --- MÉTODOS PARA MANEJAR EL ESTADO Y NOTIFICAR ---

    public void playerDied() {
        deathCount++;
        if (currentLevel != null) {
            currentLevel.resetCoinsInLevel();
            Player player = currentLevel.getPlayer();
            player.setPosition(currentLevel.getInitialPlayerX(), currentLevel.getInitialPlayerY());
        }
        // Notificar a los observadores sobre la muerte
        subject.notifyObservers(this, EventType.PLAYER_DEATH);
    }

    public void collectCoin(Coin coin) {
        if (coin != null && !coin.isCollected()) {
            coin.setCollected(true);
            // Notificar que una moneda fue recolectada
            subject.notifyObservers(this, EventType.COIN_COLLECTED);
        }
    }

    public void collectKey() {
        Key key = currentLevel.getKey();
        if (key != null && !key.isCollected()) {
            key.setCollected(true);
            currentLevel.openDoors();
            // Notificar que la llave fue recolectada
            subject.notifyObservers(this, EventType.KEY_COLLECTED);
        }
    }

    public void completeLevel() throws LevelLoadException {
        // Notificar que el nivel se completó ANTES de cargar el siguiente
        subject.notifyObservers(this, EventType.LEVEL_COMPLETE);
        
        incrementLevelIndex();
        if (currentLevelIndex >= totalLevels) {
            this.isGameOver = true;
            this.currentLevel = null;
            // Notificar que el juego completo ha terminado
            subject.notifyObservers(this, EventType.GAME_COMPLETE);
        } else {
            loadLevel(currentLevelIndex);
        }
    }


    // --- Getters y Setters para que el Controller los use ---
    public Level getCurrentLevel() { return currentLevel; }
    public boolean isGameOver() { return isGameOver; }
    public void setGameOver(boolean gameOver) { isGameOver = gameOver; }
    public int getCurrentLevelIndex() { return currentLevelIndex; }
    public void incrementLevelIndex() { this.currentLevelIndex++; }
    public int getDeathCount() { return deathCount; }
    public int getTotalLevels() { return totalLevels; }
    public Player getPlayer() { return (currentLevel != null) ? currentLevel.getPlayer() : null; }
    public Subject getSubject() { return subject; }
}