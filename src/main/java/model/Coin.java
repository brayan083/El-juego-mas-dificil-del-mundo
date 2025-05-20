package model;

import java.awt.geom.Ellipse2D; // Podríamos usar círculos para las monedas

public class Coin {
    private float x, y;
    private int radius; // Usaremos radio para dibujar círculos
    private boolean collected;

    public Coin(float x, float y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.collected = false; // Inicialmente no está recolectada
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getRadius() {
        return radius;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    // Hitbox para la moneda (un círculo)
    public Ellipse2D getBounds() {
        return new Ellipse2D.Float(x - radius, y - radius, radius * 2, radius * 2);
    }

}