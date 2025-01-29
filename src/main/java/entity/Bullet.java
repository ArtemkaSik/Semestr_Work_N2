package entity;

import java.awt.*;

public class Bullet extends Entity {
    private boolean active = true;
    private int damage;
    
    public Bullet(int x, int y, int speed, int damage) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.damage = damage;
    }
    
    public void update() {
        y -= speed; // Пуля летит вверх
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
