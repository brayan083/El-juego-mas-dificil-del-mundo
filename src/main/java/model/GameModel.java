// src/main/java/model/GameModel.java
package model;

import model.exception.LevelLoadException;

public class GameModel {

    private Level currentLevel;
    private boolean isGameOver;
    private int currentLevelIndex;
    private int deathCount;
    private final int totalLevels; // final, se carga una vez

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

    // --- Getters y Setters para que el Controller los use ---
    public Level getCurrentLevel() { return currentLevel; }
    public boolean isGameOver() { return isGameOver; }
    public void setGameOver(boolean gameOver) { isGameOver = gameOver; }
    public int getCurrentLevelIndex() { return currentLevelIndex; }
    public void incrementLevelIndex() { this.currentLevelIndex++; }
    public int getDeathCount() { return deathCount; }
    public int getTotalLevels() { return totalLevels; }
    public Player getPlayer() {
        return (currentLevel != null) ? currentLevel.getPlayer() : null;
    }
}