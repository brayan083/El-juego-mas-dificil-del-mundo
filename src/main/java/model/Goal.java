package model;
import java.awt.Rectangle;

public class Goal {
    private float x, y; // Posición de la meta
    private int width, height; // Tamaño del rectángulo

    // Constructor
    public Goal(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Obtener área para colisión
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }

}
