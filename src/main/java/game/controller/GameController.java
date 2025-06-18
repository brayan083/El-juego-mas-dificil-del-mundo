package game.controller;

import javax.swing.JOptionPane;

import game.model.GameModel;
import game.model.Key;
import game.model.Level;
import game.model.Player;
import game.model.exception.LevelLoadException;
import game.observer.UIUpdater;
import game.utils.CollisionUtil;
import game.view.GamePanel;

public class GameController {

    // --- INICIO: Implementación del Patrón Singleton ---

    // 1. Una variable estática y privada para guardar la ÚNICA instancia de la
    // clase.
    // Empieza siendo 'null'.
    private static GameController instance;

    // 2. El constructor es PRIVADO.
    // Esto es fundamental, ya que impide que se puedan crear instancias
    // con 'new GameController()' desde fuera de esta clase.
    private GameController() {
        // Obtenemos la única instancia de InputHandler a través de su
        // "puerta de entrada" pública: el método getInstance().
        this.inputHandler = InputHandler.getInstance();
    }

    // 3. Un método PÚBLICO y ESTÁTICO que crea la instancia si no existe
    // y la devuelve. Este es el único punto de acceso a la clase.
    // La palabra 'synchronized' lo hace seguro para usarse en diferentes hilos.
    public static synchronized GameController getInstance() {
        // Si la instancia todavía es 'null' (es la primera vez que se llama al
        // método)...
        if (instance == null) {
            // ...se crea la instancia por primera y única vez.
            instance = new GameController();
        }
        // Se devuelve la instancia (la nueva o la que ya existía).
        return instance;
    }

    // --- FIN: Implementación del Patrón Singleton ---

    // Atributos del controlador para manejar el juego
    private GameModel model;
    private GamePanel view;
    private final InputHandler inputHandler;

    /**
     * Conecta la vista (el panel) con el controlador.
     * Esto se llama desde Main.java después de crear ambos.
     */
    public void setView(GamePanel view) {
        this.view = view;
    }

    /**
     * Inicializa el modelo del juego (carga el primer nivel, etc.).
     * Se llama desde Main.java después de que el controlador y la vista están
     * conectados.
     */
    public void initGame() {
        try {
            if (model == null) { // Solo crea una nueva instancia si no existe
                this.model = new GameModel();
                inputHandler.setActivePlayer(model.getPlayer());

                UIUpdater uiUpdater = new UIUpdater();
                model.getSubject().addObserver(uiUpdater);
            } else {
                model.setGameOver(false); // Reinicia el estado de fin de juego
                model.incrementLevelIndex(); // Asegura que comience desde el nivel 0
                model.loadLevel(0); // Fuerza la recarga del primer nivel
                if (model.getCurrentLevel() == null) {
                    throw new LevelLoadException("No se pudo cargar el nivel inicial.");
                }
                model.getCurrentLevel().resetCoinsInLevel(); // Reinicia monedas
                model.setDeathCount(0); // Reinicia contador de muertes para nueva partida
                Player player = model.getPlayer();
                if (player != null) {
                    player.setPosition(model.getCurrentLevel().getInitialPlayerX(), model.getCurrentLevel().getInitialPlayerY());
                }
                inputHandler.setActivePlayer(model.getPlayer());
            }
        } catch (LevelLoadException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error crítico al cargar niveles: " + e.getMessage(), "Error Crítico",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    /**
     * El método principal de actualización, llamado repetidamente por el Timer en
     * Main.java.
     */
    public void update() {
        if (model == null || model.isGameOver()) {
            return;
        }
        model.update(); // 1. Actualiza la lógica del modelo
        checkCollisions(); // 2. Revisa las colisiones
    }

    private void checkCollisions() {
        Player player = model.getPlayer();
        Level level = model.getCurrentLevel();

        if (player == null || level == null) return;

        // Colisión con obstáculos
        for (game.model.Obstacle obstacle : level.getObstacles()) {
            if (CollisionUtil.intersects(player, obstacle)) {
                model.playerDied(); 
                return; // Si el jugador muere, no necesitamos chequear más colisiones en este frame.
            }
        }

        // Colisión con la llave
        Key key = level.getKey();
        if (key != null && !key.isCollected() && CollisionUtil.intersects(player, key)) {
            model.collectKey(); 
            level.openDoors();
        }

        // Colisión con monedas
        for (game.model.Coin coin : level.getCoins()) {
            if (!coin.isCollected() && CollisionUtil.intersects(player, coin)) {
                model.collectCoin(coin);
            }
        }

        // Colisión con la meta
        if (CollisionUtil.intersects(player, level.getGoal())) {
            if (level.areAllCoinsCollectedInLevel()) {
                try {
                    model.completeLevel();
                    if(model.getPlayer() != null) {
                        inputHandler.setActivePlayer(model.getPlayer());
                    }
                } catch (LevelLoadException e) {
                    model.setGameOver(true);
                }
            }
        }
    }

    public void endGame(String playerName) {
        if (model.isGameOver()) { // Solo guarda el nombre si el juego ya terminó
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = JOptionPane.showInputDialog("¡Juego completado! Ingresa tu nombre para el top 10:");
                if (playerName == null || playerName.trim().isEmpty()) {
                    playerName = "Jugador Anónimo";
                }
            }
            model.addToTopTen(playerName); // Agrega el nombre al top 10
        }
    }

    // Getters para que la Vista (GamePanel y GameView) pueda obtener la información
    // que necesita.
    public GameModel getGameModel() {
        return model;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }
}