package net.orange.game.data.nbt;

import net.orange.game.data.json.JsonArray;
import net.orange.game.data.json.JsonNumber;
import net.orange.game.data.json.JsonObj;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class TAG_Int_Array extends NBTObj{
    private int[] data;

    public TAG_Int_Array(int... data) {
        this.data = data;
    }
    public int[] getData() {
        return data;
    }

    public void setData(int... data) {
        this.data = data;
    }
    public int length(){
        return data.length;
    }

    @Override
    public @NotNull NBTType getType() {
        return NBTType.Int_Array;
    }

    @Override
    public @NotNull StringBuilder dump(StringBuilder builder, int level, String indent, boolean nextline) {
        builder.append("[I;");
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

    public TAG_Int_Array copy(){
        return new TAG_Int_Array(data.clone());
    }
}
