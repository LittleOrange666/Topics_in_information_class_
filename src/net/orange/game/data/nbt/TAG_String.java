package net.orange.game.data.nbt;

import net.orange.game.data.DataTool;
import net.orange.game.data.json.JsonString;
import net.orange.game.data.json.JsonObj;
import org.jetbrains.annotations.NotNull;

public class TAG_String extends NBTObj{
    public TAG_String(String data){
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    private String data;

    @Override
    public @NotNull NBTType getType() {
        return NBTType.String;
    }

    @Override
    public @NotNull StringBuilder dump(StringBuilder builder, int level, String indent, boolean nextline) {
        return builder.append("\"").append(DataTool.addBrackets(data)).append("\"");
    }

    @Override
    public String toString(){
        return data;
    }

    @Override
    public @NotNull JsonObj toJson() {
        return new JsonString(data);
    }

    public TAG_String copy(){
        return new TAG_String(data);
    }
}
