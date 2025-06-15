package game.model;

public class Player {
    private float x, y; // Posición del cuadrado
    private final int size; // Tamaño (ancho y alto)
    private final float speed; // Velocidad de movimiento (píxeles por actualización)
    private boolean movingUp, movingDown, movingLeft, movingRight;

    // Constructor
    public Player(float x, float y, int size, float speed) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
    }

    public void update(Level level) {
        float dx = 0;
        float dy = 0;

        // Calcula el desplazamiento deseado
        if (movingUp) dy -= speed;
        if (movingDown) dy += speed;
        if (movingLeft) dx -= speed;
        if (movingRight) dx += speed;

        // Comprueba y aplica el movimiento en el eje X
        if (dx != 0 && !level.isCollidingWithWall(x + dx, y, size, size)) {
            x += dx;
        }
        
        // Comprueba y aplica el movimiento en el eje Y
        if (dy != 0 && !level.isCollidingWithWall(x, y + dy, size, size)) {
            y += dy;
        }

        // Aplicar límites de la ventana (esto ya estaba bien)
        x = Math.max(0, Math.min(x, level.getWindowWidth() - size));
        y = Math.max(0, Math.min(y, level.getWindowHeight() - Config.HEADER_HEIGHT - size));
    }

    // Getters y Setters (sin cambios)
    public void setMovingUp(boolean moving) { movingUp = moving; }
    public void setMovingDown(boolean moving) { movingDown = moving; }
    public void setMovingLeft(boolean moving) { movingLeft = moving; }
    public void setMovingRight(boolean moving) { movingRight = moving; }
    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    public float getSpeed() { return speed; }
    public float getX() { return x; }
    public float getY() { return y; }
    public int getSize() { return size; }
}