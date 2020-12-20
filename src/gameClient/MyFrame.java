package gameClient;


import javax.swing.*;

/**
 * This class represent a GUI of the Pokemon game.
 */
public class MyFrame extends JFrame {

    MyPanel myPanel;

    /**
     * Default constructor.
     * @param s - title
     * @param ar - Arena
     */
    public MyFrame(String s, Arena ar) {
        super(s);
        myPanel = new MyPanel(ar);
        this.add(myPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}