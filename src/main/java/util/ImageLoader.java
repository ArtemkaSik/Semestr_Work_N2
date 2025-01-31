package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class ImageLoader {
    public BufferedImage[] getStarshipSprites(boolean isHost) throws IOException {
        BufferedImage[] sprites = new BufferedImage[5];
        String color;
        if (isHost){
            color = "yellow";
        } else {
            color = "red";
        }
        sprites[0] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player_" + color + "/" + color +"_min.png")));
        sprites[1] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player_" + color + "/" + color +"_low.png")));
        sprites[2] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player_" + color + "/" + color +"_mid.png")));
        sprites[3] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player_" + color + "/" + color +"_high.png")));
        sprites[4] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player_" + color + "/" + color +"_max.png")));
        return sprites;
    }

    public BufferedImage getBackgroundImage() throws IOException {
        return ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/img/space_battle_bg.png")));
    }
}
