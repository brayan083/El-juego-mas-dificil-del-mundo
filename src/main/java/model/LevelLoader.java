package model;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LevelLoader {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static JsonNode rootNode = null; // Para cachear el JSON

    // Metodo privado para inicializar y obtener rootNode
    private static JsonNode getRootNode() {
        if (rootNode == null) {
            try {
                InputStream is = LevelLoader.class.getResourceAsStream("/levels.json");
                rootNode = mapper.readTree(is);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error fatal: No se pudo cargar el archivo de niveles");
            }
        }
        return rootNode;
    }

    public static Level loadLevel(int levelIndex) {
        try {
            JsonNode root = getRootNode();
            if (root == null || levelIndex >= root.get("levels").size() || levelIndex < 0) {
                return null; // Devolver null si el índice no es válido
            }

            JsonNode levelNode = root.get("levels").get(levelIndex);

            // Crear nivel con dimensiones de ventana
            Level level = new Level(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

            // Cargar jugador
            JsonNode playerNode = levelNode.get("player");
            level.setPlayer(new Player(
                    playerNode.get("x").floatValue(),
                    playerNode.get("y").floatValue(),
                    playerNode.get("size").intValue(),
                    playerNode.get("speed").floatValue()));

            // System.out.println(playerNode);

            // Cargar meta
            JsonNode goalNode = levelNode.get("goal");
            level.setGoal(new Goal(
                    goalNode.get("x").floatValue(),
                    goalNode.get("y").floatValue(),
                    goalNode.get("width").intValue(),
                    goalNode.get("height").intValue()));

            // Cargar obstáculos
            List<Obstacle> obstacles = new ArrayList<>();
            for (JsonNode obstacleNode : levelNode.get("obstacles")) {
                obstacles.add(new Obstacle(
                        obstacleNode.get("x").floatValue(),
                        obstacleNode.get("y").floatValue(),
                        obstacleNode.get("radius").intValue(),
                        obstacleNode.get("speed").floatValue(),
                        obstacleNode.get("horizontal").booleanValue(),
                        Config.WINDOW_WIDTH,
                        Config.WINDOW_HEIGHT));
            }
            level.setObstacles(obstacles);

            // Cargar monedas 
            List<Coin> coins = new ArrayList<>();
            if (levelNode.has("coins")) { // Verificar si el array "coins" existe
                for (JsonNode coinNode : levelNode.get("coins")) {
                    coins.add(new Coin(
                            coinNode.get("x").floatValue(),
                            coinNode.get("y").floatValue(),
                            coinNode.get("radius").intValue()
                    ));
                }
            }
            level.setCoins(coins);

            // Cargar tileMap
            JsonNode tileMapNode = levelNode.get("tileMap");
            if (tileMapNode != null) {
                int rows = tileMapNode.size();
                int cols = tileMapNode.get(0).size();
                int[][] tileMap = new int[rows][cols];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        tileMap[i][j] = tileMapNode.get(i).get(j).intValue();
                    }
                }
                int tileSize = levelNode.get("tileSize").intValue();
                level.setTileMap(tileMap, tileSize);
            }

            return level;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para obtener los datos de un nivel específico
    public static JsonNode getLevelData(int levelIndex) {
        try {
            if (rootNode == null) {
                InputStream is = LevelLoader.class.getResourceAsStream("/levels.json");
                rootNode = mapper.readTree(is);
            }
            return rootNode.get("levels").get(levelIndex);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getTotalLevels() {
        try {
            if (rootNode == null) {
            InputStream is = LevelLoader.class.getResourceAsStream("/levels.json");
            rootNode = mapper.readTree(is);
            }
            return rootNode.get("levels").size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}