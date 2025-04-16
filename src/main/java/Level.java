import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics2D;

public class Level {
    private Player player; // Jugador
    private ArrayList<Obstacle> obstacles; // Lista de obstáculos
    private Goal goal; // Meta
    private ArrayList<Wall> walls; // Lista de paredes
    private int windowWidth, windowHeight; // Límites de la ventana

    // Constructor
    public Level(int windowWidth, int windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    // Actualizar elementos móviles
    public void update() {
        for (Obstacle obstacle : obstacles) {
            obstacle.update();
        }
    }

    // Dibujar todo
    public void draw(Graphics2D g) {
        for (Wall wall : walls) {
            wall.draw(g);
        }
        for (Obstacle obstacle : obstacles) {
            obstacle.draw(g);
        }
        goal.draw(g);
        player.draw(g);
    }

    // Getters
    public Player getPlayer() {
        return player;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public Goal getGoal() {
        return goal;
    }

    public ArrayList<Wall> getWalls() {
        return walls;
    }

    // Setters
    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public void setObstacles(List<Obstacle> obstacles) {
        this.obstacles = new ArrayList<>(obstacles);
    }

    public void setWalls(List<Wall> walls) {
        this.walls = new ArrayList<>(walls);
    }
}
