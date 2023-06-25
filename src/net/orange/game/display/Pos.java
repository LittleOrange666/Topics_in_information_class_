package net.orange.game.display;

import net.orange.game.data.json.JsonArray;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public record Pos(double x, double y) {
    public static final Pos zero = new Pos();
    public Pos(){
        this(0,0);
    }
    public Pos(@NotNull Point p){
        this(p.getX(), p.getY());
    }
    @Contract("_ -> new")
    public static @NotNull Pos from_json(@NotNull JsonArray p){
        return new Pos(p.getDouble(0), p.getDouble(1));
    }
    @Contract("_ -> new")
    public @NotNull Pos add(@NotNull Pos o){
        return new Pos(x+o.x,y+o.y);
    }
    public @NotNull Pos add(double x, double y){
        return new Pos(this.x+x,this.y+y);
    }
    @Contract("_ -> new")
    public @NotNull Pos sub(@NotNull Pos o){
        return new Pos(x-o.x,y-o.y);
    }
    public @NotNull Pos sub(double x, double y){
        return new Pos(this.x-x,this.y-y);
    }
    @Contract("_ -> new")
    public @NotNull Pos mul(double o){
        return new Pos(x*o,y*o);
    }
    @Contract("_ -> new")
    public @NotNull Pos div(double o){
        return new Pos(x/o,y/o);
    }
    @Contract("_ -> new")
    public @NotNull Pos rotate(double angle){
        double radians = Math.toRadians(angle);
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        return new Pos(x*cos-y*sin,y*cos+x*sin);
    }
    public double dot(@NotNull Pos o){
        return x*o.x+y*o.y;
    }
    public double cross(@NotNull Pos o){
        return x*o.y-y*o.x;
    }
    public double length(){
        return Math.sqrt(x*x+y*y);
    }
    public double distance(@NotNull Pos o){
        return sub(o).length();
    }
    public double distance(double x, double y){
        return sub(x,y).length();
    }
    @Contract(" -> new")
    public @NotNull Pos unit(){
        if (x == 0 && y == 0) return this;
        double l = length();
        return new Pos(x/l,y/l);
    }
    @Contract(" -> new")
    public @NotNull BlockPos toBlockPos(){
        return new BlockPos((int) x, (int) y);
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "("+x+", "+y+")";
    }
}
