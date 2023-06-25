package net.orange.game.data.json;

import net.orange.game.data.DataTool;
import net.orange.game.data.exception.JsonKeyException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JsonString extends JsonObj {
    public JsonString(@NotNull String data){
        this.data = data;
    }

    public void setData(@NotNull String data) {
        this.data = data;
    }
    public String getData() {
        return this.data;
    }
    public String getString() {
        return this.data;
    }

    private String data;
    @Override
    public @NotNull JsonType getType() {
        return JsonType.STRING;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    @Override
    public @NotNull StringBuilder dump(@NotNull StringBuilder builder, int level, String indent, boolean nextline) {
        return builder.append("\"").append(DataTool.json_addBrackets(data)).append("\"");
    }

    @Override
    public String toString(){
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
