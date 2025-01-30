package entity;

import core.GamePanel;
import handler.KeyHandler;
import lombok.Getter;
import lombok.Setter;
import util.ImageLoader;
import config.DisplayConfig;
import config.GameConfig;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Getter
@Setter
public class Starship extends Entity {
    private GamePanel gp;
    private KeyHandler keyH;
    private ImageLoader imageLoader;
    
    // Параметры анимации
    private BufferedImage[] sprites;
    private int spriteIndex = 0;
    private int animationCounter = 0;
    private static final int ANIMATION_SPEED = 8; // Чем меньше число, тем быстрее анимация
    
    // Параметры здоровья
    private int currentHp = GameConfig.STARSHIP_HP;
    private static final int HP_BAR_WIDTH = 50;
    private static final int HP_BAR_HEIGHT = 8;
    
    public Starship(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        
        setDefaultValues();
        loadSprites();
    }

    public Starship(){}
    
    private void setDefaultValues() {
        x = DisplayConfig.SCREEN_WIDTH / 2 - 24;
        y = DisplayConfig.SCREEN_HEIGHT - 100;
        direction = "up";
        currentHp = GameConfig.STARSHIP_HP;
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
    
    public void takeDamage() {
        currentHp -= GameConfig.BULLET_DAMAGE;
        if (currentHp < 0) currentHp = 0;
    }
    
    public boolean isAlive() {
        return currentHp > 0;
    }
    
    public void update() {
        if (!isAlive()) return;
        
        // Обновление позиции
        if (keyH.isUpPressed() && keyH.isLeftPressed()){
            x -= GameConfig.STARSHIP_D_SPEED;
            y -= GameConfig.STARSHIP_D_SPEED;
        } else if (keyH.isUpPressed() && keyH.isRightPressed()){
            x += GameConfig.STARSHIP_D_SPEED;
            y -= GameConfig.STARSHIP_D_SPEED;
        } else if (keyH.isDownPressed() && keyH.isLeftPressed()){
            x -= GameConfig.STARSHIP_D_SPEED;
            y += GameConfig.STARSHIP_D_SPEED;
        } else if (keyH.isDownPressed() && keyH.isRightPressed()){
            x += GameConfig.STARSHIP_D_SPEED;
            y += GameConfig.STARSHIP_D_SPEED;
        } else if (keyH.isUpPressed()) {
            y -= GameConfig.STARSHIP_SPEED;
        } else if (keyH.isDownPressed()) {
            y += GameConfig.STARSHIP_SPEED;
        } else if (keyH.isLeftPressed()) {
            x -= GameConfig.STARSHIP_SPEED;
        } else if (keyH.isRightPressed()) {
            x += GameConfig.STARSHIP_SPEED;
        }

        // Ограничение движения в пределах экрана
        gp.collisionChecker.check(this);
        
        // Обновление анимации
        updateAnimation();
    }
    
    public void draw(Graphics2D g2) {
        if (!isAlive()) return;
        
        // Отрисовка корабля
        g2.drawImage(sprite, x, y, null);
        
        // Отрисовка полоски здоровья
        int hpBarX = x + (sprite.getWidth() - HP_BAR_WIDTH) / 2;
        int hpBarY = y - 15;
        
        // Фон полоски здоровья
        g2.setColor(Color.GRAY);
        g2.fillRect(hpBarX, hpBarY, HP_BAR_WIDTH, HP_BAR_HEIGHT);
        
        // Текущее здоровье
        float healthPercentage = (float) currentHp / GameConfig.STARSHIP_HP;
        int currentWidth = (int) (HP_BAR_WIDTH * healthPercentage);
        g2.setColor(Color.GREEN);
        g2.fillRect(hpBarX, hpBarY, currentWidth, HP_BAR_HEIGHT);
        
        // Обводка полоски здоровья
        g2.setColor(Color.WHITE);
        g2.drawRect(hpBarX, hpBarY, HP_BAR_WIDTH, HP_BAR_HEIGHT);
    }
}
