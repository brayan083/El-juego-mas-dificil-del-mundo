import java.awt.Graphics2D;
import java.awt.Color;
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

    // Dibujar la meta verde
    public void draw(Graphics2D g) {
        g.setColor(new Color(165,255, 163));
        g.fillRect((int)x, (int)y, width, height);
    }
}
