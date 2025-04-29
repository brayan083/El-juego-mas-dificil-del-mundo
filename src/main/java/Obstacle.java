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
    // Actualizar posición con rebote
    public void update(List<Wall> walls) {
        // Calcular nueva posición
        float newX = x + (isHorizontal ? speed : 0);
        float newY = y + (isHorizontal ? 0 : speed);

        // Crear un área de prueba para la nueva posición
        Ellipse2D nextPos = new Ellipse2D.Float(newX - radius, newY - radius, radius * 2, radius * 2);

        // Verificar colisión con paredes
        boolean collision = false;
        for (Wall wall : walls) {
            if (nextPos.intersects(wall.getBounds())) {
                collision = true;
                break;
            }
        }

        if (isHorizontal) {
            // Verificar colisión con bordes de la ventana o paredes
            if (newX - radius <= 0 || newX + radius >= windowWidth || collision) {
                speed = -speed; // Invertir dirección
                // Ajustar posición para evitar que se pegue
                if (newX - radius <= 0) {
                    newX = radius + 1.0f; // Margen adicional
                } else if (newX + radius >= windowWidth) {
                    newX = windowWidth - radius - 1.0f;
                } else if (collision) {
                    // Ajustar posición según la dirección anterior
                    if (speed > 0) { // Iba hacia la izquierda, ahora va hacia la derecha
                        for (Wall wall : walls) {
                            if (nextPos.intersects(wall.getBounds())) {
                                newX = wall.getBounds().x + wall.getBounds().width + radius + 1.0f;
                                break;
                            }
                        }
                    } else { // Iba hacia la derecha, ahora va hacia la izquierda
                        for (Wall wall : walls) {
                            if (nextPos.intersects(wall.getBounds())) {
                                newX = wall.getBounds().x - radius - 1.0f;
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            // Verificar colisión con bordes de la ventana o paredes
            if (newY - radius <= 0 || newY + radius >= windowHeight || collision) {
                speed = -speed; // Invertir dirección
                // Ajustar posición
                if (newY - radius <= 0) {
                    newY = radius + 1.0f;
                } else if (newY + radius >= windowHeight) {
                    newY = windowHeight - radius - 1.0f;
                } else if (collision) {
                    // Ajustar posición según la dirección anterior
                    if (speed > 0) { // Iba hacia arriba, ahora va hacia abajo
                        for (Wall wall : walls) {
                            if (nextPos.intersects(wall.getBounds())) {
                                newY = wall.getBounds().y + wall.getBounds().height + radius + 1.0f;
                                break;
                            }
                        }
                    } else { // Iba hacia abajo, ahora va hacia arriba
                        for (Wall wall : walls) {
                            if (nextPos.intersects(wall.getBounds())) {
                                newY = wall.getBounds().y - radius - 1.0f;
                                break;
                            }
                        }
                    }
                }
            }
        }

        // Verificar si la nueva posición es válida (no colisiona)
        nextPos.setFrame(newX - radius, newY - radius, radius * 2, radius * 2);
        boolean validPosition = true;
        for (Wall wall : walls) {
            if (nextPos.intersects(wall.getBounds())) {
                validPosition = false;
                break;
            }
        }

        // Aplicar la nueva posición solo si es válida
        if (validPosition) {
            x = newX;
            y = newY;
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
