import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;

public class Player {
    private float x, y; // Posición del cuadrado
    private int size;   // Tamaño (ancho y alto)
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
    public void update(int windowWidth, int windowHeight) {
        if (movingUp && y > 0) {
            y -= speed;
        }
        if (movingDown && y + size < windowHeight) {
            y += speed;
        }
        if (movingLeft && x > 0) {
            x -= speed;
        }
        if (movingRight && x + size < windowWidth) {
            x += speed;
        }
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
        return new Rectangle((int)x, (int)y, size, size);
    }

    // Dibujar el cuadrado rojo
    public void draw(Graphics2D g) {
        g.setColor(Color.RED);
        g.fillRect((int)x, (int)y, size, size);
    }

    // Getters y setters (por si necesitas ajustar posición desde Level)
    public float getX() { return x; }
    public float getY() { return y; }
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
