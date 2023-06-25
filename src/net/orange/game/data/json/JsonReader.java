package net.orange.game.data.json;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JsonReader {
    public static @NotNull JsonObject read(byte[] data) throws IOException {
        return read(new ByteArrayInputStream(data));
    }
    public static @NotNull JsonObject read(@NotNull InputStream in) throws IOException {
        return readObject(new DataInputStream(in));
    }
    private static JsonObj read(byte type, DataInputStream in) throws IOException {
        switch (type) {
            case 1 -> {
                return readNumber(in);
            }
            case 2 -> {
                return readString(in);
            }
            case 3 -> {
                return readBoolean(in);
            }
            case 4 -> {
                return readArray(in);
            }
            case 5 -> {
                return readObject(in);
            }
            case 6 -> {
                return readNull(in);
            }
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    @Contract("_ -> new")
    private static @NotNull JsonNull readNull(DataInputStream in) {
        return new JsonNull();
    }

    private static @NotNull JsonObject readObject(@NotNull DataInputStream in) throws IOException {
        JsonObject r = new JsonObject();
        byte type;
        while ((type = in.readByte())!=0){
            int len = in.readInt();
            byte[] bytes = in.readNBytes(len);
            r.put(new String(bytes, StandardCharsets.UTF_8), read(type,in));
        }
        return r;
    }

    private static @NotNull JsonArray readArray(@NotNull DataInputStream in) throws IOException {
        JsonArray r = new JsonArray();
        byte type;
        while ((type = in.readByte())!=0){
            r.add(read(type,in));
        }
        return r;
    }

    @Contract("_ -> new")
    private static @NotNull JsonBoolean readBoolean(@NotNull DataInputStream in) throws IOException {
        return new JsonBoolean(in.readBoolean());
    }

    private static @NotNull JsonString readString(@NotNull DataInputStream in) throws IOException {
        int len = in.readInt();
        byte[] bytes = in.readNBytes(len);
        return new JsonString(new String(bytes, StandardCharsets.UTF_8));
    }

    @Contract("_ -> new")
    private static @NotNull JsonNumber readNumber(@NotNull DataInputStream in) throws IOException {
        return new JsonNumber(in.readDouble());
    }
}