package core;

import javax.swing.*;

public class Game {
    private final GamePanel gamePanel;

    public Game(boolean isHost, String serverIp, String playerName) {
        this.gamePanel = new GamePanel(isHost, serverIp, playerName);
    }

    public void start() {
        gamePanel.startGameThread();
    }

    public JPanel getPanel() {
        return gamePanel;
    }
}
