package model;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Obstacle {
    private float x, y; // Posición del centro del círculo
    private int radius; // Radio del círculo
    private float speed; // Velocidad (positiva o negativa)
    private boolean isHorizontal; // True: mueve horizontal, False: mueve vertical
    private int windowWidth, windowHeight; // Límites de la ventana

    // Constructor
    public Obstacle(float x, float y, int radius, float speed, boolean isHorizontal, int windowWidth,
            int windowHeight) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.speed = speed; // Velocidad única (se aplica a X o Y según isHorizontal)
        this.isHorizontal = isHorizontal;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    // Actualizar posición con rebote
    public void update(int[][] tileMap, int tileSize) {
        // Calcular nueva posición
        float newX = x + (isHorizontal ? speed : 0);
        float newY = y + (isHorizontal ? 0 : speed);

        // Crear un área de prueba para la nueva posición
        Ellipse2D nextPos = new Ellipse2D.Float(newX - radius, newY - radius, radius * 2, radius * 2);

        // Convertir Rectangle2D a Rectangle
        Rectangle bounds = new Rectangle(
                (int) nextPos.getX(),
                (int) nextPos.getY(),
                (int) nextPos.getWidth(),
                (int) nextPos.getHeight());

        // Verificar colisión con paredes
        boolean collision = collidesWithTileMap(bounds, tileMap, tileSize);
        if (isHorizontal) {
            // Verificar colisión con bordes de la ventana o paredes
            if (newX - radius <= 0 || newX + radius >= windowWidth || collision) {
                speed = -speed; // Invertir dirección
                // Ajustar posición para evitar que se pegue
                if (newX - radius <= 0) {
                    newX = radius + 1.0f; // Margen adicional
                } else if (newX + radius >= windowWidth) {
                    newX = windowWidth - radius - 1.0f;
                } else if (collision) {
                    newX = x; // Revertir movimiento
                }
            }
        } else {
            // Verificar colisión con bordes de la ventana o paredes
            if (newY - radius <= 0 || newY + radius >= windowHeight || collision) {
                speed = -speed; // Invertir dirección
                // Ajustar posición
                if (newY - radius <= 0) {
                    newY = radius + 1.0f;
                } else if (newY + radius >= windowHeight) {
                    newY = windowHeight - radius - 1.0f;
                } else if (collision) {
                    newY = y; // Revertir movimiento
                }
            }
        }

        // Actualizar posición
        x = newX;
        y = newY;
    }

    private boolean collidesWithTileMap(Rectangle bounds, int[][] tileMap, int tileSize) {
        if (tileMap == null)
            return false;

        int minRow = Math.max(0, bounds.y / tileSize);
        int maxRow = Math.min(tileMap.length - 1, (bounds.y + bounds.height - 1) / tileSize);
        int minCol = Math.max(0, bounds.x / tileSize);
        int maxCol = Math.min(tileMap[0].length - 1, (bounds.x + bounds.width - 1) / tileSize);

        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minCol; j <= maxCol; j++) {
                if (tileMap[i][j] == 1) {
                    Rectangle tileRect = new Rectangle(j * tileSize, i * tileSize, tileSize, tileSize);
                    if (bounds.intersects(tileRect)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Obtener área para colisiones
    public Ellipse2D getBounds() {
        return new Ellipse2D.Float(x - radius, y - radius, radius * 2, radius * 2);
    }

    // Dibujar el círculo azul
    public void draw(Graphics2D g) {
        g.setColor(Color.BLUE);
        g.fillOval((int) (x - radius), (int) (y - radius), radius * 2, radius * 2);
    }
}
