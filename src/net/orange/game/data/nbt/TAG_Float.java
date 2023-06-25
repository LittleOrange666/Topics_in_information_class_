package net.orange.game.data.nbt;

import net.orange.game.data.json.JsonNumber;
import net.orange.game.data.json.JsonObj;
import org.jetbrains.annotations.NotNull;

public class TAG_Float extends NBTObj{
    private float data;
    public TAG_Float(float data){
        this.data = data;
    }
    public float getData() {
        return data;
    }

    public void setData(float data) {
        this.data = data;
    }

    @Override
    public @NotNull NBTType getType() {
        return NBTType.Float;
    }

    @Override
    public @NotNull StringBuilder dump(StringBuilder builder, int level, String indent, boolean nextline) {
        return builder.append(data).append("f");
    }
    @Override
    public String toString(){
        return data + "f";
    }

    @Override
    public @NotNull JsonObj toJson() {
        return new JsonNumber(data);
    }

    public TAG_Float copy(){
        return new TAG_Float(data);
    }
}
