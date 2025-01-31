package util;

import core.GamePanel;
import entity.Bullet;
import entity.Entity;
import entity.Starship;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gamePanel){
        this.gp = gamePanel;
    }

    public CollisionChecker(){

    }

    public boolean checkBulletPlayerCollision(Bullet bullet, Starship starship) {
        // Get the coordinates and dimensions of the bullet and starship
        int bulletX = bullet.getX();
        int bulletY = bullet.getY();
        int starshipX = starship.getX();
        int starshipY = starship.getY();


        // Check if bullet coordinates are within the starship's bounds
        return bulletX >= starshipX && 
               bulletX <= starshipX &&
               bulletY >= starshipY && 
               bulletY <= starshipY;
    }
}
