import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("World's Hardest Game");
        Game game = new Game();


        game.setBackground(Color.WHITE); // Color de fondo del área de juego
        game.setPreferredSize(new Dimension(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT)); // Dimensiones del área de juego
        frame.add(game);
        frame.pack(); // Ajusta el tamaño del JFrame para que coincida con el JPanel

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Centrar la ventana en la pantalla
        frame.setVisible(true);

        // Bucle del juego
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                game.update();
                game.repaint();
            }
        }, 0, 1000 / 60); // 60 FPS
    }
}