package model;

import java.awt.Rectangle;

public class Player {
    private float x, y; // Posición del cuadrado
    private int size; // Tamaño (ancho y alto)
    private float speed; // Velocidad de movimiento (píxeles por actualización)
    private boolean movingUp, movingDown, movingLeft, movingRight;

    // Constructor
    public Player(float x, float y, int size, float speed) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
    }

    /**
     * MÉTODO UPDATE CORREGIDO
     * Ahora recibe el objeto Level completo.
     */
    public void update(Level level) {
        // Obtenemos todos los datos que necesitamos desde el nivel
        float newX = x;
        float newY = y;
        int[][] tileMap = level.getTileMap();
        int tileSize = level.getTileSize();
        boolean doorsOpen = level.areDoorsOpen(); // Le preguntamos al nivel por las puertas

        // Movimientos individuales con la misma velocidad
        if (movingUp) {
            Rectangle checkY = new Rectangle((int) x, (int) (y - speed), size, size);
            // Ahora pasamos 'doorsOpen' a la llamada
            if (!collidesWithTileMap(checkY, tileMap, tileSize, doorsOpen)) {
                newY -= speed;
            }
        }

        if (movingDown) {
            Rectangle checkY = new Rectangle((int) x, (int) (y + speed), size, size);
            // Ahora pasamos 'doorsOpen' a la llamada
            if (!collidesWithTileMap(checkY, tileMap, tileSize, doorsOpen)) {
                newY += speed;
            }
        }

        if (movingLeft) {
            Rectangle checkX = new Rectangle((int) (x - speed), (int) y, size, size);
            // Ahora pasamos 'doorsOpen' a la llamada
            if (!collidesWithTileMap(checkX, tileMap, tileSize, doorsOpen)) {
                newX -= speed;
            }
        }

        if (movingRight) {
            Rectangle checkX = new Rectangle((int) (x + speed), (int) y, size, size);
            // Ahora pasamos 'doorsOpen' a la llamada
            if (!collidesWithTileMap(checkX, tileMap, tileSize, doorsOpen)) {
                newX += speed;
            }
        }

        // Aplicar límites de la ventana (obtenidos del nivel)
        x = Math.max(0, Math.min(newX, level.getWindowWidth() - size));
        y = Math.max(0, Math.min(newY, level.getWindowHeight() - Config.HEADER_HEIGHT - size));
    }

    private boolean collidesWithTileMap(Rectangle bounds, int[][] tileMap, int tileSize, boolean doorsAreOpen) {
        if (tileMap == null)
            return false;

        int minRow = Math.max(0, bounds.y / tileSize);
        int maxRow = Math.min(tileMap.length - 1, (bounds.y + bounds.height - 1) / tileSize);
        int minCol = Math.max(0, bounds.x / tileSize);
        int maxCol = Math.min(tileMap[0].length - 1, (bounds.x + bounds.width - 1) / tileSize);

        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minCol; j <= maxCol; j++) {
                // ---- VERIFICA ESTA LÍNEA CUIDADOSAMENTE ----
                // La condición debe ser: el tile es un muro (1) O el tile es una puerta (2) Y las puertas están cerradas.
                if (tileMap[i][j] == 1 || (tileMap[i][j] == 2 && !doorsAreOpen)) {
                    Rectangle tileRect = new Rectangle(j * tileSize, i * tileSize, tileSize, tileSize);
                    if (bounds.intersects(tileRect)) {
                        return true; // Hay colisión
                    }
                }
            }
        }
        return false; // No hubo colisión
    }



    // Getters y Setters (sin cambios)
    public void setMovingUp(boolean moving) { movingUp = moving; }
    public void setMovingDown(boolean moving) { movingDown = moving; }
    public void setMovingLeft(boolean moving) { movingLeft = moving; }
    public void setMovingRight(boolean moving) { movingRight = moving; }
    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    public Rectangle getBounds() { return new Rectangle((int) x, (int) y, size, size); }
    public float getSpeed() { return speed; }
    public float getX() { return x; }
    public float getY() { return y; }
    public int getSize() { return size; }
}