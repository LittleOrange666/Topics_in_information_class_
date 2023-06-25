package net.orange.game.data.nbt;

public class NBTTag {
    private final String key;
    private final NBTObj value;
    public NBTTag(String key, NBTObj value){
        this.key = key;
        this.value = value;
    }
    public NBTTag(String key, byte value){
        this.key = key;
        this.value = new TAG_Byte(value);
    }
    public NBTTag(String key, byte... value){
        this.key = key;
        this.value = new TAG_Byte_Array(value);
    }
    public NBTTag(String key, double value){
        this.key = key;
        this.value = new TAG_Double(value);
    }
    public NBTTag(String key, float value){
        this.key = key;
        this.value = new TAG_Float(value);
    }
    public NBTTag(String key, int value){
        this.key = key;
        this.value = new TAG_Int(value);
    }
    public NBTTag(String key, int... value){
        this.key = key;
        this.value = new TAG_Int_Array(value);
    }
    public NBTTag(String key, long value){
        this.key = key;
        this.value = new TAG_Long(value);
    }
    public NBTTag(String key, String value){
        this.key = key;
        this.value = new TAG_String(value);
    }
    public NBTTag(String key, long... value){
        this.key = key;
        this.value = new TAG_Long_Array(value);
    }
    public NBTTag(String key, NBTObj... value){
        this.key = key;
        this.value = new TAG_List(value);
    }
    public NBTTag(String key, NBTTag... value){
        this.key = key;
        this.value = new TAG_Compound(value);
    }
    public String key(){
        return key;
    }
    public NBTObj value(){
        return value;
    }
}