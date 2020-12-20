package gameClient;


import javax.swing.*;

public class MyFrame extends JFrame {

    MyPanel myPanel;

    public MyFrame(String s, Arena ar) {
        super(s);
        myPanel = new MyPanel(ar);
        this.add(myPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}