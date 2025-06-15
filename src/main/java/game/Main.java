package game;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import game.controller.GameController;
import game.model.GameModel;
import game.model.Level;
import game.model.Player;
import game.view.ConsoleView;
import game.view.GamePanel;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String choice;

        while (true) { // Bucle infinito hasta que el usuario elija salir
            choice = displayMainMenu(scanner); // Llama al nuevo método del menú

            switch (choice) {
                case "1":
                    runGraphicalGame();
                    // Nota: El programa terminará cuando se cierre la ventana de Swing.
                    return; // Salir del método main
                case "2":
                    runInteractiveConsoleGame();
                    break; // Vuelve al menú después de que termine el juego de consola
                case "3":
                    runConsoleSimulation();
                    System.out.println("Presiona Enter para continuar...");
                    scanner.nextLine(); // Espera a que el usuario presione Enter
                    break; // Vuelve al menú después de la simulación
                case "4":
                    System.out.println("¡Gracias por jugar! Adiós.");
                    scanner.close();
                    return; // Termina la aplicación
                default:
                    System.out.println("Opción no válida. Por favor, inténtalo de nuevo.");
                    System.out.println("Presiona Enter para continuar...");
                    scanner.nextLine(); // Espera a que el usuario presione Enter
                    break;
            }
        }
    }

    private static void runGraphicalGame() {
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

                // Es crucial que el panel tenga el foco para poder escuchar los eventos del
                // teclado.
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
     * Nuevo método para correr el juego de forma interactiva en la consola.
     */
    private static void runInteractiveConsoleGame() {
        GameController controller = GameController.getInstance();
        controller.initGame();

        GameModel model = controller.getGameModel();
        ConsoleView consoleView = new ConsoleView();
        try (Scanner scanner = new Scanner(System.in)) {

            // Bucle de juego principal y de un solo hilo
            while (!model.isGameOver()) {
                consoleView.render(model); // Dibuja el estado actual

                Level currentLevel = model.getCurrentLevel();
                if (currentLevel == null) {
                    break; // El juego ha terminado
                }

                System.out.print("Comando (w,a,s,d) y Enter (q para salir): ");
                String input = scanner.nextLine().trim().toLowerCase();

                if (input.isEmpty()) {
                    continue;
                }
                char command = input.charAt(0);

                if (command == 'q') {
                    break;
                }

                Player player = model.getPlayer();
                float currentX = player.getX();
                float currentY = player.getY();
                int stepSize = currentLevel.getTileSize(); // Nos moveremos una celda entera

                // Modificamos directamente la posición del jugador
                switch (command) {
                    case 'w':
                        player.setPosition(currentX, currentY - stepSize);
                        break;
                    case 's':
                        player.setPosition(currentX, currentY + stepSize);
                        break;
                    case 'a':
                        player.setPosition(currentX - stepSize, currentY);
                        break;
                    case 'd':
                        player.setPosition(currentX + stepSize, currentY);
                        break;
                }

                // Después de mover al jugador, llamamos a update() una vez.
                // Esto hará que los obstáculos se muevan un paso y que se
                // comprueben TODAS las colisiones en la nueva posición.
                controller.update();
            }

            System.out.println("¡Juego Terminado!");
            System.out.println("Muertes totales: " + model.getDeathCount());
        }
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

    private static String displayMainMenu(Scanner scanner) {
        // Limpia la consola para una presentación limpia
        ConsoleView.clearConsole();

        // Arte ASCII para el título (¡puedes personalizarlo!)
        // System.out.println("============================================================");
        // System.out.println(" ADVERTENCIA: El siguiente juego puede causar...");
        // System.out.println(" ...frustración, enojo y ganas de voltear la mesa.");
        // System.out.println();
        // System.out.println(" (╯°□°)╯︵ ┻━┻");
        // System.out.println();
        // System.out.println(" EL JUEGO MÁS DIFÍCIL DEL MUNDO");
        // System.out.println("============================================================");
        System.out.println("------------------------------------------------------------");
        System.out.println("        Respira hondo. Encuentra tu paz interior.");
        System.out.println("                      (-_-)");
        System.out.println("              ...la vas a necesitar.");
        System.out.println();
        System.out.println("              EL JUEGO MÁS DIFÍCIL DEL MUNDO");
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("               MENÚ PRINCIPAL");
        System.out.println("-----------------------------------------------------------");
        System.out.println("  1. Jugar (Interfaz Gráfica)");
        System.out.println("  2. Jugar (Consola Interactiva)");
        System.out.println("  3. Simulación (Consola)");
        System.out.println("  4. Salir");
        System.out.println("-----------------------------------------------------------");
        System.out.print("Por favor, elige una opción: ");

        return scanner.nextLine().trim();
    }
}
