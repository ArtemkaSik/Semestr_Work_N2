package util;

import core.GamePanel;
import entity.Bullet;
import entity.Entity;
import entity.Starship;

public class CollisionChecker {

    public CollisionChecker(){

    }

    public boolean checkBulletPlayerCollision(Bullet bullet, Starship starship) {
        int bulletX = bullet.getX();
        int bulletY = bullet.getY();
        
        int starshipX = starship.getX();
        int starshipY = starship.getY();

        return bulletX < starshipX + starship.getWidth() &&
               bulletX + bullet.getWidth() > starshipX &&
               bulletY < starshipY + starship.getHeight() &&
               bulletY + bullet.getHeight() > starshipY;
    }
}
