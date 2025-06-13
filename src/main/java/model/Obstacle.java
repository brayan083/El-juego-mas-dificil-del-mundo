package model;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.List;
import java.util.ArrayList;

public class Obstacle {
    private float x, y; // Posición del centro del círculo
    private int radius; // Radio del círculo
    private float speed; // Velocidad (positiva o negativa)
    private boolean isHorizontal; // True: mueve horizontal, False: mueve vertical
    private int windowWidth, windowHeight; // Límites de la ventana
    private List<Rectangle> customBounceBarriers;

    // Constructor principal que incluye barreras de rebote personalizadas
    public Obstacle(float x, float y, int radius, float speed, boolean isHorizontal, int windowWidth,
                    int windowHeight, List<Rectangle> customBounceBarriers) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.speed = speed;
        this.isHorizontal = isHorizontal;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        // Asegura que customBounceBarriers nunca sea null para evitar NullPointerExceptions
        this.customBounceBarriers = (customBounceBarriers != null) ? customBounceBarriers : new ArrayList<>();
    }

    // Constructor sobrecargado (sin barreras personalizadas explícitas)
    // Este constructor resuelve el error "undefined constructor" si se llama sin la lista de barreras.
    // Delega al constructor principal con una lista vacía de barreras.
    public Obstacle(float x, float y, int radius, float speed, boolean isHorizontal, int windowWidth, int windowHeight) {
        this(x, y, radius, speed, isHorizontal, windowWidth, windowHeight, new ArrayList<>());
    }

    public void update(int[][] tileMap, int tileSize) {
        // Posición tentativa para el próximo frame
        float tentativeX = x;
        float tentativeY = y;

        if (isHorizontal) {
            tentativeX += speed;
        } else {
            tentativeY += speed;
        }

        // Límites del obstáculo en la posición tentativa
        Rectangle nextPosBounds = new Rectangle(
                (int) (tentativeX - radius),
                (int) (tentativeY - radius),
                radius * 2,
                radius * 2);

        // Comprobar colisiones
        boolean collisionWithTileMap = collidesWithTileMap(nextPosBounds, tileMap, tileSize);
        boolean collisionWithCustomBarrier = false;
        for (Rectangle barrier : this.customBounceBarriers) {
            if (nextPosBounds.intersects(barrier)) {
                collisionWithCustomBarrier = true;
                break;
            }
        }

        boolean internalCollision = collisionWithTileMap || collisionWithCustomBarrier;

        if (isHorizontal) {
            // Colisión con bordes de ventana o barreras internas
            if (tentativeX - radius < 0 || tentativeX + radius > windowWidth || internalCollision) {
                speed = -speed; // Invertir velocidad
                if (internalCollision) {
                    // No actualizar 'x', el obstáculo se queda en su posición actual
                    // para evitar penetrar la barrera. Se moverá en la nueva dirección en el próximo frame.
                } else if (tentativeX - radius < 0) {
                    x = radius; // Ajustar al borde izquierdo
                } else if (tentativeX + radius > windowWidth) {
                    x = windowWidth - radius; // Ajustar al borde derecho
                }
            } else {
                x = tentativeX; // Mover si no hay colisión
            }
        } else { // Movimiento vertical
            // Colisión con bordes de ventana o barreras internas
            if (tentativeY - radius < 0 || tentativeY + radius > windowHeight || internalCollision) {
                speed = -speed; // Invertir velocidad
                if (internalCollision) {
                    // No actualizar 'y' por la misma razón que con 'x'.
                } else if (tentativeY - radius < 0) {
                    y = radius; // Ajustar al borde superior
                } else if (tentativeY + radius > windowHeight) {
                    y = windowHeight - radius; // Ajustar al borde inferior
                }
            } else {
                y = tentativeY; // Mover si no hay colisión
            }
        }
    }

    private boolean collidesWithTileMap(Rectangle objectBounds, int[][] tileMap, int tileSize) {
        int startCol = Math.max(0, objectBounds.x / tileSize);
        int endCol = Math.min(tileMap[0].length - 1, (objectBounds.x + objectBounds.width) / tileSize);
        int startRow = Math.max(0, objectBounds.y / tileSize);
        int endRow = Math.min(tileMap.length - 1, (objectBounds.y + objectBounds.height) / tileSize);

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                if (tileMap[row][col] == 1) { // 1 representa una pared
                    Rectangle tileRect = new Rectangle(col * tileSize, row * tileSize, tileSize, tileSize);
                    if (objectBounds.intersects(tileRect)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Ellipse2D getBounds() {
        return new Ellipse2D.Float(x - radius, y - radius, radius * 2, radius * 2);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public int getRadius() { return radius; }
}