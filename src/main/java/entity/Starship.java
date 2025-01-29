package entity;

import core.GamePanel;
import handler.KeyHandler;
import util.ImageLoader;
import config.DisplayConfig;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Starship extends Entity {
    private GamePanel gp;
    private KeyHandler keyH;
    private ImageLoader imageLoader;
    
    // Параметры анимации
    private BufferedImage[] sprites;
    private int spriteIndex = 0;
    private int animationCounter = 0;
    private static final int ANIMATION_SPEED = 8; // Чем меньше число, тем быстрее анимация
    
    public Starship(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        
        setDefaultValues();
        loadSprites();
    }
    
    private void setDefaultValues() {
        x = DisplayConfig.SCREEN_WIDTH / 2 - 24;
        y = DisplayConfig.SCREEN_HEIGHT - 100;
        speed = 4;
        direction = "up";
    }
    
    private void loadSprites() {
        imageLoader = new ImageLoader();
        try {
            sprites = imageLoader.getStarshipSprites();
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
    
    public void update() {
        // Обновление позиции
        if (keyH.isUpPressed()) {
            y -= speed;
        } else if (keyH.isDownPressed()) {
            y += speed;
        } else if (keyH.isLeftPressed()) {
            x -= speed;
        } else if (keyH.isRightPressed()) {
            x += speed;
        }
        
        // Ограничение движения в пределах экрана
        if (x < 0) x = 0;
        if (x > gp.getWidth() - sprite.getWidth()) x = gp.getWidth() - sprite.getWidth();
        if (y < 0) y = 0;
        if (y > gp.getHeight() - sprite.getHeight()) y = gp.getHeight() - sprite.getHeight();
        
        // Обновление анимации
        updateAnimation();
    }
    
    public void draw(Graphics2D g2) {
        g2.drawImage(sprite, x, y, null);
    }
}
