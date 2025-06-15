package controller;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import handler.InputHandler;
import model.Level;
import model.LevelLoader;
import model.Player;
import model.exception.LevelLoadException;
import view.GameView;
import model.Coin;

import java.awt.*;
import java.awt.geom.AffineTransform;

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
        // Estas operaciones son seguras.
        inputHandler = new InputHandler();
        gameView = new GameView(); // <-- Mover la inicialización aquí, antes del try-catch.

        addKeyListener(inputHandler);
        setFocusable(true); // Es bueno poner esto al principio también.

        try {
            totalLevels = LevelLoader.getTotalLevels();
            loadLevel(currentLevelIndex);
            gameOver = false;
        } catch (LevelLoadException e) {
            // Si la carga falla, el juego no puede empezar.
            e.printStackTrace();

            // El constructor mostrará un error y terminará la aplicación.
            // GameView ya existe, por lo que no habrá un NullPointerException si
            // el sistema intenta repintar antes de cerrar.
            JOptionPane.showMessageDialog(null, "Error al cargar niveles: " + e.getMessage(), "Error Crítico",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void loadLevel(int levelIdx) {
        try {
            Level newLevel = LevelLoader.loadLevel(levelIdx);

            // Esta sección se ejecuta solo si la carga fue exitosa
            this.level = newLevel;
            this.currentLevelIndex = levelIdx;
            gameOver = false;
            if (inputHandler != null && this.level.getPlayer() != null) {
                inputHandler.setActivePlayer(this.level.getPlayer());
            }

        } catch (LevelLoadException e) {
            // Maneja el caso en que se intenta cargar un nivel que no existe (ej. al
            // terminar el juego)
            if (levelIdx >= totalLevels && totalLevels > 0) {
                gameOver = true;
                this.level = null;
                currentLevelIndex = totalLevels;
                if (inputHandler != null) {
                    inputHandler.setActivePlayer(null);
                }
            } else {
                // Un error inesperado cargando un nivel que debería existir
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al cargar el nivel: " + e.getMessage(), "Error de Carga",
                        JOptionPane.WARNING_MESSAGE);
                gameOver = true; // No se puede continuar
            }
        }
    }

    public void update() {
        if (!gameOver && level != null) { // Solo actualizar si hay un nivel y no es game over
            level.update();
            checkCollisions();
        }
    }

    private void checkCollisions() {
        if (level == null || level.getPlayer() == null)
            return;
        Player player = level.getPlayer();

        // Colisiones con obstáculos usando nuestra nueva utilidad
        if (level.getObstacles() != null) {
            for (model.Obstacle obstacle : level.getObstacles()) {
                if (CollisionUtil.intersects(player, obstacle)) { // <-- CÓDIGO ACTUALIZADO
                    resetPlayerPosition();
                    return;
                }
            }
        }

        // Colisiones con la llave
        Key key = level.getKey();
        if (key != null && !key.isCollected()) {
            if (CollisionUtil.intersects(player, key)) { // <-- CÓDIGO ACTUALIZADO
                key.setCollected(true);
                level.openDoors();
            }
        }

        // Colisiones con monedas
        if (level.getCoins() != null) {
            for (Coin coin : level.getCoins()) {
                if (!coin.isCollected()) {
                    // La compleja verificación con Area se reemplaza por una simple llamada
                    if (CollisionUtil.intersects(player, coin)) { // <-- CÓDIGO ACTUALIZADO
                        coin.setCollected(true);
                        totalCoinsEverCollected++;
                    }
                }
            }
        }

        // Colisión con la meta
        if (level.getGoal() != null && CollisionUtil.intersects(player, level.getGoal())) { // <-- CÓDIGO ACTUALIZADO
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
            level.resetCoinsInLevel(); // Esto ya lo tenías y está bien
            Player player = level.getPlayer();
            // Simplemente resetea la posición desde el nivel
            player.setPosition(level.getInitialPlayerX(), level.getInitialPlayerY());
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