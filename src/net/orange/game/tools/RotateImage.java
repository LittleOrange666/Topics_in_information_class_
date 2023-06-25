package net.orange.game.tools;

import net.orange.game.Main;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RotateImage {
    public static @NotNull BufferedImage Rotate(@NotNull Image src, double angel) {
        int src_width = src.getWidth(null);
        int src_height = src.getHeight(null);
        Rectangle rect_des = CalcRotatedSize(new Rectangle(new Dimension(
                src_width, src_height)), angel);
        BufferedImage res;
        res = new BufferedImage(rect_des.width, rect_des.height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = res.createGraphics();
        g2.translate((rect_des.width - src_width) / 2,
                (rect_des.height - src_height) / 2);
        g2.rotate(Math.toRadians(angel), (double) src_width / 2, (double) src_height / 2);

        g2.drawImage(src, null, null);
        return res;
    }
    @Contract("_, _ -> new")
    public static @NotNull Rectangle CalcRotatedSize(Rectangle src, double angel) {
        /*
        // 如果旋转的角度大于90度做相应的转换
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int w = src.width;
                int h = src.height;
                src.height = w;
                src.width = h;
            }
            angel = angel % 90;
        }
        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angel_dalta_width = Math.atan((double) src.height / src.width);
        double angel_dalta_height = Math.atan((double) src.width / src.height);

        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_width));
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_height));
        int des_width = src.width + len_dalta_width * 2;
        int des_height = src.height + len_dalta_height * 2;
         */
        double radians = Math.toRadians(angel);
        double cos = Math.abs(Math.cos(radians));
        double sin = Math.abs(Math.sin(radians));
        int des_width = (int) (src.width* cos + src.height*sin);
        int des_height = (int) (src.height*cos+src.width*sin);
        return new Rectangle(new Dimension(des_width, des_height));
    }
}
