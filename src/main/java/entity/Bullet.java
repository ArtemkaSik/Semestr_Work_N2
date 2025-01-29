package entity;

import config.GameConfig;

import java.awt.*;

public class Bullet extends Entity {
    private boolean active = true;

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        y -= GameConfig.BULLET_SPEED; // Пуля летит вверх
        if (y < 0) active = false;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.fillOval(x, y, 6, 10);
    }

    public boolean isActive() {
        return active;
    }
}
