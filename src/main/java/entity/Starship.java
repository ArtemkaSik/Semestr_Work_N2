package entity;

import core.GamePanel;
import handler.KeyHandler;
import util.ImageLoader;
import config.DisplayConfig;
import config.GameConfig;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Starship extends Entity {
    private GamePanel gp;
    private KeyHandler keyH;
    private ImageLoader imageLoader;
    
    // Animation parameters
    private BufferedImage[] sprites;
    private int spriteIndex = 0;
    private int animationCounter = 0;
    private static final int ANIMATION_SPEED = 8;
    
    public Starship(GamePanel gp, KeyHandler keyH, BufferedImage[] sprites) {
        this.gp = gp;
        this.keyH = keyH;
        this.sprites = sprites;
        this.sprite = sprites[0];
        
        setDefaultValues();
    }
    
    private void setDefaultValues() {
        x = DisplayConfig.SCREEN_WIDTH / 2 - 24;
        y = DisplayConfig.SCREEN_HEIGHT - 100;
        direction = "up";
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
        if (keyH != null) {
            // Update position
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
            
            // Limit movement within screen bounds
            if (x < 0) x = 0;
            if (x > gp.getWidth() - sprite.getWidth()) x = gp.getWidth() - sprite.getWidth();
            if (y < 0) y = 0;
            if (y > gp.getHeight() - sprite.getHeight()) y = gp.getHeight() - sprite.getHeight();
        }
        
        // Update animation
        updateAnimation();
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public BufferedImage getSprite() {
        return sprite;
    }
    
    public void draw(Graphics2D g2) {
        g2.drawImage(sprite, x, y, null);
    }
}
