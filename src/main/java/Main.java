import javax.swing.JFrame;
import controller.Game;
import model.Config;
import model.Player;
import model.Level;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {
        // runConsoleSimulation(); // Descomenta esto si quieres probar la simulación

        // --- CÓDIGO CORREGIDO PARA LA GUI ---
        // Ya no se necesita try-catch aquí, porque Game maneja sus propios errores.
        JFrame frame = new JFrame("World's Hardest Game");
        Game gameGUIPanel = new Game(); // Esta línea ya no da error.

        // Si la creación de Game fallara, el constructor de Game mostrará
        // un JOptionPane y llamará a System.exit(1), por lo que el código
        // siguiente no se ejecutaría en caso de error.

        gameGUIPanel.setBackground(Color.WHITE);
        gameGUIPanel.setPreferredSize(new Dimension(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT)); //
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
        }, 0, 1000 / 60); //
    }

    /**
     * Esta simulación en consola carga el juego, mueve al jugador hacia una
     * coordenada específica para recoger una moneda y luego intenta chocar
     * con un obstáculo para simular una muerte. Imprime el estado en cada paso.
     */
    private static void runConsoleSimulation() {
        System.out.println("--- Iniciando Mini Simulación en Consola ---");

        Game gameSim = new Game();

        // Si el código llega aquí, el juego se cargó correctamente.
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

        System.out.println("Nivel Inicial: " + (gameSim.getCurrentLevelIndex() + 1));
        System.out.println("Posición Inicial Jugador: (" + playerSim.getX() + ", " + playerSim.getY() + ")");
        System.out.println("Monedas Recolectadas (Nivel): " + currentLevelSim.getNumberOfCurrentlyCollectedCoinsInLevel()
                + "/" + currentLevelSim.getTotalCoinsInLevel());
        System.out.println("Muertes: " + gameSim.getDeathCount());

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
                    System.out.println("¡Moneda alcanzada!");
                }
            } else {
                // Una vez alcanzada la moneda, moverse hacia arriba para chocar.
                playerSim.setMovingUp(true);
                System.out.println("Acción: Jugador se mueve hacia arriba para buscar un obstáculo.");
            }
            
            // Actualizar el estado del juego (movimiento, colisiones, etc.)
            gameSim.update();

            // Imprimir estado actual
            System.out.println("Posición Jugador: (" + playerSim.getX() + ", " + playerSim.getY() + ")");
            System.out.println(
                    "Monedas Recolectadas (Nivel): " + currentLevelSim.getNumberOfCurrentlyCollectedCoinsInLevel()
                            + "/" + currentLevelSim.getTotalCoinsInLevel());
            System.out.println("Muertes: " + gameSim.getDeathCount());

            if (gameSim.isGameOver()) {
                System.out.println("¡GAME OVER durante la simulación!");
                break;
            }

            // Resetear flags de movimiento para el siguiente tick
            playerSim.setMovingRight(false);
            playerSim.setMovingLeft(false);
            playerSim.setMovingUp(false);
            playerSim.setMovingDown(false);
        }

        System.out.println("\n--- Fin de la Mini Simulación en Consola ---");
    }
}