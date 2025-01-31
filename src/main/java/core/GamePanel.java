package core;

import config.DisplayConfig;
import entity.Starship;
import handler.KeyHandler;
import network.GameClient;
import network.GameServer;
import util.ImageLoader;
import util.SoundPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class GamePanel extends JPanel implements Runnable{

    private Thread gameThread;

    private KeyHandler keyH;
    private Starship starship;
    private Starship enemy;

    private BufferedImage backgroundImage;
    private final SoundPlayer soundPlayer;

    private GameClient gameClient;
    private boolean gameStarted;
    private String waitingMessage;
    private String playerName;
    private boolean gameOver;
    private String winnerMessage;

    public GamePanel(boolean isHost, String serverIp, String playerName){
        setPreferredSize(new Dimension(DisplayConfig.SCREEN_WIDTH, DisplayConfig.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);

        this.keyH = new KeyHandler();
        this.soundPlayer = new SoundPlayer();

        this.playerName = playerName;
        this.gameOver = false;

        this.starship = new Starship(keyH, isHost);
        this.enemy = new Starship(null, !isHost);
        this.gameStarted = false;
        this.waitingMessage = isHost ? "Waiting for player to connect..." : "Waiting for server...";

        addKeyListener(keyH);
        setFocusable(true);

        try {
            if (isHost) {
                GameServer server = new GameServer();
                new Thread(server).start();
            }
            gameClient = new GameClient(serverIp, starship, enemy);
            new Thread(gameClient).start();
            ImageLoader imageLoader = new ImageLoader();
            backgroundImage = imageLoader.getBackgroundImage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        soundPlayer.playMusic();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000D / DisplayConfig.FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        long currentTime;

        while(gameThread != null){
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;

            lastTime = currentTime;

            if(delta >= 1){
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update(){
        if (!gameStarted && gameClient.areBothPlayersConnected()) {
            gameStarted = true;
            waitingMessage = null;
        }

        if (gameStarted && gameClient.areBothPlayersConnected() && !gameOver) {
            starship.update();
            gameClient.sendPlayerPosition();

            // Проверка окончания игры
            if (!starship.isAlive() || !enemy.isAlive()) {
                gameOver = true;
                if (!starship.isAlive()) {
                    winnerMessage = enemy.getIsHost() ? "Победил: Игрок 1" : "Победил: " + playerName;
                } else {
                    winnerMessage = starship.getIsHost() ? "Победил: " + playerName : "Победил: Игрок 1";
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Отрисовка фона
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }

        // Отрисовка сообщения ожидания
        if (waitingMessage != null) {
            drawCenteredMessage(g2, waitingMessage, 30);
        }

        if (gameStarted) {
            starship.draw(g2);
            enemy.draw(g2);

            // Отрисовка сообщения о победителе
            if (gameOver && winnerMessage != null) {
                drawGameOverMessage(g2);
            }
        }

        g2.dispose();
    }

    private void drawCenteredMessage(Graphics2D g2, String message, int fontSize) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, fontSize));
        FontMetrics fm = g2.getFontMetrics();
        int messageWidth = fm.stringWidth(message);
        int x = (getWidth() - messageWidth) / 2;
        int y = getHeight() / 2;
        g2.drawString(message, x, y);
    }

    private void drawGameOverMessage(Graphics2D g2) {
        // Создаем полупрозрачный фон
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Рисуем заголовок "GAME OVER"
        drawCenteredMessage(g2, "GAME END", 50);

        g2.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics fm = g2.getFontMetrics();
        int messageWidth = fm.stringWidth(winnerMessage);
        int x = (getWidth() - messageWidth) / 2;
        int y = getHeight() / 2 + 50;
        g2.drawString(winnerMessage, x, y);
    }
}
