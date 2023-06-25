package net.orange.game.display;

import net.orange.game.data.json.JsonArray;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record BlockPos(int x, int y) {
    public static final BlockPos zero = new BlockPos();
    public BlockPos(){
        this(0,0);
    }
    @Contract("_ -> new")
    public static @NotNull BlockPos from_json(@NotNull JsonArray p){
        return new BlockPos(p.getInt(0), p.getInt(1));
    }
    @Contract("_ -> new")
    public @NotNull BlockPos add(@NotNull BlockPos o){
        return new BlockPos(x+o.x,y+o.y);
    }
    @Contract("_ -> new")
    public @NotNull BlockPos sub(@NotNull BlockPos o){
        return new BlockPos(x-o.x,y-o.y);
    }
    @Contract("_ -> new")
    public @NotNull BlockPos mul(int o){
        return new BlockPos(x*o,y*o);
    }
    public int dot(@NotNull BlockPos o){
        return x*o.x+y*o.y;
    }
    public int cross(@NotNull BlockPos o){
        return x*o.y-y*o.x;
    }
    public int length2(){
        return x*x+y*y;
    }
    @Contract(" -> new")
    public @NotNull Pos toPos(){
        return new Pos(x, y);
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "("+x+", "+y+")";
    }
    @Contract(" -> new")
    public @NotNull BlockPos rotate(){
        return new BlockPos(-y,x);
    }
}
