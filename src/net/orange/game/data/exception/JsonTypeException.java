package net.orange.game.data.exception;

import net.orange.game.data.json.JsonObj;

public class JsonTypeException extends DataException {

    public JsonTypeException(String message, JsonObj object) {
        super(message+", path: "+object.getPath());
    }
}
