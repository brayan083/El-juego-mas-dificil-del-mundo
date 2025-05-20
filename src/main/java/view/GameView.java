package view; // o com.tuproyecto.view si estás usando una estructura de paquetes más profunda

import model.Config;
import model.Level;
import model.Player;
import model.Obstacle;
import model.Goal;
import controller.Game; // Para acceder al estado del juego como deathCount, currentLevel, etc.

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;

public class GameView {

    public void renderGame(Graphics2D g2d, Game gameController, Level currentLevel, int totalLevels) {
        // Dibujar el header
        drawHeader(g2d, gameController, totalLevels);

        // Mover el área de juego hacia abajo para dejar espacio al header
        g2d.translate(0, Config.HEADER_HEIGHT);

        // Pintar toda el área externa de color lavanda
        g2d.setColor(Config.COLOR_PLAY_AREA_BACKGROUND);
        g2d.fillRect(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT - Config.HEADER_HEIGHT);

        if (gameController.isGameOver() && currentLevel == null) { // Caso de juego completado
            // Restaurar la transformación antes de dibujar pantalla de game over que ocupa
            g2d.translate(0, - Config.HEADER_HEIGHT);
            drawGameOverScreen(g2d, "¡Juego Completado!");
        } else if (currentLevel != null) {
            // Calcular el área jugable (rectángulo que encierra las paredes)
            Shape playArea = calculatePlayArea(currentLevel);
            if (playArea != null) {
                Shape originalClip = g2d.getClip();
                g2d.setClip(playArea);

                // Dibujar el tablero de ajedrez dentro del área jugable
                drawChessboardBackground(g2d, currentLevel);
                g2d.setClip(originalClip);
            }

            // Dibujar los componentes del nivel
            drawLevelComponents(g2d, currentLevel);
        }
        // Si es gameOver por otra razón (ej. error de carga y currentLevel es el último
        // válido)
        // se podría mostrar otro mensaje o simplemente no dibujar nada más.

        // Restaurar la transformación al final si es necesario (depende de cómo se
        // estructure)
        // Por ahora, Game.java se encargará de la transformación final en su
        // paintComponent
        // g2d.translate(0, -HEADER_HEIGHT); // Esta línea se maneja mejor en Game.java
        // o al final de paintComponent
    }

    private void drawHeader(Graphics2D g2d, Game gameController, int totalLevels) {
        g2d.setColor(Config.COLOR_HEADER_BACKGROUND);
        g2d.fillRect(0, 0, Config.WINDOW_WIDTH, Config.HEADER_HEIGHT);

        g2d.setColor(Config.COLOR_HEADER_TEXT);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2d.drawString("Nivel: " + (gameController.getCurrentLevelIndex() + 1) + "/" + totalLevels, 20,
                Config.HEADER_HEIGHT / 2 + 5); // Ajustar Y para centrar
        g2d.drawString("Muertes: " + gameController.getDeathCount(), Config.WINDOW_WIDTH - 150, Config.HEADER_HEIGHT / 2 + 5); // Ajustar
                                                                                                                        // Y
    }

    private Shape calculatePlayArea(Level level) {
        if (level == null || level.getTileMap() == null) {
            // System.out.println("No hay tileMap, usando área por defecto para playArea.");
            return new Rectangle(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT - Config.HEADER_HEIGHT);
        }

        int[][] tileMap = level.getTileMap();
        int tileSize = level.getTileSize();
        Area playAreaShape = new Area();

        for (int i = 0; i < tileMap.length; i++) {
            for (int j = 0; j < tileMap[i].length; j++) {
                if (tileMap[i][j] == 0) { // Suelo (parte jugable)
                    playAreaShape.add(new Area(new Rectangle(j * tileSize, i * tileSize, tileSize, tileSize)));
                }
            }
        }
        return playAreaShape;
    }

    private void drawChessboardBackground(Graphics2D g2d, Level level) {
        // El parámetro Level puede no ser necesario si tileSize es constante o viene de
        // Config
        int chessboardTileSize = 30; // Tamaño de cada cuadrado del fondo. Puede ser diferente al tileSize del nivel.
                                     // Considera hacerlo una constante en GameView o Config.
        boolean isWhite = true; // Determina el color del primer tile (0,0) del chessboard

        for (int y = 0; y < Config.WINDOW_HEIGHT - Config.HEADER_HEIGHT; y += chessboardTileSize) {
            // Para cada nueva fila, el color de inicio debe ser el opuesto al de la celda
            // (x=0) de la fila anterior.
            // Pero, más simple: mantenemos el color de la última celda de la fila anterior
            // y lo invertimos *si el número de celdas por fila es impar*.
            // O, la forma más clásica:
            boolean rowStartIsWhite = isWhite; // Guardamos el color con el que debe empezar esta fila

            for (int x = 0; x < Config.WINDOW_WIDTH; x += chessboardTileSize) {
                if (rowStartIsWhite) {
                    g2d.setColor(Config.COLOR_BACKGROUND_CHESS_LIGHT);
                } else {
                    g2d.setColor(Config.COLOR_BACKGROUND_CHESS_DARK);
                }
                g2d.fillRect(x, y, chessboardTileSize, chessboardTileSize);
                rowStartIsWhite = !rowStartIsWhite; // Alternar para la siguiente celda en la misma fila
            }
            // Para la siguiente fila, invertir el color de inicio que usamos para esta
            // fila.
            isWhite = !isWhite;
        }
    }

    private void drawLevelComponents(Graphics2D g2d, Level level) {
        // Dibujar el tileMap (solo las paredes)
        if (level.getTileMap() != null) {
            int[][] tileMap = level.getTileMap();
            int tileSize = level.getTileSize();
            for (int i = 0; i < tileMap.length; i++) {
                for (int j = 0; j < tileMap[i].length; j++) {
                    if (tileMap[i][j] == 1) { // Pared
                        g2d.setColor(Config.COLOR_WALL_TILE);
                        g2d.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                    }
                }
            }
        }

        // Dibujar obstáculos
        ArrayList<Obstacle> obstacles = level.getObstacles();
        if (obstacles != null) {
            for (Obstacle obstacle : obstacles) {
                drawObstacle(g2d, obstacle);
            }
        }

        // Dibujar meta
        Goal goal = level.getGoal();
        if (goal != null) {
            drawGoal(g2d, goal);
        }

        // Dibujar jugador
        Player player = level.getPlayer();
        if (player != null) {
            drawPlayer(g2d, player);
        }
    }

    private void drawPlayer(Graphics2D g2d, Player player) {
        g2d.setColor(Config.COLOR_PLAYER);
        Rectangle bounds = player.getBounds(); // Asumiendo que Player tiene getBounds()
        g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    private void drawObstacle(Graphics2D g2d, Obstacle obstacle) {
        g2d.setColor(Config.COLOR_OBSTACLE);
        // Obstacle.getBounds() devuelve Ellipse2D.Float.
        // Usaremos los datos del obstáculo para dibujar, ya que Ellipse2D no tiene
        // x,y,radius directos
        // O modificamos Obstacle para que exponga x,y,radius
        // Por ahora, asumimos que Obstacle tiene getX, getY, getRadius
        g2d.fillOval((int) (obstacle.getX() - obstacle.getRadius()),
                (int) (obstacle.getY() - obstacle.getRadius()),
                obstacle.getRadius() * 2,
                obstacle.getRadius() * 2);
    }

    private void drawGoal(Graphics2D g2d, Goal goal) {
        g2d.setColor(Config.COLOR_GOAL);
        Rectangle bounds = goal.getBounds(); // Asumiendo que Goal tiene getBounds()
        g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void drawGameOverScreen(Graphics2D g2d, String message) {
        // Fondo negro semi-transparente
        g2d.setColor(Config.COLOR_GAME_OVER_BACKGROUND);
        g2d.fillRect(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT); // Cubre toda la ventana

        // Mensaje
        g2d.setColor(Config.COLOR_GAME_OVER_TEXT);
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics metrics = g2d.getFontMetrics();
        int x = (Config.WINDOW_WIDTH - metrics.stringWidth(message)) / 2;
        int y = (Config.WINDOW_HEIGHT - metrics.getHeight()) / 2 + metrics.getAscent();
        g2d.drawString(message, x, y);
    }
}