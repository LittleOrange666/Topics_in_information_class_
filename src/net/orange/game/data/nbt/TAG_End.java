package net.orange.game.data.nbt;

import net.orange.game.data.json.JsonNull;
import net.orange.game.data.json.JsonObj;
import org.jetbrains.annotations.NotNull;

public class TAG_End extends NBTObj{
    public TAG_End(){
    }
    @Override
    public @NotNull NBTType getType() {
        return NBTType.End;
    }

    @Override
    public @NotNull StringBuilder dump(StringBuilder builder, int level, String indent, boolean nextline) {
        return builder;
    }

    @Override
    public @NotNull JsonObj toJson() {
        return new JsonNull();
    }
}
