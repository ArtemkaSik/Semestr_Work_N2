package core;

import config.DisplayConfig;
import config.GameConfig;
import entity.Bullet;
import entity.Starship;
import handler.KeyHandler;
import util.CollisionChecker;
import util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable{

    private Thread gameThread;
    private KeyHandler keyH = new KeyHandler();
    private Starship starship =  new Starship(this, keyH);
    private List<Bullet> bullets = new ArrayList<>();
    private long lastShotTime = 0;
    private static final long SHOT_COOLDOWN = 500;
    private BufferedImage backgroundImage;
    public CollisionChecker collisionChecker = new CollisionChecker(this);

    public GamePanel(){
        setPreferredSize(new Dimension(DisplayConfig.SCREEN_WIDTH, DisplayConfig.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        addKeyListener(keyH);
        setFocusable(true);

        try {
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
        if (!starship.isAlive()) return;
        
        starship.update();
        
        // Обновление пуль
        bullets.removeIf(bullet -> !bullet.isActive());
        for (Bullet bullet : bullets) {
            bullet.update();
        }
        
        // Стрельба
        if (keyH.isSpacePressed()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShotTime >= SHOT_COOLDOWN) {
                bullets.add(new Bullet(
                    starship.x + starship.sprite.getWidth() / 2 - 3,
                    starship.y
                ));
                lastShotTime = currentTime;
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
        
        // Отрисовка корабля
        starship.draw(g2);
        
        // Отрисовка пуль
        for (Bullet bullet : bullets) {
            bullet.draw(g2);
        }
        
        g2.dispose();
    }
}
