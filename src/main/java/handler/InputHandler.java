package handler;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import model.Player;

public class InputHandler extends KeyAdapter { // Extendemos KeyAdapter para no tener que implementar todos los métodos de KeyListener
    private Player activePlayer;

    /**
     * Establece el jugador activo que será controlado por este manejador de entradas.
     * Si se pasa null, se desactivará el control.
     * @param player El jugador a controlar, o null para desactivar.
     */
    public void setActivePlayer(Player player) {
        this.activePlayer = player;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (activePlayer == null) {
            return; // No hacer nada si no hay un jugador activo
        }

        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_UP:
                activePlayer.setMovingUp(true);
                break;
            case KeyEvent.VK_DOWN:
                activePlayer.setMovingDown(true);
                break;
            case KeyEvent.VK_LEFT:
                activePlayer.setMovingLeft(true);
                break;
            case KeyEvent.VK_RIGHT:
                activePlayer.setMovingRight(true);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (activePlayer == null) {
            return; // No hacer nada si no hay un jugador activo
        }

        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_UP:
                activePlayer.setMovingUp(false);
                break;
            case KeyEvent.VK_DOWN:
                activePlayer.setMovingDown(false);
                break;
            case KeyEvent.VK_LEFT:
                activePlayer.setMovingLeft(false);
                break;
            case KeyEvent.VK_RIGHT:
                activePlayer.setMovingRight(false);
                break;
        }
    }
}