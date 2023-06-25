package net.orange.game.data.nbt;

import net.orange.game.data.json.JsonNumber;
import net.orange.game.data.json.JsonObj;
import org.jetbrains.annotations.NotNull;

public class TAG_Byte extends NBTObj{
    private byte data;
    public TAG_Byte(byte data){
        this.data = data;
    }
    public byte getData() {
        return data;
    }

    public void setData(byte data) {
        this.data = data;
    }
    @Override
    public @NotNull NBTType getType() {
        return NBTType.Byte;
    }

    @Override
    public @NotNull StringBuilder dump(StringBuilder builder, int level, String indent, boolean nextline) {
        return builder.append(data).append("b");
    }

    @Override
    public @NotNull JsonObj toJson() {
        return new JsonNumber(data);
    }

    @Override
    public String toString(){
        return data + "b";
    }

    public TAG_Byte copy(){
        return new TAG_Byte(data);
    }
}
