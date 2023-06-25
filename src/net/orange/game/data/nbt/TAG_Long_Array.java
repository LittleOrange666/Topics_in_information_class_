package net.orange.game.data.nbt;

import net.orange.game.data.json.JsonArray;
import net.orange.game.data.json.JsonNumber;
import net.orange.game.data.json.JsonObj;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class TAG_Long_Array extends NBTObj{
    private long[] data;

    public TAG_Long_Array(long... data) {
        this.data = data;
    }
    public long[] getData() {
        return data;
    }

    public void setData(long... data) {
        this.data = data;
    }
    public int length(){
        return data.length;
    }

    @Override
    public @NotNull NBTType getType() {
        return NBTType.Long_Array;
    }

    @Override
    public @NotNull StringBuilder dump(StringBuilder builder, int level, String indent, boolean nextline) {
        builder.append("[L;");
        for (int i = 0; i < data.length; i++) {
            builder.append(data[i]);
            if (i < data.length-1){
                builder.append(",");
            }
        }
        return builder.append("]");
    }

    @Override
    public @NotNull JsonObj toJson() {
        return new JsonArray(Arrays.stream(data).mapToObj(s->(JsonObj)new JsonNumber(s)).toList());
    }

    @Override
    public String toString(){
        return dump(new StringBuilder(), 0, "", false).toString();
    }

    public TAG_Long_Array copy(){
        return new TAG_Long_Array(data.clone());
    }
}
