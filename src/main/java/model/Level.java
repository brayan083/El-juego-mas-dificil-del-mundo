package model;
import java.util.ArrayList;
import java.util.List;

public class Level {
    private Player player; // Jugador
    private ArrayList<Obstacle> obstacles; // Lista de obstáculos
    private Goal goal; // Meta
    private ArrayList<Coin> coins;
    private int[][] tileMap; // Cuadrícula del mapa
    private int tileSize; // Tamaño de cada celda
    private int windowWidth, windowHeight; // Límites de la ventana
    private Key key;
    private boolean doorsAreOpen;

    // Constructor
    public Level(int windowWidth, int windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.obstacles = new ArrayList<>();
        this.coins = new ArrayList<>();
        this.doorsAreOpen = false; 
    }

    // Actualizar elementos móviles
    public void update() {
        if (obstacles != null) {
            for (Obstacle obstacle : obstacles) {
                obstacle.update(tileMap, tileSize);
            }
        }
        if (player != null) {
            player.update(this);
        }
    }

    // Verificar si el jugador ha recogido todas las monedas
    public boolean areAllCoinsCollectedInLevel() {
        if (this.coins == null || this.coins.isEmpty()) {
            return true; // No hay monedas para recolectar, condición cumplida.
        }
        for (Coin coin : this.coins) {
            if (!coin.isCollected()) {
                return false; // Se encontró al menos una moneda no recolectada.
            }
        }
        return true; // Todas las monedas han sido recolectadas.
    }

    /**
     * Obtiene el número de monedas actualmente recolectadas en este nivel.
     * @return El número de monedas recolectadas.
     */
    public int getNumberOfCurrentlyCollectedCoinsInLevel() {
        if (this.coins == null || this.coins.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (Coin coin : this.coins) {
            if (coin.isCollected()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Obtiene el número total de monedas disponibles en este nivel.
     * @return El total de monedas en el nivel.
     */
    public int getTotalCoinsInLevel() {
        if (this.coins == null) {
            return 0;
        }
        return this.coins.size();
    }

    /**
     * Reinicia el estado de todas las monedas en el nivel a 'no recolectadas'.
     * Esto se usará cuando el jugador muera.
     */
    public void resetCoinsInLevel() {
        if (this.coins != null) {
            for (Coin coin : this.coins) {
                coin.setCollected(false);
            }
        }
        if (this.key != null) {
            this.key.setCollected(false); // <-- REINICIAR LA LLAVE
        }
        this.doorsAreOpen = false;
    }

    public void openDoors() {
        this.doorsAreOpen = true;
    }

    public boolean areDoorsOpen() {
        return doorsAreOpen;
    }

    // Getters 
    public Player getPlayer() { return player; }
    public ArrayList<Obstacle> getObstacles() { return obstacles; }
    public Goal getGoal() { return goal; }
    public ArrayList<Coin> getCoins() { return coins; }
    public int[][] getTileMap() { return tileMap; }
    public int getTileSize() { return tileSize; }
    public int getWindowWidth() { return windowWidth; }
    public int getWindowHeight() { return windowHeight; }

    // Setters 
    public void setPlayer(Player player) { this.player = player; }
    public void setGoal(Goal goal) { this.goal = goal; }
    public void setCoins(List<Coin> coins) { this.coins = new ArrayList<>(coins); }
    public void setObstacles(List<Obstacle> obstacles) { this.obstacles = new ArrayList<>(obstacles); }
    public void setTileMap(int[][] tileMap, int tileSize) { this.tileMap = tileMap; this.tileSize = tileSize; }

    // Getters y Setters para la llave
    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

}
