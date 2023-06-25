package net.orange.game.combat;

import net.orange.game.data.json.JsonArray;
import net.orange.game.data.json.JsonObj;
import net.orange.game.display.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;

public class AttackArea implements Iterable<BlockPos>{
    private final HashSet<BlockPos> blocks;
    public AttackArea(){
        this.blocks = new HashSet<>();
    }

    public AttackArea(@NotNull JsonArray array) {
        this();
        for(JsonObj obj : array) {
            if (obj instanceof JsonArray pos){
                blocks.add(BlockPos.from_json(pos));
            }
        }
    }
    public AttackArea rotate(){
        AttackArea r = new AttackArea();
        r.blocks.addAll(this.blocks.stream().map(BlockPos::rotate).toList());
        return r;
    }
    public boolean contains(@NotNull BlockPos pos){
        return blocks.contains(pos);
    }

    @NotNull
    @Override
    public Iterator<BlockPos> iterator() {
        return blocks.iterator();
    }
}
