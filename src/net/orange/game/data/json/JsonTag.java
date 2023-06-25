package net.orange.game.data.json;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record JsonTag(String key, JsonObj value) {
    public JsonTag(Map.@NotNull Entry<String, JsonObj> entry) {
        this(entry.getKey(), entry.getValue());
    }
    public JsonTag(String key, boolean value){
        this(key, new JsonBoolean(value));
    }
    public JsonTag(String key, double value){
        this(key, new JsonNumber(value));
    }
    public JsonTag(String key, String value){
        this(key, new JsonString(value));
    }
    public JsonTag(String key, JsonTag... value){
        this(key, new JsonObject(value));
    }
    public JsonTag(String key, JsonObj... value){
        this(key, new JsonArray(value));
    }
}
