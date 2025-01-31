package core;

import config.DisplayConfig;
import entity.Starship;
import handler.KeyHandler;
import network.GameClient;
import network.GameServer;
import util.ImageLoader;

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

//    public final CollisionChecker collisionChecker;
    private GameClient gameClient;

    public GamePanel(boolean isHost, String serverIp){
        setPreferredSize(new Dimension(DisplayConfig.SCREEN_WIDTH, DisplayConfig.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);

        this.keyH = new KeyHandler();


//        this.collisionChecker = new CollisionChecker(this);
        this.starship = new Starship(keyH, isHost);
        this.enemy = new Starship(null, !isHost);

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
        starship.update();

        gameClient.sendPlayerPosition();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Отрисовка фона
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }

        // Отрисовка корабля
        starship.draw(g2);

        if (gameClient != null) {
            gameClient.getEnemy().draw(g2);
            gameClient.getEnemy().getBullets().forEach(bullet -> bullet.draw(g2));
        }

        g2.dispose();
    }
}
