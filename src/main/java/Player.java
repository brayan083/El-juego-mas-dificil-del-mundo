import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;

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
    public void update(int windowWidth, int windowHeight, List<Wall> walls) {
        // Calculamos la nueva posición
        float newX = x;
        float newY = y;
        
        // Movimientos individuales con la misma velocidad
        if (movingUp) {
            Rectangle checkY = new Rectangle((int)x, (int)(y - speed), size, size);
            boolean canMoveY = true;
            for (Wall wall : walls) {
                if (checkY.intersects(wall.getBounds())) {
                    canMoveY = false;
                    break;
                }
            }
            if (canMoveY) {
                newY -= speed;
            }
        }
        
        if (movingDown) {
            Rectangle checkY = new Rectangle((int)x, (int)(y + speed), size, size);
            boolean canMoveY = true;
            for (Wall wall : walls) {
                if (checkY.intersects(wall.getBounds())) {
                    canMoveY = false;
                    break;
                }
            }
            if (canMoveY) {
                newY += speed;
            }
        }
        
        if (movingLeft) {
            Rectangle checkX = new Rectangle((int)(x - speed), (int)y, size, size);
            boolean canMoveX = true;
            for (Wall wall : walls) {
                if (checkX.intersects(wall.getBounds())) {
                    canMoveX = false;
                    break;
                }
            }
            if (canMoveX) {
                newX -= speed;
            }
        }
        
        if (movingRight) {
            Rectangle checkX = new Rectangle((int)(x + speed), (int)y, size, size);
            boolean canMoveX = true;
            for (Wall wall : walls) {
                if (checkX.intersects(wall.getBounds())) {
                    canMoveX = false;
                    break;
                }
            }
            if (canMoveX) {
                newX += speed;
            }
        }
        
        // Aplicar límites de la ventana
        x = Math.max(0, Math.min(newX, windowWidth - size));
        y = Math.max(0, Math.min(newY, windowHeight - size));
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
