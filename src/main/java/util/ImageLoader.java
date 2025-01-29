package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class ImageLoader {
    public BufferedImage[] getStarshipSprites() throws IOException {
        BufferedImage[] sprites = new BufferedImage[5];
        sprites[0] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player_yellow/yellow_min.png")));
        sprites[1] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player_yellow/yellow_low.png")));
        sprites[2] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player_yellow/yellow_mid.png")));
        sprites[3] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player_yellow/yellow_high.png")));
        sprites[4] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player_yellow/yellow_max.png")));
        return sprites;
    }

    public BufferedImage getBackgroundImage() throws IOException {
        return ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/img/space_battle_bg.png")));
    }
}
