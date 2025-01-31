package handler;

import config.DisplayConfig;
import config.GameConfig;
import entity.Starship;

public class MoveHandler {

    private KeyHandler keyH;

    public MoveHandler(KeyHandler keyH) {
        this.keyH = keyH;
    }

    public void handleMove(Starship starship){
        // Обновление позиции
        if (keyH.isUpPressed() && keyH.isLeftPressed()){
            starship.x -= GameConfig.STARSHIP_D_SPEED;
            starship.y -= GameConfig.STARSHIP_D_SPEED;
        } else if (keyH.isUpPressed() && keyH.isRightPressed()){
            starship.x += GameConfig.STARSHIP_D_SPEED;
            starship.y -= GameConfig.STARSHIP_D_SPEED;
        } else if (keyH.isDownPressed() && keyH.isLeftPressed()){
            starship.x -= GameConfig.STARSHIP_D_SPEED;
            starship.y += GameConfig.STARSHIP_D_SPEED;
        } else if (keyH.isDownPressed() && keyH.isRightPressed()){
            starship.x += GameConfig.STARSHIP_D_SPEED;
            starship.y += GameConfig.STARSHIP_D_SPEED;
        } else if (keyH.isUpPressed()) {
            starship.y -= GameConfig.STARSHIP_SPEED;
        } else if (keyH.isDownPressed()) {
            starship.y += GameConfig.STARSHIP_SPEED;
        } else if (keyH.isLeftPressed()) {
            starship.x -= GameConfig.STARSHIP_SPEED;
        } else if (keyH.isRightPressed()) {
            starship.x += GameConfig.STARSHIP_SPEED;
        }

        // Ограничение движения в пределах экрана
        inTheBox(starship);
        onSide(starship);
    }

    private void inTheBox(Starship starship){
        if (starship.getX() < 0) starship.setX(0);
        if (starship.getX() > DisplayConfig.SCREEN_WIDTH - starship.sprite.getWidth()) starship.setX(DisplayConfig.SCREEN_WIDTH - starship.sprite.getWidth());
        if (starship.getY() < 0) starship.setY(0);
        if (starship.getY() > DisplayConfig.SCREEN_HEIGHT - starship.sprite.getHeight()) starship.setY(DisplayConfig.SCREEN_HEIGHT - starship.sprite.getHeight());
    }

    private void onSide(Starship starship){
        if(starship.getIsHost()){
            if (starship.getY() <= DisplayConfig.SCREEN_HEIGHT/2) starship.setY(DisplayConfig.SCREEN_HEIGHT/2);
        } else {
            if (starship.getY() >= DisplayConfig.SCREEN_HEIGHT/2) starship.setY(DisplayConfig.SCREEN_HEIGHT/2);
        }
    }
}
