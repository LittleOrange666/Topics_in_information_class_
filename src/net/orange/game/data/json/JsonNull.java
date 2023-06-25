package net.orange.game.data.json;

import net.orange.game.data.exception.JsonKeyException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;

public class JsonNull extends JsonObj {
    public JsonNull(){

    }
    @Override
    public @NotNull JsonType getType() {
        return JsonType.NULL;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
    }

    @Override
    public @NotNull StringBuilder dump(StringBuilder builder, int level, String indent, boolean nextline) {
        return builder.append("null");
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
