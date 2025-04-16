import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.List;

public class Obstacle {
    private float x, y; // Posición del centro del círculo
    private int radius; // Radio del círculo
    private float speed; // Velocidad (positiva o negativa)
    private boolean isHorizontal; // True: mueve horizontal, False: mueve vertical
    private int windowWidth, windowHeight; // Límites de la ventana

    // Constructor
    public Obstacle(float x, float y, int radius, float speed, boolean isHorizontal, int windowWidth,
            int windowHeight) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.speed = speed; // Velocidad única (se aplica a X o Y según isHorizontal)
        this.isHorizontal = isHorizontal;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    // Actualizar posición con rebote
    public void update(List<Wall> walls) {
        // Calcular nueva posición
        float newX = x;
        float newY = y;

        if (isHorizontal) {
            newX += speed;
            // Crear un área de prueba para la nueva posición
            Ellipse2D nextPos = new Ellipse2D.Float(newX - radius, y - radius, radius * 2, radius * 2);

            // Verificar colisión con paredes
            boolean collision = false;
            for (Wall wall : walls) {
                if (nextPos.intersects(wall.getBounds())) {
                    collision = true;
                    break;
                }
            }

            // Verificar colisión con bordes de la ventana o paredes
            if (newX - radius <= 0 || newX + radius >= windowWidth || collision) {
                speed = -speed; // Invertir dirección
                // Ajustar posición para evitar que se pegue
                if (newX - radius < 0)
                    newX = radius;
                if (newX + radius > windowWidth)
                    newX = windowWidth - radius;
            } else {
                x = newX; // Actualizar posición si no hay colisión
            }
        } else {
            newY += speed;
            // Crear un área de prueba para la nueva posición
            Ellipse2D nextPos = new Ellipse2D.Float(x - radius, newY - radius, radius * 2, radius * 2);

            // Verificar colisión con paredes
            boolean collision = false;
            for (Wall wall : walls) {
                if (nextPos.intersects(wall.getBounds())) {
                    collision = true;
                    break;
                }
            }

            // Verificar colisión con bordes de la ventana o paredes
            if (newY - radius <= 0 || newY + radius >= windowHeight || collision) {
                speed = -speed; // Invertir dirección
                // Ajustar posición
                if (newY - radius < 0)
                    newY = radius;
                if (newY + radius > windowHeight)
                    newY = windowHeight - radius;
            } else {
                y = newY; // Actualizar posición si no hay colisión
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
        g.fillOval((int) (x - radius), (int) (y - radius), radius * 2, radius * 2);
    }
}
