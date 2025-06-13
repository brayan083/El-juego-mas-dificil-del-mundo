package controller;

import javax.swing.JPanel;

import com.fasterxml.jackson.databind.JsonNode;

import handler.InputHandler;
import model.Level;
import model.LevelLoader;
import model.Player;
import view.GameView;
import model.Coin;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

import model.Key;

public class Game extends JPanel {
    private Level level;
    private boolean gameOver;
    private int currentLevelIndex = 0;
    private int deathCount = 0; // Contador de muertes
    private int totalLevels = 0;
    private int totalCoinsEverCollected = 0;

    private InputHandler inputHandler; // Manejador de entrada
    private GameView gameView; // <-- NUEVA INSTANCIA

    public Game() {
        inputHandler = new InputHandler();
        addKeyListener(inputHandler); // Registrar InputHandler como KeyListener
        // Es crucial que loadLevel se llame DESPUÉS de inicializar inputHandler
        // y que inputHandler.setActivePlayer se llame DESPUÉS de que el jugador del
        // nivel esté disponible.
        gameView = new GameView(); // <-- INICIALIZAR GameView
        totalLevels = LevelLoader.getTotalLevels(); // Cargar una vez
        loadLevel(currentLevelIndex);
        gameOver = false;
        setFocusable(true); // Asegúrate de que el JPanel pueda recibir foco para los eventos de teclado
    }

    private void loadLevel(int levelIdx) {
        Level newLevel = LevelLoader.loadLevel(levelIdx);
        if (newLevel == null) {
            // Si no se pudo cargar el siguiente nivel (ej. llegamos al final o error)
            if (levelIdx >= totalLevels && totalLevels > 0) { // Asumimos que se completaron todos los niveles
                gameOver = true;
                this.level = null; // No hay nivel actual si el juego está completado
                currentLevelIndex = totalLevels; // Para mostrar N/N o similar
                if (inputHandler != null) {
                    inputHandler.setActivePlayer(null);
                }
            } else { // Error cargando un nivel que debería existir, o no hay niveles.
                System.err.println("Error al cargar el nivel: " + levelIdx + ". O no hay más niveles.");
                // Mantener el último nivel válido o manejar como juego terminado
                // Si es el primer nivel y falla, es un problema mayor.
                // Por ahora, si falla y no es el final, mantenemos el anterior
                if (this.level != null) { // Si ya había un nivel cargado
                    currentLevelIndex--; // Revertir al índice anterior si es posible
                } else { // No hay ningún nivel cargado y el primero falla
                    gameOver = true; // No se puede continuar
                }
                if (inputHandler != null) {
                    inputHandler.setActivePlayer(null);
                }
            }
            return;
        }
        this.level = newLevel;
        this.currentLevelIndex = levelIdx; // Actualizar el índice
        gameOver = false;
        if (inputHandler != null && this.level.getPlayer() != null) {
            inputHandler.setActivePlayer(this.level.getPlayer());
        } else if (inputHandler != null) {
            inputHandler.setActivePlayer(null);
        }
    }

    public void update() {
        if (!gameOver && level != null) { // Solo actualizar si hay un nivel y no es game over
            level.update();
            checkCollisions();
        }
    }

    private void checkCollisions() {
        if (level == null || level.getPlayer() == null || level.getGoal() == null)
            return; // Chequeo de seguridad

        Player player = level.getPlayer();
        Rectangle playerBounds = player.getBounds();

        // Verificar colisiones con obstáculos
        if (level.getObstacles() != null) {
            for (model.Obstacle obstacle : level.getObstacles()) { // Especificar model.Obstacle si hay ambigüedad
                if (player.getBounds().intersects(obstacle.getBounds().getBounds())) {
                    resetPlayerPosition();
                    return;
                }
            }
        }

        // Verificar colisiones con la llave
        Key key = level.getKey();
        if (key != null && !key.isCollected()) {
            if (playerBounds.intersects(key.getBounds())) {
                key.setCollected(true);
                level.openDoors(); // Abrir las puertas
            }
        }

        // Colisiones con monedas
        if (level.getCoins() != null) {
            for (Coin coin : level.getCoins()) {
                if (!coin.isCollected()) {
                    Area playerArea = new Area(playerBounds);
                    Area coinArea = new Area(coin.getBounds()); // coin.getBounds() devuelve Ellipse2D
                    playerArea.intersect(coinArea);
                    if (!playerArea.isEmpty()) {
                        coin.setCollected(true);
                        totalCoinsEverCollected++; // Incrementa el contador global
                        // System.out.println("Monedas (total juego): " + totalCoinsEverCollected);
                    }
                }
            }
        }

        // Verificar si llegó a la meta
        if (level.getGoal() != null && player.getBounds().intersects(level.getGoal().getBounds())) { //
            if (!gameOver) { //
                // ----- INICIO DE LA MODIFICACIÓN -----
                if (level.areAllCoinsCollectedInLevel()) { // ¡NUEVA CONDICIÓN!
                    currentLevelIndex++; //
                    if (currentLevelIndex < totalLevels) { //
                        loadLevel(currentLevelIndex); //
                    } else {
                        // Todos los niveles completados
                        gameOver = true; //
                        level = null; //
                        inputHandler.setActivePlayer(null); //
                        System.out.println("¡Juego Completado!"); //
                    }
                } else {
                    // Aún no ha recolectado todas las monedas.
                    // El jugador puede estar sobre la meta, pero no pasa nada.
                    // Opcional: Mostrar un mensaje al jugador.
                    System.out.println("¡Necesitas recolectar todas las monedas para avanzar!");
                }
            }
        }
    }

    private void resetPlayerPosition() {
        deathCount++;
        if (level != null) {
            level.resetCoinsInLevel();
        }
        Player player = level.getPlayer();
        JsonNode levelData = LevelLoader.getLevelData(currentLevelIndex); // Usar currentLevelIndex
        if (levelData != null) {
            JsonNode playerNode = levelData.get("player");
            if (playerNode != null) { // Chequeo adicional
                int x = (int) playerNode.get("x").floatValue();
                int y = (int) playerNode.get("y").floatValue();
                player.setPosition(x, y);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Guardar la transformación original
        AffineTransform originalTransform = g2d.getTransform();

        gameView.renderGame(g2d, this, this.level, this.totalLevels);

        // Restaurar la transformación original al final
        // Esto es importante si GameView aplica transformaciones como translate
        // y queremos que el JPanel se mantenga "limpio" para otros posibles dibujados
        // o para asegurar que el siguiente repaint comience desde un estado conocido.
        g2d.setTransform(originalTransform);

        // dispose g2d si fue creado específicamente, pero aquí es el del sistema.
    }

    // Getters para GameView (y potencialmente otros)
    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public int getCoinsCollected() {
        return totalCoinsEverCollected;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    // Este getter podría ser útil para GameView si necesita acceder directamente al
    // objeto Level
    // Alternativamente, GameView podría tomar todos los datos que necesita como
    // parámetros separados.
    public Level getCurrentLevelData() {
        return level;
    }
}