package game;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import game.controller.GameController;
import game.model.GameModel;
import game.model.Level;
import game.model.Player;
import game.view.GamePanel;

import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {
        // runConsoleSimulation(); // Descomenta esto si quieres probar la simulación
        
        // Es una buena práctica ejecutar el código de la GUI de Swing en el
        // Event Dispatch Thread (EDT) para evitar problemas de concurrencia.
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                // 1. OBTENER EL CONTROLADOR (SINGLETON)
                GameController controller = GameController.getInstance();

                // 2. CREAR LA VISTA (EL PANEL DEL JUEGO)
                GamePanel gamePanel = new GamePanel(controller);

                // 3. CONECTAR LAS PIEZAS
                // El controlador necesita una referencia al panel para poder pedirle que se
                // repinte (`repaint()`).
                controller.setView(gamePanel);

                // 4. INICIALIZAR EL JUEGO
                controller.initGame();

                // 5. CONFIGURAR LA VENTANA PRINCIPAL (JFRAME)
                JFrame frame = new JFrame("World's Hardest Game");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false);
                frame.add(gamePanel); // Añadimos nuestro panel MVC-compatible.
                frame.pack(); // Ajusta el tamaño de la ventana al del panel.
                frame.setLocationRelativeTo(null); // Centra la ventana.
                frame.setVisible(true);

                // Es crucial que el panel tenga el foco para poder escuchar los eventos del teclado.
                gamePanel.requestFocusInWindow();

                // 6. INICIAR EL BUCLE DEL JUEGO (GAME LOOP)
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        // El bucle ahora hace dos cosas:
                        controller.update(); // 1. Actualiza el estado del juego.
                        gamePanel.repaint(); // 2. Pide a la vista que se repinte.
                    }
                }, 0, 1000 / 60); // 60 FPS

            } catch (Exception e) {
                // Si algo catastrófico ocurre durante la inicialización, lo capturamos aquí.
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Ocurrió un error fatal al iniciar el juego: " + e.getMessage(),
                        "Error Crítico",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }

    /**
     * Simulación en consola actualizada para la arquitectura MVC.
     * Carga el juego, mueve al jugador y simula una muerte.
     */
    private static void runConsoleSimulation() {
        System.out.println("--- Iniciando Simulación en Consola (MVC) ---");

        // 1. Obtenemos la instancia del controlador. No hay vista (panel).
        GameController controller = GameController.getInstance();
        controller.initGame(); // Inicializamos el modelo del juego.

        // 2. Obtenemos el modelo y el jugador a través del controlador.
        GameModel modelSim = controller.getGameModel();
        if (modelSim == null) {
            System.out.println("Error: No se pudo inicializar el modelo del juego.");
            return;
        }
        Player playerSim = modelSim.getPlayer();
        Level currentLevelSim = modelSim.getCurrentLevel();

        if (playerSim == null || currentLevelSim == null) {
            System.out.println("Error: No se pudo obtener el jugador o el nivel para la simulación.");
            System.out.println("--- Fin de la Simulación ---");
            return;
        }

        System.out.println("Nivel Inicial: " + (controller.getGameModel().getCurrentLevelIndex() + 1));
        System.out.println("Posición Inicial Jugador: (" + playerSim.getX() + ", " + playerSim.getY() + ")");
        System.out
                .println("Monedas Recolectadas (Nivel): " + currentLevelSim.getNumberOfCurrentlyCollectedCoinsInLevel()
                        + "/" + currentLevelSim.getTotalCoinsInLevel());
        System.out.println("Muertes: " + controller.getGameModel().getDeathCount());

        // La lógica de la simulación es la misma
        int targetX = 300;
        int targetY = 250;
        boolean monedaAlcanzada = false;
        int ticksToSimulate = 50;

        for (int i = 0; i < ticksToSimulate; i++) {
            System.out.println("\n--- Tick de Simulación " + (i + 1) + " ---");

            if (!monedaAlcanzada) {
                if (Math.abs(playerSim.getX() - targetX) > playerSim.getSpeed()) {
                    playerSim.setMovingRight(playerSim.getX() < targetX);
                    playerSim.setMovingLeft(playerSim.getX() > targetX);
                } else if (Math.abs(playerSim.getY() - targetY) > playerSim.getSpeed()) {
                    playerSim.setMovingDown(playerSim.getY() < targetY);
                    playerSim.setMovingUp(playerSim.getY() > targetY);
                } else {
                    monedaAlcanzada = true;
                    System.out.println("¡Objetivo de coordenadas alcanzado!");
                }
            } else {
                // Moverse hacia arriba para chocar con un obstáculo.
                playerSim.setMovingUp(true);
                System.out.println("Acción: Jugador se mueve hacia arriba para buscar un obstáculo.");
            }

            // 3. Llamamos al update del controlador para que corra la lógica del juego.
            controller.update();

            // Imprimir estado actual, obteniendo los datos desde el modelo.
            System.out.println("Posición Jugador: (" + playerSim.getX() + ", " + playerSim.getY() + ")");
            System.out.println(
                    "Monedas Recolectadas (Nivel): " + currentLevelSim.getNumberOfCurrentlyCollectedCoinsInLevel()
                            + "/" + currentLevelSim.getTotalCoinsInLevel());
            System.out.println("Muertes: " + modelSim.getDeathCount());

            if (modelSim.isGameOver()) {
                System.out.println("¡GAME OVER durante la simulación!");
                break;
            }

            // Resetear flags de movimiento para el siguiente tick.
            playerSim.setMovingRight(false);
            playerSim.setMovingLeft(false);
            playerSim.setMovingUp(false);
            playerSim.setMovingDown(false);
        }

        System.out.println("\n--- Fin de la Simulación en Consola ---");
    }
}