package net.orange.game.data.json;

import net.orange.game.data.exception.JsonKeyException;
import net.orange.game.data.exception.JsonTypeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonObject extends JsonObj implements Iterable<JsonTag>{
    private final Map<String, JsonObj> data;

    public JsonObject(Map<String, JsonObj> data) {
        this.data = data;
    }

    public JsonObject(){
        this.data = new HashMap<>();
    }
    public JsonObject(@NotNull List<JsonTag> data){
        this.data = new HashMap<>();
        for (JsonTag tag : data){
            this.data.put(tag.key(),tag.value());
        }
    }
    public JsonObject(JsonTag @NotNull ... data){
        this.data = new HashMap<>();
        for (JsonTag tag : data){
            this.data.put(tag.key(),tag.value());
        }
    }
    public int size(){
        return data.size();
    }

    public void put(String key, @NotNull JsonObj value){
        value.setPath("."+key,this);
        this.data.put(key, value);
    }
    public void put(String key, double value){
        put(key, new JsonNumber(value));
    }

    public void put(String key, String value){
        put(key, new JsonString(value));
    }

    public void put(@NotNull JsonTag value){
        put(value.key(), value.value());
    }

    public boolean has(String key){
        return data.containsKey(key);
    }

    @Override
    public @NotNull JsonType getType() {
        return JsonType.OBJECT;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        for(JsonTag tag : this){
            byte[] bytes = tag.key().getBytes(StandardCharsets.UTF_8);
            out.writeByte(tag.value().getType().id);
            out.writeInt(bytes.length);
            out.write(bytes);
            tag.value().write(out);
        }
        out.writeByte(0);
    }

    @Override
    public @NotNull StringBuilder dump(@NotNull StringBuilder builder, int level, String indent, boolean nextline) {
        nextline = data.size() > 0 && nextline;
        String thisindent = nextline?indent:"";
        String nl = nextline?"\n":"";
        String colon = nextline?": ":":";
        builder.append("{").append(nl);
        List<String> keys = data.keySet().stream().sorted().toList();
        for (int i = 0; i < data.size(); i++) {
            String key = keys.get(i);
            builder.append(thisindent.repeat(level+1))
                    .append("\"").append(key).append("\"").append(colon);
            data.get(key).dump(builder,level+1, indent, nextline);
            builder.append(i<data.size()-1?",":"").append(nl);
        }
        return builder.append(thisindent.repeat(level)).append("}");
    }

    @Override
    public @Nullable JsonObj read(@NotNull String path) {
        if (path.equals("")) return this;
        if (path.startsWith(".")){
            int i0 = path.indexOf(".",1);
            int i1 = path.indexOf("[",1);
            int i = path.length();
            if (i0!=-1) i = Math.min(i0,i);
            if (i1!=-1) i = Math.min(i1,i);
            String key = path.substring(1,i);
            if (has(key)){
                return get(key).read(path.substring(i));
            }
        }
        return null;
    }

    @Override
    public void write(@NotNull String path, JsonObj value) {
        if (path.startsWith(".")){
            int i0 = path.indexOf(".",1);
            int i1 = path.indexOf("[",1);
            int i = path.length();
            if (i0!=-1) i = Math.min(i0,i);
            if (i1!=-1) i = Math.min(i1,i);
            String key = path.substring(1,i);
            if (i == path.length()){
                put(key,value);
            }else{
                get(key).write(path.substring(i),value);
            }
            return;
        }
        throw new JsonKeyException("unknow path\""+getPath()+path+"\"",this);
    }

    public JsonObj get(String key){
        if (!data.containsKey(key)) throw new JsonKeyException("key \"" + key + "\" not found",this);
        return data.get(key);
    }
    private @NotNull JsonObj tryget(String key, JsonType type){
        JsonObj o = get(key);
        if (o.getType()!=type){
            throw new JsonTypeException("Unexpected type in key \""+key+"\": " + o.getType().name()+", type required:"+type.name(),this);
        }
        return o;
    }
    public boolean getBoolean(String key){
        JsonObj o = tryget(key,JsonType.BOOLEAN);
        return ((JsonBoolean)o).getBoolean();
    }

    public int getInt(String key){
        JsonObj o = tryget(key,JsonType.NUMBER);
        return ((JsonNumber)o).getInt();
    }

    public long getLong(String key){
        JsonObj o = tryget(key,JsonType.NUMBER);
        return ((JsonNumber)o).getLong();
    }

    public double getDouble(String key){
        JsonObj o = tryget(key,JsonType.NUMBER);
        return ((JsonNumber)o).getDouble();
    }

    public String getString(String key){
        JsonObj o = tryget(key,JsonType.STRING);
        return ((JsonString)o).getString();
    }

    public JsonArray getArray(String key){
        JsonObj o = tryget(key,JsonType.ARRAY);
        return (JsonArray)o;
    }

    public JsonObject getObject(String key){
        JsonObj o = tryget(key,JsonType.OBJECT);
        return (JsonObject)o;
    }
    public void remove(String key){
        data.remove(key);
    }

    @NotNull
    @Override
    public Iterator<JsonTag> iterator() {
        return data.entrySet().stream().map(JsonTag::new).iterator();
    }
    public Iterable<String> keys(){
        return data.keySet();
    }
}
