import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LevelLoader {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static JsonNode rootNode; // Agregamos esta variable para cachear el JSON


    public static Level loadLevel(int levelIndex) {
        try {
            InputStream is = LevelLoader.class.getResourceAsStream("/levels.json");
            JsonNode root = mapper.readTree(is);
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

            // Cargar paredes
            List<Wall> walls = new ArrayList<>();
            for (JsonNode wallNode : levelNode.get("walls")) {
                walls.add(new Wall(
                        wallNode.get("x").floatValue(),
                        wallNode.get("y").floatValue(),
                        wallNode.get("width").intValue(),
                        wallNode.get("height").intValue()));
            }
            level.setWalls(walls);

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
}