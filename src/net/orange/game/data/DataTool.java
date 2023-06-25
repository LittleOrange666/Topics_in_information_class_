package net.orange.game.data;

import net.orange.game.data.exception.DataIOException;
import net.orange.game.data.json.JsonAnalyze;
import net.orange.game.data.exception.JsonAnalyzeException;
import net.orange.game.data.json.JsonObject;
import net.orange.game.data.json.JsonReader;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.HashMap;
import java.util.zip.*;

public class DataTool {
    private static final HashMap<Character,Integer> mp = new HashMap<>();
    private static final String s = "0123456789abcdef";
    static {
        for (int i = 0; i < 16; i++) {
            mp.put(s.charAt(i), i);
        }
    }
    public static @NotNull String removeBrackets(@NotNull String string){
        StringBuilder builder = new StringBuilder();
        boolean slash = false;
        for(int i=0; i<string.length(); i++){
            char c = string.charAt(i);
            if (slash){
                switch (c) {
                    case 'b' -> builder.append("\b");
                    case 'n' -> builder.append("\n");
                    case 't' -> builder.append("\t");
                    case 'r' -> builder.append("\r");
                    case 'f' -> builder.append("\f");
                    case 'u' ->{
                        int x = mp.get(string.charAt(i + 1));
                        x = x*16 + mp.get(string.charAt(i+2));
                        x = x*16 + mp.get(string.charAt(i+3));
                        x = x*16 + mp.get(string.charAt(i+4));
                        builder.append((char)x);
                        i+=4;
                    }
                    default -> builder.append(c);
                }
                slash = false;
            }else if (c == '\\'){
                slash = true;
            }else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
    public static @NotNull String addBrackets(@NotNull String string){
        StringBuilder builder = new StringBuilder();
        builder.append("\"");
        for(int i=0; i<string.length(); i++){
            char c = string.charAt(i);
            switch (c) {
                case '\b' -> builder.append("\\b");
                case '\n' -> builder.append("\\n");
                case '\t' -> builder.append("\\t");
                case '\r' -> builder.append("\\r");
                case '\f' -> builder.append("\\f");
                case '\'' -> builder.append("\\'");
                case '\"' -> builder.append("\\\"");
                case '\\' -> builder.append("\\\\");
                default -> builder.append(c);
            }
        }
        builder.append("\"");
        return builder.toString();
    }
    public static @NotNull String json_addBrackets(@NotNull String s){
        StringBuilder builder = new StringBuilder();
        for (char c : s.toCharArray()){
            if (c>127){
                builder.append("\\u");
                getString(c,builder);
            }else if(c == '\\'){
                builder.append("\\\\");
            }else if(c == '\n'){
                builder.append("\\n");
            }else if(c == '\t'){
                builder.append("\\t");
            }else if(c == '\r'){
                builder.append("\\r");
            }else if(c == '\f'){
                builder.append("\\f");
            }else if(c == '\"'){
                builder.append("\\\"");
            }else if(c == '\''){
                builder.append("\\'");
            }else if(c == '\b'){
                builder.append("\\b");
            }else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
    private static void getString(int c, StringBuilder builder){
        StringBuilder builder1 = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            builder1.append(byteToChar(c%16));
            c = c / 16;
        }
        builder.append(builder1.reverse());
    }
    private static char byteToChar(int c) {
        return s.charAt(c);
    }
    public static @NotNull JsonObject read_textfile(String file){
        try {
            return JsonAnalyze.read(file);
        }catch (IOException | JsonAnalyzeException e){
            throw new DataIOException("Could not read json text file " + file, e);
        }
    }
    public static void write_textfile(String file, @NotNull JsonObject content){
        try (FileWriter writer = new FileWriter(file)){
            writer.write(content.dump(true));
        } catch (IOException e) {
            throw new DataIOException("Could not write json text file " + file, e);
        }
    }
    public static @NotNull JsonObject read_binaryfile(String file){
        try {
            return JsonReader.read(new FileInputStream(file));
        }catch (IOException e){
            throw new DataIOException("Could not read json binary file " + file, e);
        }
    }
    public static @NotNull JsonObject read_zipfile(String file){
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            byte[] bytes = fileInputStream.readAllBytes();
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
            JsonObject r = JsonReader.read(gzipInputStream.readAllBytes());
            gzipInputStream.close();
            return r;
        }catch (IOException e){
            throw new DataIOException("Could not read json zip file " + file, e);
        }
    }
    public static void write_binaryfile(String file, @NotNull JsonObject content){
        try {
            content.write(new DataOutputStream(new FileOutputStream(file)));
        }catch (IOException e){
            throw new DataIOException("Could not write json binary file " + file, e);
        }
    }
    public static void write_zipfile(String file, @NotNull JsonObject content){
        try {
            GZIPOutputStream outputStream = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            content.write(new DataOutputStream(byteArrayOutputStream));
            byteArrayOutputStream.close();
            outputStream.write(byteArrayOutputStream.toByteArray());
            outputStream.close();
        }catch (IOException e){
            throw new DataIOException("Could not write json zip file " + file, e);
        }
    }
}
