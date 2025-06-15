package game.view;

import game.model.Coin;
import game.model.GameModel;
import game.model.Level;
import game.model.Obstacle;
import game.model.Player;

public class ConsoleView {

    /**
     * Dibuja el estado completo del juego en la consola.
     */
    public void render(GameModel model) {
        if (model == null || model.getCurrentLevel() == null) {
            System.out.println("Cargando...");
            return;
        }

        Level level = model.getCurrentLevel();
        int[][] tileMap = level.getTileMap();
        int tileSize = level.getTileSize();
        
        // Para simplificar, asumimos un tamaño de celda. Los cálculos convierten
        // las coordenadas del juego (pixeles) a posiciones de la grilla de la consola.
        int consoleRows = 20; // Alto de la consola en caracteres
        int consoleCols = 40; // Ancho de la consola en caracteres
        
        char[][] consoleBuffer = new char[consoleRows][consoleCols];

        // 1. Dibuja el fondo y las paredes
        for (int i = 0; i < consoleRows; i++) {
            for (int j = 0; j < consoleCols; j++) {
                // Mapea la coordenada de la consola a la del tileMap
                int mapRow = i * (tileMap.length) / consoleRows;
                int mapCol = j * (tileMap[0].length) / consoleCols;
                
                if (level.getTileMap()[mapRow][mapCol] == 1) {
                    consoleBuffer[i][j] = '#'; // Pared
                } else if (level.getTileMap()[mapRow][mapCol] == 2 && !level.areDoorsOpen()) {
                    consoleBuffer[i][j] = 'D'; // Puerta cerrada
                } else {
                    consoleBuffer[i][j] = '.'; // Espacio vacío
                }
            }
        }

        // 2. Dibuja los elementos del juego sobre el buffer
        Player player = level.getPlayer();
        int playerCol = (int) (player.getX() / tileSize * consoleCols / (tileMap[0].length));
        int playerRow = (int) (player.getY() / tileSize * consoleRows / (tileMap.length));
        placeInBuffer(consoleBuffer, playerRow, playerCol, 'P');

        for (Coin coin : level.getCoins()) {
            if (!coin.isCollected()) {
                int coinCol = (int) (coin.getX() / tileSize * consoleCols / (tileMap[0].length));
                int coinRow = (int) (coin.getY() / tileSize * consoleRows / (tileMap.length));
                placeInBuffer(consoleBuffer, coinRow, coinCol, 'o');
            }
        }
        
        for (Obstacle obstacle : level.getObstacles()) {
            int obsCol = (int) (obstacle.getX() / tileSize * consoleCols / (tileMap[0].length));
            int obsRow = (int) (obstacle.getY() / tileSize * consoleRows / (tileMap.length));
            placeInBuffer(consoleBuffer, obsRow, obsCol, 'X');
        }
        
        if (level.getKey() != null && !level.getKey().isCollected()) {
             int keyCol = (int) (level.getKey().getX() / tileSize * consoleCols / (tileMap[0].length));
             int keyRow = (int) (level.getKey().getY() / tileSize * consoleRows / (tileMap.length));
             placeInBuffer(consoleBuffer, keyRow, keyCol, 'K');
        }

        int goalCol = (int) (level.getGoal().getX() / tileSize * consoleCols / (tileMap[0].length));
        int goalRow = (int) (level.getGoal().getY() / tileSize * consoleRows / (tileMap.length));
        placeInBuffer(consoleBuffer, goalRow, goalCol, 'G');

        // 3. Imprime el buffer final a la consola
        clearConsole();
        System.out.println("--- World's Hardest Game (Consola) ---");
        System.out.println("Nivel: " + (model.getCurrentLevelIndex() + 1) + " | Muertes: " + model.getDeathCount());
        
        for (int i = 0; i < consoleRows; i++) {
            for (int j = 0; j < consoleCols; j++) {
                System.out.print(consoleBuffer[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("Comandos: [W] Arriba, [A] Izquierda, [S] Abajo, [D] Derecha, [Q] Salir");
    }

    // Método de ayuda para no salirse de los límites del buffer
    private void placeInBuffer(char[][] buffer, int row, int col, char c) {
        if (row >= 0 && row < buffer.length && col >= 0 && col < buffer[0].length) {
            buffer[row][col] = c;
        }
    }

    // Este método intenta limpiar la consola. Funciona en terminales reales,
    // pero puede no funcionar en las consolas de algunos IDEs.
    public final static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (final Exception e) {
            // Manejar excepción
        }
    }
}