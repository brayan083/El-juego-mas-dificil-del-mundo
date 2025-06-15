package game.view; 

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;

import game.model.Coin;
import game.model.Config;
import game.model.GameModel;
import game.model.Goal;
import game.model.Key;
import game.model.Level;
import game.model.Obstacle;
import game.model.Player;

public class GameView {

    public void renderGame(Graphics2D g2d, GameModel model) {
        Level currentLevel = model.getCurrentLevel(); // Obtenemos el nivel desde el modelo.

        // Dibujar el header, pasándole el modelo.
        drawHeader(g2d, model);

        g2d.translate(0, Config.HEADER_HEIGHT);

        g2d.setColor(Config.COLOR_PLAY_AREA_BACKGROUND);
        g2d.fillRect(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT - Config.HEADER_HEIGHT);

        // La lógica para game over ahora consulta directamente al modelo.
        if (model.isGameOver() && currentLevel == null) {
            g2d.translate(0, -Config.HEADER_HEIGHT);
            drawGameOverScreen(g2d, "¡Juego Completado!");
        } else if (currentLevel != null) {
            Shape playArea = calculatePlayArea(currentLevel);
            if (playArea != null) {
                Shape originalClip = g2d.getClip();
                g2d.setClip(playArea);
                drawChessboardBackground(g2d, currentLevel);
                g2d.setClip(originalClip);
            }
            drawLevelComponents(g2d, currentLevel);
        }
    }

    private void drawHeader(Graphics2D g2d, GameModel model) {
        g2d.setColor(Config.COLOR_HEADER_BACKGROUND);
        g2d.fillRect(0, 0, Config.WINDOW_WIDTH, Config.HEADER_HEIGHT);

        g2d.setColor(Config.COLOR_HEADER_TEXT);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 18));

        // Obtenemos los datos directamente del modelo.
        String levelText = "Nivel: " + (model.getCurrentLevelIndex() + 1) + "/" + model.getTotalLevels();
        g2d.drawString(levelText, 20, Config.HEADER_HEIGHT / 2 + 5);

        String coinsText = "Monedas: 0/0";
        Level currentLevel = model.getCurrentLevel();
        if (currentLevel != null) {
            int collectedInLevel = currentLevel.getNumberOfCurrentlyCollectedCoinsInLevel();
            int totalInLevel = currentLevel.getTotalCoinsInLevel();
            coinsText = "Monedas: " + collectedInLevel + "/" + totalInLevel;
        }

        FontMetrics metrics = g2d.getFontMetrics();
        int coinsTextWidth = metrics.stringWidth(coinsText);
        g2d.drawString(coinsText, (Config.WINDOW_WIDTH - coinsTextWidth) / 2, Config.HEADER_HEIGHT / 2 + 5);

        // Obtenemos el contador de muertes del modelo.
        String deathsText = "Muertes: " + model.getDeathCount();
        int deathsTextWidth = metrics.stringWidth(deathsText);
        g2d.drawString(deathsText, Config.WINDOW_WIDTH - deathsTextWidth - 20, Config.HEADER_HEIGHT / 2 + 5);
    }

    private Shape calculatePlayArea(Level level) {
        if (level == null || level.getTileMap() == null) {
            return new Rectangle(0, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT - Config.HEADER_HEIGHT);
        }

        int[][] tileMap = level.getTileMap();
        int tileSize = level.getTileSize();
        Area playAreaShape = new Area();

        for (int i = 0; i < tileMap.length; i++) {
            for (int j = 0; j < tileMap[i].length; j++) {
                if (tileMap[i][j] == 0 || tileMap[i][j] == 2) {
                    playAreaShape.add(new Area(new Rectangle(j * tileSize, i * tileSize, tileSize, tileSize)));
                }
            }
        }
        return playAreaShape;
    }

    private void drawChessboardBackground(Graphics2D g2d, Level level) {
        int chessboardTileSize = Config.CHESSBOARD_TILE_SIZE; // Tamaño de cada cuadrado del fondo. Puede ser diferente al tileSize del nivel.
        boolean isWhite = true; // Determina el color del primer tile (0,0) del chessboard

        for (int y = 0; y < Config.WINDOW_HEIGHT - Config.HEADER_HEIGHT; y += chessboardTileSize) {
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
            boolean doorsOpen = level.areDoorsOpen(); // Obtener el estado de la puerta

            for (int i = 0; i < tileMap.length; i++) {
                for (int j = 0; j < tileMap[i].length; j++) {
                    if (tileMap[i][j] == Config.TILE_WALL) { // Pared
                        g2d.setColor(Config.COLOR_WALL_TILE);
                        g2d.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);

                        // ---- ESTA LÍNEA ES LA CLAVE DEL DIBUJADO ----
                        // Solo dibuja la puerta (2) si las puertas están CERRADAS (!doorsOpen)
                    } else if (tileMap[i][j] == Config.TILE_DOOR && !doorsOpen) {
                        g2d.setColor(Config.COLOR_SAFE_ZONE_TILE);
                        g2d.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                    }
                }
            }
        }

        // Dibujar llave (si existe y no ha sido recolectada)
        Key key = level.getKey();
        if (key != null && !key.isCollected()) {
            drawKey(g2d, key);
        }

        // Dibujar obstáculos
        ArrayList<Obstacle> obstacles = level.getObstacles();
        if (obstacles != null) {
            for (Obstacle obstacle : obstacles) {
                drawObstacle(g2d, obstacle);
            }
        }

        // Dibujar monedas
        ArrayList<Coin> coins = level.getCoins();
        if (coins != null) {
            for (Coin coin : coins) {
                if (!coin.isCollected()) { // Solo dibujar si no ha sido recolectada
                    drawCoin(g2d, coin);
                }
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

    private void drawKey(Graphics2D g2d, Key key) {
        Rectangle bounds = key.getBounds();

        // La cabeza de la llave será un círculo tan ancho como la 'width' de la llave
        int headDiameter = bounds.width;

        // El vástago será un rectángulo delgado y centrado
        int shaftWidth = 4; // Grosor del vástago
        int shaftX = bounds.x + (headDiameter / 2) - (shaftWidth / 2); // Lo centramos
        // El vástago empieza en el centro de la cabeza y baja hasta el final
        int shaftY = bounds.y + (headDiameter / 2);
        int shaftHeight = bounds.height - (headDiameter / 2);

        // El diente de la llave
        int toothWidth = headDiameter / 2 + 2;
        int toothHeight = 5;
        int toothY = bounds.y + bounds.height - toothHeight - 2;

        // --- Dibujamos la llave por partes ---

        // 1. Dibuja el vástago (el cuerpo principal)
        g2d.setColor(Config.COLOR_KEY_GOLD);
        g2d.fillRect(shaftX, shaftY, shaftWidth, shaftHeight);

        // 2. Dibuja el diente
        g2d.fillRect(shaftX, toothY, toothWidth, toothHeight);

        // 3. Dibuja la cabeza de la llave
        g2d.setColor(Config.COLOR_KEY_GOLD);
        g2d.fillOval(bounds.x, bounds.y, headDiameter, headDiameter);

        // 4. Dibuja un "agujero" o detalle oscuro en la cabeza para darle profundidad
        g2d.setColor(Config.COLOR_KEY_SHADOW);
        g2d.fillOval(bounds.x + 3, bounds.y + 3, headDiameter - 6, headDiameter - 6);
    }

    private void drawPlayer(Graphics2D g2d, Player player) {
        g2d.setColor(Config.COLOR_PLAYER);
        // Es necesario convertir x e y a int porque fillRect los requiere.
        g2d.fillRect(
                (int) player.getX(),
                (int) player.getY(),
                player.getSize(),
                player.getSize());
    }

    private void drawObstacle(Graphics2D g2d, Obstacle obstacle) {
        g2d.setColor(Config.COLOR_OBSTACLE);
        g2d.fillOval((int) (obstacle.getX() - obstacle.getRadius()),
                (int) (obstacle.getY() - obstacle.getRadius()),
                obstacle.getRadius() * 2,
                obstacle.getRadius() * 2);
    }

    private void drawGoal(Graphics2D g2d, Goal goal) {
        g2d.setColor(Config.COLOR_GOAL);
        Rectangle bounds = goal.getBounds(); 
        g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    // Método para dibujar una moneda
    private void drawCoin(Graphics2D g2d, Coin coin) {
        g2d.setColor(Config.COLOR_COIN);
        g2d.fillOval(
                (int) (coin.getX() - coin.getRadius()),
                (int) (coin.getY() - coin.getRadius()),
                coin.getRadius() * 2,
                coin.getRadius() * 2);
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