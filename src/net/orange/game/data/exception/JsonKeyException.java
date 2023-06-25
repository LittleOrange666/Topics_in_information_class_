package net.orange.game.data.exception;

import net.orange.game.data.json.JsonObj;
import org.jetbrains.annotations.NotNull;

public class JsonKeyException extends DataException {

    public JsonKeyException(String message, @NotNull JsonObj obj) {
        super(message+", path: "+obj.getPath());
    }
}
