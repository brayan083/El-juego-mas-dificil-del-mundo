package controller;

import javax.swing.JPanel;

import com.fasterxml.jackson.databind.JsonNode;

import handler.InputHandler;
import model.Config;
import model.Level;
import model.LevelLoader;
import model.Obstacle;
import model.Player;

import java.awt.*;
import java.awt.geom.Area;

public class Game extends JPanel {
    private Level level;
    private boolean gameOver;
    private int currentLevel = 0;
    private int deathCount = 0; // Contador de muertes

    private InputHandler inputHandler; // Manejador de entrada

    public Game() {
        inputHandler = new InputHandler();
        addKeyListener(inputHandler); // Registrar InputHandler como KeyListener
        // Es crucial que loadLevel se llame DESPUÉS de inicializar inputHandler
        // y que inputHandler.setActivePlayer se llame DESPUÉS de que el jugador del
        // nivel esté disponible.
        loadLevel(currentLevel);
        gameOver = false;
        setFocusable(true); // Asegúrate de que el JPanel pueda recibir foco para los eventos de teclado
    }

    private void loadLevel(int levelIndex) {
        Level newLevel = LevelLoader.loadLevel(levelIndex);
        if (newLevel == null) {
            gameOver = true;
            currentLevel--; // Mantener el último nivel válido
            if (inputHandler != null) {
                inputHandler.setActivePlayer(null); // No hay jugador activo
            }
            // Mantenemos el último nivel en lugar de hacerlo null
            return;
        }
        level = newLevel;
        gameOver = false;
        if (inputHandler != null && level.getPlayer() != null) {
            inputHandler.setActivePlayer(level.getPlayer()); // Establecer el jugador activo
        } else if (inputHandler != null) {
            inputHandler.setActivePlayer(null); // Si no hay jugador por alguna razón
        }
    }

    public void update() {
        if (!gameOver) {
            level.update();
            checkCollisions();
        }
    }

    private void checkCollisions() {
        Player player = level.getPlayer();

        // Verificar colisiones con obstáculos
        for (Obstacle obstacle : level.getObstacles()) {
            if (player.getBounds().intersects(obstacle.getBounds().getBounds())) {
                resetPlayerPosition();
                return;
            }
        }

        // Verificar si llegó a la meta
        if (player.getBounds().intersects(level.getGoal().getBounds())) {
            if (!gameOver) {
                currentLevel++;
                loadLevel(currentLevel);
            }
        }
    }

    private void resetPlayerPosition() {
        deathCount++; // Incrementar contador de muertes
        Player player = level.getPlayer();
        // Obtener la posición inicial del jugador desde el nivel actual
        JsonNode levelData = LevelLoader.getLevelData(currentLevel);
        if (levelData != null) {
            JsonNode playerNode = levelData.get("player");
            int x = (int) playerNode.get("x").floatValue();
            int y = (int) playerNode.get("y").floatValue();
            player.setPosition(x, y);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Dibujar el header
        g2d.setColor(new Color(0, 0, 0));
        g2d.fillRect(0, 0, Config.WINDOW_WIDTH, 40);

        // Texto del header
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
        int totalLevels = LevelLoader.getTotalLevels(); // Obtener el total de niveles
        g2d.drawString("Nivel: " + (currentLevel + 1) + "/" + totalLevels, 20, 22);
        g2d.drawString("Muertes: " + deathCount, Config.WINDOW_WIDTH - 150, 22);

        drawGame(g2d);
    }

    private void drawGame(Graphics2D g2d) {
        // Mover el área de juego hacia abajo
        g2d.translate(0, 40);

        // Pintar toda el área externa de color lavanda
        g2d.setColor(new Color(179, 179, 255)); // Color lavanda para el área externa
        g2d.fillRect(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT - 40);

        // Calcular el área jugable (rectángulo que encierra las paredes)
        Shape playArea = calculatePlayArea();
        if (playArea != null) {
            Shape originalClip = g2d.getClip();
            g2d.setClip(playArea);

            // Dibujar el tablero de ajedrez
            int tileSize = 30; // Tamaño de cada cuadrado
            boolean isWhite = true;
            for (int y = 0; y < Config.WINDOW_HEIGHT - 40; y += tileSize) {
                for (int x = 0; x < Config.WINDOW_WIDTH; x += tileSize) {
                    if (isWhite) {
                        g2d.setColor(new Color(222, 222, 255)); // Lavanda muy claro
                    } else {
                        g2d.setColor(new Color(247, 247, 255)); // Gris claro
                    }
                    g2d.fillRect(x, y, tileSize, tileSize);
                    isWhite = !isWhite;
                }
                isWhite = !isWhite; // Cambiar el patrón en cada fila
            }
            g2d.setClip(originalClip);
        }

        // Solo dibujamos el nivel si existe y no estamos en gameOver
        if (level != null && !gameOver) {
            level.draw(g2d);
        }

        if (gameOver) {
            // Fondo negro semi-transparente
            g2d.setColor(new Color(0, 0, 0, 128));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Mensaje de victoria
            g2d.setColor(Color.GREEN);
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            String message = "¡Juego Completado!";
            FontMetrics metrics = g2d.getFontMetrics();
            int x = (getWidth() - metrics.stringWidth(message)) / 2;
            int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
            g2d.drawString(message, x, y);
        }

        // Restaurar la transformación
        g2d.translate(0, -30);
    }

    private Shape calculatePlayArea() {
        if (level == null || level.getTileMap() == null) {
            System.out.println("No hay tileMap, usando área por defecto: (0, 0, " + Config.WINDOW_WIDTH + ", "
                    + (Config.WINDOW_HEIGHT - 30) + ")");
            return new Rectangle(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT - 30);
        }

        int[][] tileMap = level.getTileMap();
        int tileSize = level.getTileSize();
        Area playArea = new Area();

        for (int i = 0; i < tileMap.length; i++) {
            for (int j = 0; j < tileMap[i].length; j++) {
                if (tileMap[i][j] == 0) { // Piso
                    playArea.add(new Area(new Rectangle(j * tileSize, i * tileSize, tileSize, tileSize)));
                }
            }
        }
        return playArea;
    }
}