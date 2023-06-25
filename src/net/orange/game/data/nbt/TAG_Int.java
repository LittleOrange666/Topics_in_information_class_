package net.orange.game.data.nbt;

import net.orange.game.data.json.JsonNumber;
import net.orange.game.data.json.JsonObj;
import org.jetbrains.annotations.NotNull;

public class TAG_Int extends NBTObj{
    private int data;
    public TAG_Int(int data){
        this.data = data;
    }
    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    @Override
    public @NotNull NBTType getType() {
        return NBTType.Int;
    }

    @Override
    public @NotNull StringBuilder dump(StringBuilder builder, int level, String indent, boolean nextline) {
        return builder.append(data);
    }
    @Override
    public String toString(){
        return data + "";
    }

    @Override
    public @NotNull JsonObj toJson() {
        return new JsonNumber(data);
    }

    public TAG_Int copy(){
        return new TAG_Int(data);
    }
}
