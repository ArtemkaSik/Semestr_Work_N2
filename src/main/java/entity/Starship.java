package entity;

import handler.KeyHandler;
import handler.MoveHandler;
import handler.ShotHandler;
import util.ImageLoader;
import config.DisplayConfig;
import config.GameConfig;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;


public class Starship extends Entity {
    private KeyHandler keyH;
    private ShotHandler shotH;
    private MoveHandler moveH;
    private ImageLoader imageLoader;

    public boolean isHost;
    
    // Параметры анимации
    private BufferedImage[] sprites;
    private int spriteIndex = 0;
    private int animationCounter = 0;
    private static final int ANIMATION_SPEED = 8; // Чем меньше число, тем быстрее анимация
    
    // Параметры здоровья
    private int currentHp;
    private static final int HP_BAR_WIDTH = 50;
    private static final int HP_BAR_HEIGHT = 8;

    //Выстрелы, совершённые кораблём
    private ArrayList<Bullet> bullets = new ArrayList();

    public Starship(KeyHandler keyH, boolean isHost) {
        this.keyH = keyH;


        this.shotH = new ShotHandler(keyH);
        this.moveH = new MoveHandler(keyH);
        this.isHost = isHost;

        setDefaultValues();
        loadSprites();
    }
    
    public void setDefaultValues() {
        x = DisplayConfig.SCREEN_WIDTH / 2 - 24;
        currentHp = GameConfig.STARSHIP_HP;
        if (getIsHost()){
            y = DisplayConfig.SCREEN_HEIGHT - 120;
        } else {
            y = 20;
        }
    }
    
    private void loadSprites() {
        imageLoader = new ImageLoader();
        try {
            sprites = imageLoader.getStarshipSprites(getIsHost());
            sprite = sprites[0]; // Установка начального спрайта
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void updateAnimation() {
        animationCounter++;
        if (animationCounter >= ANIMATION_SPEED) {
            spriteIndex = (spriteIndex + 1) % sprites.length;
            sprite = sprites[spriteIndex];
            animationCounter = 0;
        }
    }
    
    public void takeDamage() {
        if (isAlive()) {
            currentHp -= GameConfig.BULLET_DAMAGE;
            if (currentHp < 0) currentHp = 0;
            System.out.println(isHost ? "Host" : "Client" + " took damage! HP: " + currentHp);
        }
    }
    
    public boolean isAlive() {
        return currentHp > 0;
    }
    
    public void update() {
        if (!isAlive()) return;

        getBullets().removeIf(bullet -> !bullet.isActive());
        for (Bullet bullet : getBullets()) {
            bullet.update(getIsHost());
        }

        moveH.handleMove(this);

        shotH.handleShooting(this);
        
        // Обновление анимации
        updateAnimation();
    }
    
    public void draw(Graphics2D g2) {
        // Отрисовка корабля
        g2.drawImage(sprite, x, y, null);
        
        // Отрисовка полоски здоровья
        drawHealthBar(g2);
        
        // Отрисовка пуль
        for (Bullet bullet : bullets) {
            if (bullet.isActive()) {
                bullet.draw(g2);
            }
        }
    }
    
    private void drawHealthBar(Graphics2D g2) {
        int healthBarX = x;
        int healthBarY = y - 15; // Размещаем полоску здоровья над кораблем
        
        // Фон полоски здоровья (красный)
        g2.setColor(Color.RED);
        g2.fillRect(healthBarX, healthBarY, HP_BAR_WIDTH, HP_BAR_HEIGHT);
        
        // Текущее здоровье (зеленый)
        g2.setColor(Color.GREEN);
        int currentHealthWidth = (int) ((currentHp / (float) GameConfig.STARSHIP_HP) * HP_BAR_WIDTH);
        g2.fillRect(healthBarX, healthBarY, currentHealthWidth, HP_BAR_HEIGHT);
        
        // Обводка полоски здоровья
        g2.setColor(Color.WHITE);
        g2.drawRect(healthBarX, healthBarY, HP_BAR_WIDTH, HP_BAR_HEIGHT);
    }

    public ArrayList<Bullet> getBullets(){
        return bullets;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void updatePlayer(int x, int y, int health) {
        this.x = x;
        this.y = y;
        this.currentHp = health;
        updateAnimation();
    }

    public boolean getIsHost(){
        return isHost;
    }

    public void setHealth(int health) {
        this.currentHp = health;
    }

    public int getWidth() {
        return sprite != null ? sprite.getWidth() : 48;
    }

    public int getHeight() {
        return sprite != null ? sprite.getHeight() : 48; // 48 - это примерная высота спрайта
    }
}
