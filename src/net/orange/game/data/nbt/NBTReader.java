package net.orange.game.data.nbt;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class NBTReader {
    public static TAG_Compound read(byte[] bytes) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        TAG_Compound r = read(inputStream);
        inputStream.close();
        return r;
    }
    public static TAG_Compound read(InputStream inputStream) throws IOException {
        inputStream.skip(3);
        return readCompound(new DataInputStream(inputStream));
    }
    public static NBTObj read(byte type, DataInputStream inputStream) throws IOException {
        return switch (type) {
            case 1 -> readByte(inputStream);
            case 2 -> readShort(inputStream);
            case 3 -> readInt(inputStream);
            case 4 -> readLong(inputStream);
            case 5 -> readFloat(inputStream);
            case 6 -> readDouble(inputStream);
            case 7 -> readByte_Array(inputStream);
            case 8 -> readString(inputStream);
            case 9 -> readList(inputStream);
            case 10 -> readCompound(inputStream);
            case 11 -> readInt_Array(inputStream);
            case 12 -> readLong_Array(inputStream);
            default -> new TAG_End();
        };
    }
    public static TAG_Byte readByte(DataInputStream inputStream) throws IOException { // 1=
        return new TAG_Byte(inputStream.readByte());
    }
    public static TAG_Short readShort(DataInputStream inputStream) throws IOException { // 2
        return new TAG_Short(inputStream.readShort());
    }
    public static TAG_Int readInt(DataInputStream inputStream) throws IOException { // 3
        return new TAG_Int(inputStream.readInt());
    }
    public static TAG_Long readLong(DataInputStream inputStream) throws IOException { // 4
        return new TAG_Long(inputStream.readLong());
    }
    public static TAG_Float readFloat(DataInputStream inputStream) throws IOException { // 5
        return new TAG_Float(inputStream.readFloat());
    }
    public static TAG_Double readDouble(DataInputStream inputStream) throws IOException { // 6
        return new TAG_Double(inputStream.readDouble());
    }
    public static TAG_Byte_Array readByte_Array(DataInputStream inputStream) throws IOException { // 7
        int size = inputStream.readInt();
        byte[] data = new byte[size];
        for (int i = 0; i < size; i++) {
            data[i] = inputStream.readByte();
        }
        return new TAG_Byte_Array(data);
    }
    public static TAG_String readString(DataInputStream inputStream) throws IOException { // 8
        int length = inputStream.readUnsignedShort();
        byte[] bytes = new byte[length];
        int reallength = inputStream.read(bytes,0,length);
        byte[] realbytes = new byte[reallength];
        System.arraycopy(bytes,0,realbytes,0,reallength);
        return new TAG_String(new String(realbytes, StandardCharsets.UTF_8));
    }
    public static TAG_List readList(DataInputStream inputStream) throws IOException { // 9
        byte type = inputStream.readByte();
        int size = inputStream.readInt();
        TAG_List r = new TAG_List();
        for (int i = 0; i < size; i++) {
            r.add(read(type,inputStream));
        }
        return r;
    }
    public static TAG_Compound readCompound(DataInputStream inputStream) throws IOException { // 10
        TAG_Compound r = new TAG_Compound();
        boolean running = true;
        while (running){
            if (inputStream.available()>0) {
                byte type = inputStream.readByte();
                if (type > 0) {
                    int namelen = inputStream.readUnsignedShort();
                    byte[] bytes = new byte[namelen];
                    int reallength = inputStream.read(bytes, 0, namelen);
                    byte[] realbytes = new byte[reallength];
                    System.arraycopy(bytes, 0, realbytes, 0, reallength);
                    String name = new String(realbytes, StandardCharsets.UTF_8);
                    NBTObj data = read(type, inputStream);
                    r.put(name, data);
                } else {
                    running = false;
                }
            }else {
                running = false;
            }
        }
        return r;
    }
    public static TAG_Int_Array readInt_Array(DataInputStream inputStream) throws IOException { // 11
        int size = inputStream.readInt();
        int[] data = new int[size];
        for (int i = 0; i < size; i++) {
            data[i] = inputStream.readInt();
        }
        return new TAG_Int_Array(data);
    }
    public static TAG_Long_Array readLong_Array(DataInputStream inputStream) throws IOException { // 12
        int size = inputStream.readInt();
        long[] data = new long[size];
        for (int i = 0; i < size; i++) {
            data[i] = inputStream.readLong();
        }
        return new TAG_Long_Array(data);
    }
}
