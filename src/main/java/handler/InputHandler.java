// En: src/main/java/handler/InputHandler.java
package handler;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import model.Player;

public class InputHandler extends KeyAdapter {

    // --- INICIO: Implementación de Singleton ---
    private static InputHandler instance;

    // 1. Constructor privado para evitar 'new InputHandler()'.
    private InputHandler() {}

    // 2. Método público para obtener la única instancia.
    public static synchronized InputHandler getInstance() {
        if (instance == null) {
            instance = new InputHandler();
        }
        return instance;
    }
    // --- FIN: Implementación de Singleton ---


    private Player activePlayer;

    public void setActivePlayer(Player player) {
        this.activePlayer = player;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (activePlayer == null) return;
        
        // La lógica de movimiento no cambia.
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP:    activePlayer.setMovingUp(true);    break;
            case KeyEvent.VK_DOWN:  activePlayer.setMovingDown(true);  break;
            case KeyEvent.VK_LEFT:  activePlayer.setMovingLeft(true);  break;
            case KeyEvent.VK_RIGHT: activePlayer.setMovingRight(true); break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (activePlayer == null) return;

        // La lógica de movimiento no cambia.
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP:    activePlayer.setMovingUp(false);   break;
            case KeyEvent.VK_DOWN:  activePlayer.setMovingDown(false); break;
            case KeyEvent.VK_LEFT:  activePlayer.setMovingLeft(false); break;
            case KeyEvent.VK_RIGHT: activePlayer.setMovingRight(false);break;
        }
    }
}