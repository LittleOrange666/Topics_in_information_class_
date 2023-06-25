package net.orange.game.combat;

import net.orange.game.data.json.JsonObject;
import net.orange.game.display.BlockPos;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record EnemyAction(ActionType type, BlockPos pos, double time) {
    @Contract("_ -> new")
    public static @NotNull EnemyAction move(BlockPos pos){
        return new EnemyAction(ActionType.move, pos,0);
    }
    @Contract("_ -> new")
    public static @NotNull EnemyAction wait_for_second(double time){
        return new EnemyAction(ActionType.wait_for_second, BlockPos.zero,time);
    }
    @Contract("_ -> new")
    public static @NotNull EnemyAction wait_for_playtime(double time){
        return new EnemyAction(ActionType.wait_for_playtime, BlockPos.zero,time);
    }
    @Contract("_ -> new")
    public static @NotNull EnemyAction wait_for_appeartime(double time){
        return new EnemyAction(ActionType.wait_for_appeartime, BlockPos.zero,time);
    }
    @Contract("_ -> new")
    public static @NotNull EnemyAction appear_at_pos(BlockPos pos){
        return new EnemyAction(ActionType.appear_at_pos, pos,0);
    }
    @Contract(" -> new")
    public static @NotNull EnemyAction disappear(){
        return new EnemyAction(ActionType.disappear, BlockPos.zero,0);
    }
    @Contract(" -> new")
    public static @NotNull EnemyAction noaction(){
        return new EnemyAction(ActionType.noaction, BlockPos.zero,0);
    }
    public static @NotNull EnemyAction from_json(@NotNull JsonObject data){
        switch (data.getString("type")) {
            case "move" -> {
                return move(BlockPos.from_json(data.getArray("value")));
            }
            case "wait_for_second" -> {
                return wait_for_second(data.getDouble("value"));
            }
            case "wait_for_playtime" -> {
                return wait_for_playtime(data.getDouble("value"));
            }
            case "wait_for_appeartime" -> {
                return wait_for_appeartime(data.getDouble("value"));
            }
            case "appear_at_pos" -> {
                return appear_at_pos(BlockPos.from_json(data.getArray("value")));
            }
            case "disappear" -> {
                return disappear();
            }
            default -> {
                return noaction();
            }
        }
    }
}
