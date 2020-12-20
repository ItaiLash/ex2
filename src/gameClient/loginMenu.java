package gameClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;

/**
 * This class represent an opening menu for the game.
 * The menu allows to enter ID number and select the stage at which you want to play.
 */
public class loginMenu implements ActionListener {

    private static JLabel idLabel;
    private static JTextField idField;
    private static JLabel levelLabel;
    private static JTextField levelField;
    private static JButton lodinButton;
    private static ImageIcon logo;
    public boolean isOn = true;
    public int id;
    public int scenario;

    public void show(){
        JFrame frame = new JFrame();
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setLayout(new FlowLayout(FlowLayout.LEADING));
        //frame.setLayout(new FlowLayout(FlowLayout.CENTER));
        JPanel panel = new JPanel();
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        panel.setLayout(null);
        idLabel = new JLabel("ID:");
        idLabel.setBounds(10, 10, 80, 25);
        panel.add(idLabel);
        idField = new JTextField(20);
        idField.setBounds(100, 10, 165, 25);
        panel.add(idField);
        levelLabel = new JLabel("Level:");
        levelLabel.setBounds(10, 40, 80, 25);
        panel.add(levelLabel);
        levelField = new JTextField(20);
        levelField.setBounds(100, 40, 165, 25);
        panel.add(levelField);
        lodinButton = new JButton("login");
        lodinButton.setBounds(100, 70, 100, 40);
        lodinButton.addActionListener(new loginMenu());
        panel.add(lodinButton);
        lodinButton.addActionListener(this);
        frame.setVisible(true);
    }

    /**
     * This method override actionPerformed of ActionListener.
     * The method "listen" to the entered text.
     * @param e - ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        this.id = Integer.parseInt(idField.getText());
        this.scenario = Integer.parseInt(levelField.getText());
        isOn = false;

    }
}


