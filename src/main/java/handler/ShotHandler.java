package handler;

import entity.Bullet;
import entity.Starship;

import static config.GameConfig.SHOT_COOLDOWN;

public class ShotHandler {

    private final KeyHandler keyH;
    private long lastShotTime = 0;

    public ShotHandler(KeyHandler keyH) {
        this.keyH = keyH;
    }

    public void handleShooting(Starship starship) {
        if (keyH.isSpacePressed()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShotTime >= SHOT_COOLDOWN) {
                int bulletY = starship.isHost ? starship.y : starship.y + starship.sprite.getHeight();
                starship.getBullets().add(new Bullet(
                        starship.x + starship.sprite.getWidth() / 2 - 3,
                        bulletY
                ));
                lastShotTime = currentTime;
            }
        }
    }
}
