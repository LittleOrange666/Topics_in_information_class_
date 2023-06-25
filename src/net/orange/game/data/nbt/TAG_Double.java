package net.orange.game.data.nbt;

import net.orange.game.data.json.JsonNumber;
import net.orange.game.data.json.JsonObj;
import org.jetbrains.annotations.NotNull;

public class TAG_Double extends NBTObj{
    private double data;
    public TAG_Double(double data){
        this.data = data;
    }
    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }

    @Override
    public @NotNull NBTType getType() {
        return NBTType.Double;
    }

    @Override
    public @NotNull StringBuilder dump(StringBuilder builder, int level, String indent, boolean nextline) {
        return builder.append(data).append("d");
    }
    @Override
    public String toString(){
        return data + "d";
    }

    @Override
    public @NotNull JsonObj toJson() {
        return new JsonNumber(data);
    }

    public TAG_Double copy(){
        return new TAG_Double(data);
    }
}
