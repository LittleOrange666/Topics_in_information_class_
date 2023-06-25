package net.orange.game.display;

import net.orange.game.tools.RotateImage;
import net.orange.game.tools.Scaler;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RotatedPicture extends Picture {
    private final BufferedImage old_image;
    public RotatedPicture(String path) {
        super(path);
        old_image = image;
    }

    public RotatedPicture(String path, int width, int height) {
        super(path, width, height);
        old_image = image;
    }
    public void rotateTo(double angle) {
        if (isLoad_success()){
            image = RotateImage.Rotate(old_image,angle);
            this.size = new Pos(image.getWidth(),image.getHeight()).div(Scaler.scale);
            refreshAlpha();
        }
    }

    @Override
    public void paint(@NotNull Pos pos, Graphics2D g) {
        super.paint(pos.sub(getSize().mul(0.5)), g);
    }
}
