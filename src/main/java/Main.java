import javax.swing.*;
import java.awt.*;

import lobby.WindowSettings;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Лобби игры");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setResizable(false);
        frame.setMinimumSize(new Dimension(300, 200));

        WindowSettings.setupMainWindow(frame);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
