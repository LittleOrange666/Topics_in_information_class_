package net.orange.game.data.json;

import net.orange.game.data.exception.JsonKeyException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;

public class JsonNumber extends JsonObj {
    public JsonNumber(double data){
        this.data = data;
    }
    public JsonNumber(int data){
        this.data = data;
    }
    public JsonNumber(long data){
        this.data = data;
    }
    private final double data;
    @Override
    public @NotNull JsonType getType() {
        return JsonType.NUMBER;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        out.writeDouble(data);
    }

    @Override
    public @NotNull StringBuilder dump(@NotNull StringBuilder builder, int level, String indent, boolean nextline) {
        return builder.append(((int)data == data)?(String.valueOf((int) data)):(String.valueOf(data)));
    }
    public int getInt(){
        return (int) data;
    }
    public long getLong(){
        return (long) data;
    }
    public double getDouble(){
        return data;
    }

    @Override
    public @Nullable JsonObj read(@NotNull String path) {
        if (path.equals("")) return this;
        return null;
    }

    @Override
    public void write(@NotNull String path, JsonObj value) {
        throw new JsonKeyException("unknow path\""+getPath()+path+"\"",this);
    }
}
