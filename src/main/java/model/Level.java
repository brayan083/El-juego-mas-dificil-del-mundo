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

    // Constructor
    public Level(int windowWidth, int windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.obstacles = new ArrayList<>();
        this.coins = new ArrayList<>();
    }

    // Actualizar elementos móviles
    public void update() {
        if (obstacles != null) {
            for (Obstacle obstacle : obstacles) {
                obstacle.update(tileMap, tileSize);
            }
        }
        if (player != null) {
            player.update(windowWidth, windowHeight - Config.HEADER_HEIGHT, tileMap, tileSize);
        }
    }

    // Getters 
    public Player getPlayer() { return player; }
    public ArrayList<Obstacle> getObstacles() { return obstacles; }
    public Goal getGoal() { return goal; }
    public ArrayList<Coin> getCoins() { return coins; }
    public int[][] getTileMap() { return tileMap; }
    public int getTileSize() { return tileSize; }

    // Setters 
    public void setPlayer(Player player) { this.player = player; }
    public void setGoal(Goal goal) { this.goal = goal; }
    public void setCoins(List<Coin> coins) { this.coins = new ArrayList<>(coins); }
    public void setObstacles(List<Obstacle> obstacles) { this.obstacles = new ArrayList<>(obstacles); }
    public void setTileMap(int[][] tileMap, int tileSize) { this.tileMap = tileMap; this.tileSize = tileSize; }
}
