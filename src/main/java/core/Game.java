package core;

import javax.swing.*;

public class Game {
    private final GamePanel gamePanel;

    public Game(boolean isHost, String serverIp) {
        this.gamePanel = new GamePanel(isHost, serverIp);
    }

    public void start() {
        gamePanel.startGameThread();
    }

    public JPanel getPanel() {
        return gamePanel;
    }
}
