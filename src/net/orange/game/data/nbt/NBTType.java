package net.orange.game.data.nbt;

public enum NBTType {
    End((byte) 0x00),
    Byte((byte) 0x01),
    Short((byte) 0x02),
    Int((byte) 0x03),
    Long((byte) 0x04),
    Float((byte) 0x05),
    Double((byte) 0x06),
    Byte_Array((byte) 0x07),
    String((byte) 0x08),
    List((byte) 0x09),
    Compound((byte) 0x0a),
    Int_Array((byte) 0x0b),
    Long_Array((byte) 0x0c);
    public final byte id;

    NBTType(byte id) {
        this.id = id;
    }
}
