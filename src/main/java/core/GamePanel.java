package core;

import config.DisplayConfig;
import config.GameConfig;
import entity.Bullet;
import entity.Starship;
import handler.KeyHandler;
import network.NetworkManager;
import network.packet.PositionPacket;
import network.packet.ShootPacket;
import network.packet.Packet;
import util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {

    private Thread gameThread;
    private KeyHandler keyH = new KeyHandler();
    private Starship playerStarship;
    private Starship enemyStarship;
    private List<Bullet> playerBullets = new ArrayList<>();
    private List<Bullet> enemyBullets = new ArrayList<>();
    private long lastShotTime = 0;
    private static final long SHOT_COOLDOWN = 500;
    private BufferedImage backgroundImage;

    private NetworkManager networkManager;
    private boolean isHost;
    private boolean gameStarted = false;

    public GamePanel(boolean isHost, String remoteAddress){
        this.isHost = isHost;
        setPreferredSize(new Dimension(DisplayConfig.SCREEN_WIDTH, DisplayConfig.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        addKeyListener(keyH);
        setFocusable(true);

        try {
            ImageLoader imageLoader = new ImageLoader();
            backgroundImage = imageLoader.getBackgroundImage();

            // Create starships with appropriate sprites
            if (isHost) {
                playerStarship = new Starship(this, keyH, imageLoader.getStarshipSprites());
                enemyStarship = new Starship(this, null, imageLoader.getRedStarshipSprites());
            } else {
                playerStarship = new Starship(this, keyH, imageLoader.getRedStarshipSprites());
                enemyStarship = new Starship(this, null, imageLoader.getStarshipSprites());
            }

            // Initialize network manager
            networkManager = new NetworkManager(isHost);
            networkManager.start(remoteAddress);

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
                if (!gameStarted && networkManager.isConnected()) {
                    gameStarted = true;
                }

                if (gameStarted) {
                    update();
                    handleNetworkPackets();
                }
                repaint();
                delta--;
            }
        }
    }

    private void handleNetworkPackets() {
        Packet packet;
        while ((packet = networkManager.receivePacket()) != null) {
            if (packet instanceof PositionPacket) {
                PositionPacket posPacket = (PositionPacket) packet;
                enemyStarship.setPosition(posPacket.getX(), posPacket.getY());
            } else if (packet instanceof ShootPacket) {
                ShootPacket shootPacket = (ShootPacket) packet;
                enemyBullets.add(new Bullet(shootPacket.getX(), shootPacket.getY()));
            }
        }
    }

    public void update(){
        if (!gameStarted) return;

        playerStarship.update();
        networkManager.sendPacket(new PositionPacket(playerStarship.getX(), playerStarship.getY()));

        // Update bullets
        updateBullets(playerBullets);
        updateBullets(enemyBullets);

        // Handle shooting
        if (keyH.isSpacePressed()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShotTime >= SHOT_COOLDOWN) {
                int bulletX = playerStarship.getX() + playerStarship.getSprite().getWidth() / 2 - 3;
                int bulletY = playerStarship.getY();

                playerBullets.add(new Bullet(bulletX, bulletY));
                networkManager.sendPacket(new ShootPacket(bulletX, bulletY));

                lastShotTime = currentTime;
            }
        }
    }

    private void updateBullets(List<Bullet> bullets) {
        bullets.removeIf(bullet -> !bullet.isActive());
        for (Bullet bullet : bullets) {
            bullet.update();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw background
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }

        if (!gameStarted) {
            // Draw waiting message
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            String message = isHost ? "Waiting for player to connect..." : "Waiting for host...";
            int messageWidth = g2.getFontMetrics().stringWidth(message);
            g2.drawString(message, getWidth()/2 - messageWidth/2, getHeight()/2);
        } else {
            // Draw starships
            playerStarship.draw(g2);
            enemyStarship.draw(g2);

            // Draw bullets
            for (Bullet bullet : playerBullets) {
                bullet.draw(g2);
            }
            for (Bullet bullet : enemyBullets) {
                bullet.draw(g2);
            }
        }

        g2.dispose();
    }

    public void cleanup() {
        if (networkManager != null) {
            networkManager.stop();
        }
    }
}
