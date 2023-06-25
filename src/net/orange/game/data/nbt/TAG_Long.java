package net.orange.game.data.nbt;

import net.orange.game.data.json.JsonNumber;
import net.orange.game.data.json.JsonObj;
import org.jetbrains.annotations.NotNull;

public class TAG_Long extends NBTObj{
    private long data;
    public TAG_Long(long data){
        this.data = data;
    }
    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    @Override
    public @NotNull NBTType getType() {
        return NBTType.Long;
    }

    @Override
    public @NotNull StringBuilder dump(StringBuilder builder, int level, String indent, boolean nextline) {
        return builder.append(data).append("L");
    }
    @Override
    public String toString(){
        return data + "L";
    }

    @Override
    public @NotNull JsonObj toJson() {
        return new JsonNumber(data);
    }

    public TAG_Long copy(){
        return new TAG_Long(data);
    }
}
