import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;

public class Wall {
    private float x, y; // Posición de la pared
    private int width, height; // Tamaño

    // Constructor
    public Wall(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Obtener área para colisión
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }

    // Dibujar la pared (gris por ahora)
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect((int)x, (int)y, width, height);
    }
}
