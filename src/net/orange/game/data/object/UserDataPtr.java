package net.orange.game.data.object;

import net.orange.game.data.json.JsonObject;
import net.orange.game.data.json.JsonTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record UserDataPtr(boolean isonline, String id, String name) {
    @Contract("_ -> new")
    public static @NotNull UserDataPtr from_json(@NotNull JsonObject obj){
        return new UserDataPtr(obj.getBoolean("isonline"),obj.getString("id"),obj.getString("name"));
    }
    @Contract(" -> new")
    public @NotNull JsonObject to_json(){
        return new JsonObject(new JsonTag("isonline",isonline()),new JsonTag("id",id()),new JsonTag("name",name));
    }
}
