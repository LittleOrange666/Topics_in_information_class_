package net.orange.game.data.nbt;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class NBTWriter {
    public static byte[] write(TAG_Compound data) throws IOException, NBTWriteException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        write(data,outputStream);
        outputStream.close();
        return outputStream.toByteArray();
    }
    public static void write(TAG_Compound data, OutputStream outputStream) throws IOException, NBTWriteException {
        write(data,new DataOutputStream(outputStream));
    }
    public static void write(TAG_Compound data, DataOutputStream outputStream) throws IOException, NBTWriteException {
        outputStream.write(new byte[]{0x0a,0x00,0x00});
        writeCompound(data, outputStream);
    }
    public static void writeObj(NBTObj obj, DataOutputStream outputStream) throws IOException, NBTWriteException {
        if (obj instanceof TAG_Byte){
            writeByte((TAG_Byte) obj,outputStream);
        }else if (obj instanceof TAG_Short){
            writeShort((TAG_Short) obj,outputStream);
        }else if (obj instanceof TAG_Int){
            writeInt((TAG_Int) obj,outputStream);
        }else if (obj instanceof TAG_Long){
            writeLong((TAG_Long) obj,outputStream);
        }else if (obj instanceof TAG_Float){
            writeFloat((TAG_Float) obj,outputStream);
        }else if (obj instanceof TAG_Double){
            writeDouble((TAG_Double) obj,outputStream);
        }else if (obj instanceof TAG_Byte_Array){
            writeByte_Array((TAG_Byte_Array) obj,outputStream);
        }else if (obj instanceof TAG_String){
            writeString((TAG_String) obj,outputStream);
        }else if (obj instanceof TAG_List){
            writeList((TAG_List) obj,outputStream);
        }else if (obj instanceof TAG_Compound){
            writeCompound((TAG_Compound) obj,outputStream);
        }else if (obj instanceof TAG_Int_Array){
            writeInt_Array((TAG_Int_Array) obj,outputStream);
        }else if (obj instanceof TAG_Long_Array){
            writeLong_Array((TAG_Long_Array) obj,outputStream);
        }
    }
    public static void writeByte(TAG_Byte data, DataOutputStream outputStream) throws IOException {
        outputStream.writeByte(data.getData());
    }
    public static void writeShort(TAG_Short data, DataOutputStream outputStream) throws IOException {
        outputStream.writeShort(data.getData());
    }
    public static void writeInt(TAG_Int data, DataOutputStream outputStream) throws IOException {
        outputStream.writeInt(data.getData());
    }
    public static void writeLong(TAG_Long data, DataOutputStream outputStream) throws IOException {
        outputStream.writeLong(data.getData());
    }
    public static void writeFloat(TAG_Float data, DataOutputStream outputStream) throws IOException {
        outputStream.writeFloat(data.getData());
    }
    public static void writeDouble(TAG_Double data, DataOutputStream outputStream) throws IOException {
        outputStream.writeDouble(data.getData());
    }
    public static void writeByte_Array(TAG_Byte_Array data, DataOutputStream outputStream) throws IOException {
        byte[] bytes = data.getData();
        outputStream.writeInt(bytes.length);
        for(byte b : bytes) outputStream.writeByte(b);
    }
    public static void writeString(TAG_String data, DataOutputStream outputStream) throws IOException, NBTWriteException {
        String string  = data.getData();
        if (string.length()>65535){
            throw new NBTWriteException("string too long ("+string.length()+" bigger then 65535)");
        }
        int l = string.length();
        outputStream.writeShort(l);
        outputStream.write(string.getBytes(StandardCharsets.UTF_8));
    }
    public static void writeList(TAG_List data, DataOutputStream outputStream) throws IOException, NBTWriteException {
        List<NBTObj> objs = data.getData();
        if (objs.size()>0){
            NBTType type = objs.get(0).getType();
            outputStream.writeByte(type.id);
            outputStream.writeInt(objs.size());
            for(NBTObj obj : objs){
                if (obj.getType() != type){
                    throw new NBTWriteException("cant write TAG_"+obj.getType().name()+" in List of TAG_"+type.name());
                }
                writeObj(obj,outputStream);
            }
        }else {
            outputStream.write(new byte[]{0x00,0x00,0x00,0x00,0x00});
        }
    }
    public static void writeCompound(TAG_Compound data, DataOutputStream outputStream) throws IOException , NBTWriteException{
        Map<String,NBTObj> map = data.getData();
        for (String key : map.keySet()){
            NBTObj obj = map.get(key);
            outputStream.writeByte(obj.getType().id);
            if (key.length()>65535){
                throw new NBTWriteException("key too long ("+key.length()+" bigger then 65535)");
            }
            int l = key.length();
            outputStream.writeShort(l);
            outputStream.write(key.getBytes(StandardCharsets.UTF_8));
            writeObj(obj,outputStream);
        }
        outputStream.write(new byte[]{0x00});
    }
    public static void writeInt_Array(TAG_Int_Array data, DataOutputStream outputStream) throws IOException {
        int[] ints = data.getData();
        outputStream.writeInt(ints.length);
        for(int i : ints) outputStream.writeInt(i);
    }
    public static void writeLong_Array(TAG_Long_Array data, DataOutputStream outputStream) throws IOException {
        long[] longs = data.getData();
        outputStream.writeInt(longs.length);
        for (long l : longs) outputStream.writeLong(l);
    }
}
