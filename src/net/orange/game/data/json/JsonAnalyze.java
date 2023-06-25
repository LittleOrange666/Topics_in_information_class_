package net.orange.game.data.json;


import net.orange.game.data.DataTool;
import net.orange.game.data.exception.JsonAnalyzeException;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JsonAnalyze {
    public static @NotNull JsonObject read(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
        StringBuilder text = new StringBuilder();
        String s;
        while ((s = reader.readLine()) != null){
            text.append(s).append("\n");
        }
        reader.close();
        return run(text.toString());
    }
    public static @NotNull JsonObject run(String string){
        string = simply(string);
        JsonObj obj = analyze(string);
        if (!(obj instanceof JsonObject)){
            throw new JsonAnalyzeException("Invalid json text, expected a JsonObject but got " + obj.getClass().getName());
        }
        return (JsonObject) obj;
    }
    public static @NotNull String simply(String string){
        string = string.replace("\t","").replace("\r","");
        int index = 0;
        int quote = 0;
        boolean slash = false;
        StringBuilder out = new StringBuilder();
        while (index<string.length()){
            char ch = string.charAt(index);
            if(quote == 0){
                if (ch != ' ' && ch != '\n'){
                    out.append(ch);
                    if (ch == '\"'){
                        quote = 2;
                    }
                    if (ch == '\''){
                        quote = 1;
                    }
                }
            }else if (quote == 1){
                if (ch != '\n') {
                    if (!slash && ch == '\'') quote = 0;
                    slash = ch == '\\' && !slash;
                    out.append(ch);
                }
            }else {
                if (ch != '\n') {
                    if (!slash && ch == '\"') quote = 0;
                    slash = ch == '\\' && !slash;
                    out.append(ch);
                }
            }
            index++;
        }
        return out.toString();
    }
    public static JsonObj analyze(@NotNull String string) throws JsonAnalyzeException {
        if (string.equals("null")){
            return new JsonNull();
        }else if (string.equals("true")){
            return new JsonBoolean(true);
        }else if (string.equals("false")){
            return new JsonBoolean(false);
        }else if (string.startsWith("{") && string.endsWith("}")){
            return compound(string);
        }else if (string.startsWith("[") && string.endsWith("]")){
            return list(string);
        }else if ((string.startsWith("\"") && string.endsWith("\"")) || (string.startsWith("'") && string.endsWith("'"))){
            return new JsonString(DataTool.removeBrackets(string.substring(1,string.length()-1)));
        }else {
            try {
                return new JsonNumber(Double.parseDouble(string));
            }catch (NumberFormatException e){
                throw new JsonAnalyzeException("number parse error while analyze json text", e);
            }
        }
    }
    private static @NotNull JsonArray list(@NotNull String string) throws JsonAnalyzeException {
        JsonArray r = new JsonArray();
        if (string.length()<=2) return r;
        int bbc = 0; // big brackets count
        int mbc = 0; // middle brackets count
        int index = 1;
        int startindex = 1;
        int quote = 0;
        boolean slash = false;
        while (index<string.length()){
            char ch = string.charAt(index);
            if(quote == 0){
                if (ch == '\"'){
                    quote = 2;
                }
                if (ch == '\''){
                    quote = 1;
                }
                if (ch == '{'){
                    bbc++;
                }
                if (ch == '}'){
                    bbc--;
                }
                if (ch == '['){
                    mbc++;
                }
                if (ch == ']'){
                    mbc--;
                }
                if (bbc == 0 && mbc == 0 && ch == ','){
                    r.add(analyze(string.substring(startindex,index)));
                    startindex = index + 1;
                }
            }else if (quote == 1){
                if (!slash && ch == '\'') quote = 0;
                slash = ch == '\\' && !slash;
            }else {
                if (!slash && ch == '\"') quote = 0;
                slash = ch == '\\' && !slash;
            }
            index ++;
        }
        r.add(analyze(string.substring(startindex,index-1)));
        return r;
    }
    private static @NotNull JsonObject compound(@NotNull String string) throws JsonAnalyzeException {
        JsonObject r = new JsonObject();
        if (string.length()<=2) return r;
        int bbc = 0; // big brackets count
        int mbc = 0; // middle brackets count
        int index = 1;
        int startindex = 1;
        int quote = 0;
        boolean slash = false;
        boolean key = true;
        String currentkey = "";
        while (index<string.length()){
            if (key){
                index = string.indexOf(":",startindex);
                if (index<0) break;
                currentkey = string.substring(startindex,index);
                if (currentkey.startsWith("\"") && currentkey.endsWith("\"")) currentkey = currentkey.substring(1,currentkey.length()-1);
                index = string.indexOf(":",startindex);
                startindex = index + 1;
                key = false;
            }
            char ch = string.charAt(index);
            if (quote == 0) {
                if (ch == '\"') {
                    quote = 2;
                }
                if (ch == '\'') {
                    quote = 1;
                }
                if (ch == '{') {
                    bbc++;
                }
                if (ch == '}') {
                    bbc--;
                }
                if (ch == '[') {
                    mbc++;
                }
                if (ch == ']') {
                    mbc--;
                }
                if (bbc == 0 && mbc == 0 && ch == ',') {
                    r.put(currentkey, analyze(string.substring(startindex, index)));
                    key = true;
                    startindex = index + 1;
                }
            } else if (quote == 1) {
                if (!slash && ch == '\'') quote = 0;
                slash = ch == '\\' && !slash;
            } else {
                if (!slash && ch == '\"') quote = 0;
                slash = ch == '\\' && !slash;
            }
            index ++;
        }
        r.put(currentkey, analyze(string.substring(startindex,string.length()-1)));
        return r;
    }
}
