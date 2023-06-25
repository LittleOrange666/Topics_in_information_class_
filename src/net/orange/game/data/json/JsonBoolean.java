package net.orange.game.data.json;

import net.orange.game.data.exception.JsonKeyException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;

public class JsonBoolean extends JsonObj {
    public JsonBoolean(boolean data){
        this.data = data;
    }
    private final boolean data;
    @Override
    public @NotNull JsonType getType() {
        return JsonType.BOOLEAN;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        out.writeBoolean(data);
    }

    @Override
    public @NotNull StringBuilder dump(@NotNull StringBuilder builder, int level, String indent, boolean nextline) {
        return builder.append(data?"true":"false");
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

    public boolean getBoolean(){
        return data;
    }
    public boolean getData(){
        return data;
    }
}
