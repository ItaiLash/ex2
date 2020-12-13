package gameClient;


import javax.swing.*;

public class MyFrame2 extends JFrame {

    MyPanel myPanel;

    public MyFrame2(String s, Arena ar) {
        super(s);
        myPanel = new MyPanel(ar);
        this.add(myPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}