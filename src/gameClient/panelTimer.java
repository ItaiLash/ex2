package gameClient;

import api.game_service;

import javax.swing.*;
import java.awt.*;

public class panelTimer extends JPanel {

    private game_service game;

    public panelTimer(game_service game) {
        super();
        this.game=game;
        this.setBackground(Color.gray);
    }
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
