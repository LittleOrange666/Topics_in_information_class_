package net.orange.game.tools;

import net.orange.game.Main;
import net.orange.game.display.Pos;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Scaler {
    public static double dw;
    public static double dh;
    public static Pos d;
    public static double scale;
    public static void init(double width, double height ){
        scale = Math.min(width/Main.default_width, height/Main.default_height);
        dw = (width - Main.default_width*scale)/2;
        dh = (height - Main.default_height*scale)/2;
        d = new Pos(dw, dh);
    }
    @Contract("_ -> new")
    public static @NotNull Pos scale(@NotNull Pos pos) {
        return new Pos(dw+pos.x()*scale, dh+pos.y()*scale);
    }
    @Contract("_ -> new")
    public static @NotNull Pos unscale(@NotNull Pos pos) {
        return new Pos((pos.x()-dw)/scale, (pos.y()-dh)/scale);
    }
    public static @NotNull Pos scaleSize(@NotNull Pos pos){
        return pos.mul(scale);
    }
    public static @NotNull Pos unscaleSize(@NotNull Pos pos){
        return pos.div(scale);
    }
}
