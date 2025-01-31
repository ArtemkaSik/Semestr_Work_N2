package entity;

import config.DisplayConfig;
import config.GameConfig;

import java.awt.*;

public class Bullet extends Entity {
    private boolean active = true;
    
    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void update(boolean isHost) {
        if (isHost){
            y -= GameConfig.BULLET_SPEED;
        } else {
            y += GameConfig.BULLET_SPEED;
        }
        if (y < 0 || y > DisplayConfig.SCREEN_HEIGHT) notActive();
    }
    
    public void draw(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.fillOval(x, y, 6, 10);
    }
    
    public boolean isActive() {
        return active;
    }

    public void notActive() {
        active = false;
    }
}
