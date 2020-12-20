package gameClient;

import api.game_service;

import javax.swing.*;
import java.awt.*;

/**
 * This class represent a Timer.
 */
public class panelTimer extends JPanel {

    private game_service game;

    /**
     * Default constructor.
     * @param game - game_service
     */
    public panelTimer(game_service game) {
        super();
        this.game=game;
        this.setBackground(Color.gray);
    }

    /**
     * This method paint the timer on the upper left side of the frame.
     * @param g - Graphics
     */
    public void paint(Graphics g) {
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        super.paintComponent(g);
        double timeToEnd = game.timeToEnd()/1000.0;
        this.setBounds(0, 0, 300, 30);
        g.setColor(Color.black);
        g.setFont(new Font("ARIEL", Font.BOLD, 20));
        g.drawString("Timer: " + timeToEnd, 15, 20);
    }
}
