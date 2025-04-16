import javax.swing.JPanel;

import com.fasterxml.jackson.databind.JsonNode;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Game extends JPanel {
    private Level level;
    private boolean gameOver;
    private int currentLevel = 0;
    private int deathCount = 0; // Contador de muertes

    public Game() {
        // level = new Level(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        loadLevel(currentLevel);
        gameOver = false;
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyReleased(e);
            }
        });
    }

    private void loadLevel(int levelIndex) {
        Level newLevel = LevelLoader.loadLevel(levelIndex);
        if (newLevel == null) {
            // Si no hay más niveles, mostrar Game Over
            gameOver = true;
            currentLevel--; // Mantener el último nivel válido
            // Mantenemos el último nivel en lugar de hacerlo null
            return;
        }
        level = newLevel;
        gameOver = false;
    }

    private void handleKeyPressed(KeyEvent e) {
        if (!gameOver) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    level.getPlayer().setMovingUp(true);
                    break;
                case KeyEvent.VK_DOWN:
                    level.getPlayer().setMovingDown(true);
                    break;
                case KeyEvent.VK_LEFT:
                    level.getPlayer().setMovingLeft(true);
                    break;
                case KeyEvent.VK_RIGHT:
                    level.getPlayer().setMovingRight(true);
                    break;
            }
        }
    }

    private void handleKeyReleased(KeyEvent e) {
        if (!gameOver) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    level.getPlayer().setMovingUp(false);
                    break;
                case KeyEvent.VK_DOWN:
                    level.getPlayer().setMovingDown(false);
                    break;
                case KeyEvent.VK_LEFT:
                    level.getPlayer().setMovingLeft(false);
                    break;
                case KeyEvent.VK_RIGHT:
                    level.getPlayer().setMovingRight(false);
                    break;
            }
        }
    }

    public void update() {
        if (!gameOver) {
            level.update(); // Actualiza obstáculos
            level.getPlayer().update(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT, level.getWalls()); // Actualiza jugador
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
        g2d.setColor(new Color(50, 50, 50));
        g2d.fillRect(0, 0, Config.WINDOW_WIDTH, 30);

        // Texto del header
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        int totalLevels = LevelLoader.getTotalLevels(); // Obtener el total de niveles
        g2d.drawString("Nivel: " + (currentLevel + 1) + "/" + totalLevels, 20, 22);
        g2d.drawString("Muertes: " + deathCount, Config.WINDOW_WIDTH - 150, 22);

        drawGame(g2d);
    }

    private void drawGame(Graphics2D g2d) {
        // Mover todo el contenido del juego 30 píxeles hacia abajo
        g2d.translate(0, 30);
        
        // Dibujar el tablero de ajedrez
        int tileSize = 25; // Tamaño de cada cuadrado
        boolean isWhite = true;

        for (int y = 0; y < Config.WINDOW_HEIGHT; y += tileSize) {
            for (int x = 0; x < Config.WINDOW_WIDTH; x += tileSize) {
                if (isWhite) {
                    g2d.setColor(new Color(240, 240, 240)); // Gris muy claro
                } else {
                    g2d.setColor(new Color(220, 220, 220)); // Gris claro
                }
                g2d.fillRect(x, y, tileSize, tileSize);
                isWhite = !isWhite;
            }
            isWhite = !isWhite; // Cambiar el patrón en cada fila
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
}