package util;

import core.GamePanel;
import entity.Entity;
import entity.Starship;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gamePanel){
        this.gp = gamePanel;
    }

    public CollisionChecker(){

    }

    public void check(Entity entity){
        if (entity.x < 0) entity.x = 0;
        if (entity.x > gp.getWidth() - entity.sprite.getWidth()) entity.x = gp.getWidth() - entity.sprite.getWidth();
        if (entity.y < 0) entity.y = 0;
        if (entity.y > gp.getHeight() - entity.sprite.getHeight()) entity.y = gp.getHeight() - entity.sprite.getHeight();
    }
}
