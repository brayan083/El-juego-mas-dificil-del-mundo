// src/main/java/view/GamePanel.java
package view;

import controller.GameController; // Importará el nuevo controlador
import handler.InputHandler;
import model.GameModel;
import model.Config;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GamePanel extends JPanel {

    private final GameView gameView; // La vista que sabe dibujar componentes
    private final GameController controller; // Referencia al controlador

    public GamePanel(GameController controller) {
        this.controller = controller;
        this.gameView = new GameView();

        this.setPreferredSize(new Dimension(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT));

        setFocusable(true);
        // Ahora obtenemos el KeyListener directamente del Singleton.
        addKeyListener(InputHandler.getInstance());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. El panel le pide el modelo al controlador.
        GameModel model = controller.getGameModel();

        if (model != null) {
            // 2. El panel le pasa el modelo a la vista para que lo dibuje.
            gameView.renderGame(g2d, model);
        }
    }
}