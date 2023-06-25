package net.orange.game.data.nbt;

import net.orange.game.data.json.JsonArray;
import net.orange.game.data.json.JsonNumber;
import net.orange.game.data.json.JsonObj;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TAG_Byte_Array extends NBTObj{
    private byte[] data;

    public TAG_Byte_Array(byte... data) {
        this.data = data;
    }
    public byte[] getData() {
        return data;
    }

    public void setData(byte... data) {
        this.data = data;
    }
    public int length(){
        return data.length;
    }

    @Override
    public @NotNull NBTType getType() {
        return NBTType.Byte_Array;
    }

    @Override
    public @NotNull StringBuilder dump(StringBuilder builder, int level, String indent, boolean nextline) {
        builder.append("[B;");
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
        List<JsonObj> list = new ArrayList<>();
        for (byte b : data) {
            list.add(new JsonNumber(b));
        }
        return new JsonArray(list);
    }
    @Override
    public String toString(){
        return dump(new StringBuilder(), 0, "", false).toString();
    }

    public TAG_Byte_Array copy(){
        return new TAG_Byte_Array(data.clone());
    }
}
