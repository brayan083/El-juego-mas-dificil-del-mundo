import javax.swing.JFrame;
import controller.Game;
import model.Config;
import model.Player; // Necesario para acceder al jugador
import model.Level; // Necesario para acceder a datos del nivel
import model.LevelLoader;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {
        // Si deseas ejecutar la simulación en consola, descomenta la siguiente línea:
        // runConsoleSimulation();

        // Código existente para la GUI:
        // Se crea una nueva instancia para la GUI para evitar conflictos con la
        // simulación en consola.
        JFrame frame = new JFrame("World's Hardest Game");
        Game gameGUIPanel = new Game(); // Nueva instancia para la GUI

        gameGUIPanel.setBackground(Color.WHITE);
        gameGUIPanel.setPreferredSize(new Dimension(Config.WINDOW_WIDTH,
                Config.WINDOW_HEIGHT)); //
        frame.add(gameGUIPanel);
        frame.pack();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        gameGUIPanel.requestFocusInWindow();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                gameGUIPanel.update();
                gameGUIPanel.repaint();
            }
        }, 0, 1000 / 60); // 60 FPS
    }

    private static void runConsoleSimulation() {

        // Esta simulación en consola mueve automáticamente al jugador desde su posición
        // inicial hasta la primera moneda del nivel (en la posición 300, 250).
        // El jugador avanza primero en el eje X y luego en el eje Y hasta alcanzar la
        // moneda.
        // Una vez recolectada, el jugador comienza a moverse hacia arriba para
        // buscar un obstáculo y morir. En cada tick se imprime el estado del jugador,
        // las monedas recolectadas y la cantidad de muertes, mostrando paso a paso el
        // avance y
        // el resultado de la simulación.

        System.out.println("--- Iniciando Mini Simulación en Consola ---");

        // 1. Crear una instancia de Game.
        // El constructor de Game ya carga el nivel 0 y el jugador.
        Game gameSim = new Game();

        Player playerSim = null;
        Level currentLevelSim = gameSim.getCurrentLevelData();

        if (currentLevelSim != null) {
            playerSim = currentLevelSim.getPlayer();
        }

        if (playerSim == null) {
            System.out.println("Error: No se pudo obtener el jugador para la simulación.");
            System.out.println("--- Fin de la Mini Simulación en Consola ---");
            return;
        }

        System.out.println("Nivel Inicial: " + (gameSim.getCurrentLevelIndex() + 1)); //
        System.out.println("Posición Inicial Jugador: (" + playerSim.getX() + ", " + playerSim.getY() + ")"); //
        System.out
                .println("Monedas Recolectadas (Nivel): " + currentLevelSim.getNumberOfCurrentlyCollectedCoinsInLevel()
                        + "/" + currentLevelSim.getTotalCoinsInLevel()); //
        System.out.println("Muertes: " + gameSim.getDeathCount()); //

        int targetX = 300;
        int targetY = 250;
        boolean monedaAlcanzada = false;

        int ticksToSimulate = 50; // Puedes dejarlo alto para asegurar tiempo suficiente

        for (int i = 0; i < ticksToSimulate; i++) {
            System.out.println("\n--- Tick de Simulación " + (i + 1) + " ---");

            // Mover hasta la moneda
            if (!monedaAlcanzada) {
                if (Math.abs(playerSim.getX() - targetX) > playerSim.getSpeed()) {
                    if (playerSim.getX() < targetX) {
                        playerSim.setMovingRight(true);
                        System.out.println("Acción: Jugador intenta moverse a la derecha.");
                    } else if (playerSim.getX() > targetX) {
                        playerSim.setMovingLeft(true);
                        System.out.println("Acción: Jugador intenta moverse a la izquierda.");
                    }
                } else if (Math.abs(playerSim.getY() - targetY) > playerSim.getSpeed()) {
                    if (playerSim.getY() < targetY) {
                        playerSim.setMovingDown(true);
                        System.out.println("Acción: Jugador intenta moverse hacia abajo.");
                    } else if (playerSim.getY() > targetY) {
                        playerSim.setMovingUp(true);
                        System.out.println("Acción: Jugador intenta moverse hacia arriba.");
                    }
                } else {
                    monedaAlcanzada = true;
                    System.out.println("¡Moneda alcanzada!");
                }
            } else {
                // Aquí puedes agregar lógica para morir o terminar la simulación
                // Por ejemplo, moverse hacia arriba para buscar un obstáculo
                playerSim.setMovingUp(true);
                System.out.println("Acción: Jugador intenta moverse hacia arriba (buscar muerte).");
            }

            // Actualizar juego
            gameSim.update();

            // Imprimir estado
            if (playerSim != null && currentLevelSim != null) {
                System.out.println("Posición Jugador: (" + playerSim.getX() + ", " + playerSim.getY() + ")");
                System.out.println(
                        "Monedas Recolectadas (Nivel): " + currentLevelSim.getNumberOfCurrentlyCollectedCoinsInLevel()
                                + "/" + currentLevelSim.getTotalCoinsInLevel());
            }

            System.out.println("Muertes: " + gameSim.getDeathCount());

            if (gameSim.isGameOver()) {
                if (gameSim.getCurrentLevelIndex() != LevelLoader.getTotalLevels()) {
                    System.out.println("¡GAME OVER durante la simulación!");
                }
                break;
            }

            // Resetear flags de movimiento
            if (playerSim != null) {
                playerSim.setMovingRight(false);
                playerSim.setMovingLeft(false);
                playerSim.setMovingUp(false);
                playerSim.setMovingDown(false);
            }
        }

        System.out.println("\n--- Fin de la Mini Simulación en Consola ---");
    }

}
