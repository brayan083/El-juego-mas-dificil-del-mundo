import java.awt.Graphics2D;
import java.awt.Color;
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

    // Actualizar posición según estado de teclas
    public void update(int windowWidth, int windowHeight, int[][] tileMap, int tileSize) {
        // Calculamos la nueva posición
        float newX = x;
        float newY = y;
        
        // Movimientos individuales con la misma velocidad
        if (movingUp) {
            Rectangle checkY = new Rectangle((int)x, (int)(y - speed), size, size);
            if (!collidesWithTileMap(checkY, tileMap, tileSize)) {
                newY -= speed;
            }
        }
        
        if (movingDown) {
            Rectangle checkY = new Rectangle((int)x, (int)(y + speed), size, size);
            if (!collidesWithTileMap(checkY, tileMap, tileSize)) {
                newY += speed;
            }
        }
        
        if (movingLeft) {
            Rectangle checkX = new Rectangle((int)(x - speed), (int)y, size, size);
            if (!collidesWithTileMap(checkX, tileMap, tileSize)) {
                newX -= speed;
            }
        }
        
        if (movingRight) {
            Rectangle checkX = new Rectangle((int)(x + speed), (int)y, size, size);
            if (!collidesWithTileMap(checkX, tileMap, tileSize)) {
                newX += speed;
            }
        }
        
        // Aplicar límites de la ventana
        x = Math.max(0, Math.min(newX, windowWidth - size));
        y = Math.max(0, Math.min(newY, windowHeight - size));
    }

    private boolean collidesWithTileMap(Rectangle bounds, int[][] tileMap, int tileSize) {
        if (tileMap == null) return false;

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

    // Métodos para cambiar el estado de movimiento
    public void setMovingUp(boolean moving) {
        movingUp = moving;
    }

    public void setMovingDown(boolean moving) {
        movingDown = moving;
    }

    public void setMovingLeft(boolean moving) {
        movingLeft = moving;
    }

    public void setMovingRight(boolean moving) {
        movingRight = moving;
    }

    // Obtener área para colisiones
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, size, size);
    }

    // Dibujar el cuadrado rojo
    public void draw(Graphics2D g) {
        g.setColor(Color.RED);
        g.fillRect((int) x, (int) y, size, size);
    }

    // Getters y setters (por si necesitas ajustar posición desde Level)
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
