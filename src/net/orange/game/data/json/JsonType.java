package net.orange.game.data.json;

public enum JsonType {
    NUMBER((byte) 0x01),STRING((byte) 0x02),BOOLEAN((byte) 0x03),ARRAY((byte) 0x04),OBJECT((byte) 0x05),NULL((byte) 0x06);
    public final byte id;

    JsonType(byte id) {
        this.id = id;
    }
}
