package game.utils;

import game.model.*;

/**
 * Clase de utilidad con métodos estáticos para detectar colisiones
 * entre los diferentes objetos del juego usando sus propiedades primitivas.
 */
public class CollisionUtil {

    /**
     * Verifica si un jugador (rectángulo) y un obstáculo (círculo) se intersectan.
     */
    public static boolean intersects(Player player, Obstacle obstacle) {
        // Encuentra el punto en el AABB del jugador más cercano al centro del círculo
        float closestX = Math.max(player.getX(), Math.min(obstacle.getX(), player.getX() + player.getSize()));
        float closestY = Math.max(player.getY(), Math.min(obstacle.getY(), player.getY() + player.getSize()));

        // Calcula la distancia entre el punto más cercano y el centro del círculo
        float distanceX = obstacle.getX() - closestX;
        float distanceY = obstacle.getY() - closestY;

        // Si la distancia al cuadrado es menor que el radio al cuadrado, hay colisión.
        float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
        return distanceSquared < (obstacle.getRadius() * obstacle.getRadius());
    }

    /**
     * Verifica si un jugador (rectángulo) y una moneda (círculo) se intersectan.
     */
    public static boolean intersects(Player player, Coin coin) {
        // La lógica es idéntica a la del obstáculo
        float closestX = Math.max(player.getX(), Math.min(coin.getX(), player.getX() + player.getSize()));
        float closestY = Math.max(player.getY(), Math.min(coin.getY(), player.getY() + player.getSize()));
        float distanceX = coin.getX() - closestX;
        float distanceY = coin.getY() - closestY;
        float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
        return distanceSquared < (coin.getRadius() * coin.getRadius());
    }

    /**
     * Verifica si dos rectángulos (jugador y meta) se intersectan.
     */
    public static boolean intersects(Player player, Goal goal) {
        return player.getX() < goal.getX() + goal.getWidth() &&
               player.getX() + player.getSize() > goal.getX() &&
               player.getY() < goal.getY() + goal.getHeight() &&
               player.getY() + player.getSize() > goal.getY();
    }

    /**
     * Verifica si dos rectángulos (jugador y llave) se intersectan.
     */
    public static boolean intersects(Player player, Key key) {
        if (key == null) return false;
        return player.getX() < key.getX() + key.getWidth() &&
               player.getX() + player.getSize() > key.getX() &&
               player.getY() < key.getY() + key.getHeight() &&
               player.getY() + player.getSize() > key.getY();
    }
}