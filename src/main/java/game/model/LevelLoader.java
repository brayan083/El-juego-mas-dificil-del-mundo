package game.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import game.model.exception.LevelLoadException;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.awt.Rectangle;
import java.io.IOException; 

public class LevelLoader {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static JsonNode rootNode = null; // Para cachear el JSON

    // Metodo privado para inicializar y obtener rootNode
    private static JsonNode getRootNode() throws LevelLoadException {
        if (rootNode == null) {
            try {
                InputStream is = LevelLoader.class.getResourceAsStream("/levels.json");
                if (is == null) {
                    throw new LevelLoadException("El archivo 'levels.json' no se encontró en los recursos.");
                }
                rootNode = mapper.readTree(is);
            } catch (IOException e) {
                throw new LevelLoadException("Error crítico al cargar 'levels.json'.", e);
            }
        }
        return rootNode;
    }

    public static Level loadLevel(int levelIndex) throws LevelLoadException {
        try {
            JsonNode root = getRootNode(); // getRootNode también debe lanzar la excepción
            if (root == null || levelIndex >= root.get("levels").size() || levelIndex < 0) {
                // Lanza una excepción en lugar de devolver null para un índice inválido
                throw new LevelLoadException("Índice de nivel fuera de rango: " + levelIndex);
            }

            JsonNode levelNode = root.get("levels").get(levelIndex);

            // Crear nivel con dimensiones de ventana
            Level level = new Level(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

            // Cargar jugador
            // Dentro de loadLevel()
            JsonNode playerNode = levelNode.get("player");
            float startX = playerNode.get("x").floatValue();
            float startY = playerNode.get("y").floatValue();

            level.setPlayer(new Player(
                    startX,
                    startY,
                    playerNode.get("size").intValue(),
                    playerNode.get("speed").floatValue()));

            level.initialPlayerX = startX;
            level.initialPlayerY = startY;
            // System.out.println(playerNode);

            // Cargar meta
            JsonNode goalNode = levelNode.get("goal");
            level.setGoal(new Goal(
                    goalNode.get("x").floatValue(),
                    goalNode.get("y").floatValue(),
                    goalNode.get("width").intValue(),
                    goalNode.get("height").intValue()));

            // Cargar llave (si existe)
            if (levelNode.has("key")) {
                JsonNode keyNode = levelNode.get("key");
                level.setKey(new Key(
                        keyNode.get("x").floatValue(),
                        keyNode.get("y").floatValue(),
                        keyNode.get("width").intValue(),
                        keyNode.get("height").intValue()));
            }

            // Cargar obstáculos
            List<Obstacle> obstacles = new ArrayList<>();
            for (JsonNode obstacleNode : levelNode.get("obstacles")) {
                List<Rectangle> customBarriers = new ArrayList<>(); // Siempre crea la lista
                if (obstacleNode.has("customBounceBarriers")) {
                    for (JsonNode barrierNode : obstacleNode.get("customBounceBarriers")) {
                        customBarriers.add(new Rectangle(
                                barrierNode.get("x").intValue(),
                                barrierNode.get("y").intValue(),
                                barrierNode.get("width").intValue(),
                                barrierNode.get("height").intValue()));
                    }
                }

                obstacles.add(new Obstacle( // Llama al constructor con la lista de barreras (puede estar vacía)
                        obstacleNode.get("x").floatValue(),
                        obstacleNode.get("y").floatValue(),
                        obstacleNode.get("radius").intValue(),
                        obstacleNode.get("speed").floatValue(),
                        obstacleNode.get("horizontal").booleanValue(),
                        Config.WINDOW_WIDTH, // Asumiendo que tienes una clase Config con estas constantes
                        Config.WINDOW_HEIGHT,
                        customBarriers));
            }
            level.setObstacles(obstacles);

            // Cargar monedas
            List<Coin> coins = new ArrayList<>();
            if (levelNode.has("coins")) { // Verificar si el array "coins" existe
                for (JsonNode coinNode : levelNode.get("coins")) {
                    coins.add(new Coin(
                            coinNode.get("x").floatValue(),
                            coinNode.get("y").floatValue(),
                            coinNode.get("radius").intValue()));
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
            // Error genérico para cualquier otro problema (ej. JSON mal formado)
            throw new LevelLoadException("No se pudo parsear o cargar el nivel " + levelIndex, e);
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

    public static int getTotalLevels() throws LevelLoadException {
        try {
            JsonNode root = getRootNode();
            return root.get("levels").size();
        } catch (Exception e) {
            throw new LevelLoadException("No se pudo determinar el número total de niveles.", e);
        }
    }
}