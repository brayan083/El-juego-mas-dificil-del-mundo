import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Ellipse2D;

public class Obstacle {
    private float x, y; // Posición del centro del círculo
    private int radius; // Radio del círculo
    private float speed; // Velocidad (positiva o negativa)
    private boolean isHorizontal; // True: mueve horizontal, False: mueve vertical
    private int windowWidth, windowHeight; // Límites de la ventana

    // Constructor
    public Obstacle(float x, float y, int radius, float speed, boolean isHorizontal, int windowWidth, int windowHeight) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.speed = speed; // Velocidad única (se aplica a X o Y según isHorizontal)
        this.isHorizontal = isHorizontal;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    // Actualizar posición con rebote
    public void update() {
        if (isHorizontal) {
            x += speed;
            // Rebote en bordes horizontales (izquierda y derecha)
            if (x - radius <= 0 || x + radius >= windowWidth) {
                speed = -speed; // Invertir dirección
                // Ajustar posición para evitar que se pegue
                if (x - radius < 0) x = radius;
                if (x + radius > windowWidth) x = windowWidth - radius;
            }
        } else {
            y += speed;
            // Rebote en bordes verticales (arriba y abajo)
            if (y - radius <= 0 || y + radius >= windowHeight) {
                speed = -speed; // Invertir dirección
                // Ajustar posición
                if (y - radius < 0) y = radius;
                if (y + radius > windowHeight) y = windowHeight - radius;
            }
        }
    }

    // Obtener área para colisiones
    public Ellipse2D getBounds() {
        return new Ellipse2D.Float(x - radius, y - radius, radius * 2, radius * 2);
    }

    // Dibujar el círculo azul
    public void draw(Graphics2D g) {
        g.setColor(Color.BLUE);
        g.fillOval((int)(x - radius), (int)(y - radius), radius * 2, radius * 2);
    }
}
