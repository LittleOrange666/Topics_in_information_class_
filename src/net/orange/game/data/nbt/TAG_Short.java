package net.orange.game.data.nbt;

import net.orange.game.data.json.JsonNumber;
import net.orange.game.data.json.JsonObj;
import org.jetbrains.annotations.NotNull;

public class TAG_Short extends NBTObj{
    private short data;
    public TAG_Short(short data){
        this.data = data;
    }
    public short getData() {
        return data;
    }

    public void setData(short data) {
        this.data = data;
    }

    @Override
    public @NotNull NBTType getType() {
        return NBTType.Short;
    }

    @Override
    public @NotNull StringBuilder dump(StringBuilder builder, int level, String indent, boolean nextline) {
        return builder.append(data).append("s");
    }
    @Override
    public String toString(){
        return data + "s";
    }

    @Override
    public @NotNull JsonObj toJson() {
        return new JsonNumber(data);
    }

    public TAG_Short copy(){
        return new TAG_Short(data);
    }
}
