package net.orange.game.data.json;

import net.orange.game.data.exception.JsonKeyException;
import net.orange.game.data.exception.JsonTypeException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class JsonArray extends JsonObj implements Iterable<JsonObj>{
    public JsonArray(){
        this.data = new ArrayList<>();
    }
    public JsonArray(ArrayList<JsonObj> data){
        this.data = data;
    }
    public JsonArray(List<JsonObj> data){
        this.data = new ArrayList<>(data);
    }
    public JsonArray(JsonObj... data){
        this.data = new ArrayList<>(Arrays.asList(data));
    }
    public JsonArray(double... data){
        this.data = new ArrayList<>(Arrays.stream(data).mapToObj(d->(JsonObj)new JsonNumber(d)).toList());
    }
    public JsonArray(String... data){
        this.data = new ArrayList<>(Arrays.stream(data).map(s->(JsonObj)new JsonString(s)).toList());
    }
    private final ArrayList<JsonObj> data;
    public void add(@NotNull JsonObj jsonobj){
        jsonobj.setPath("["+data.size()+"]",this);
        data.add(jsonobj);
    }
    public void add(String value){
        add(new JsonString(value));
    }
    public void add(double value){
        add(new JsonNumber(value));
    }

    public void put(int key, @NotNull JsonObj value){
        value.setPath("."+key,this);
        this.data.set(key, value);
    }
    public void put(int key, double value){
        put(key, new JsonNumber(value));
    }

    public void put(int key, String value){
        put(key, new JsonString(value));
    }

    @Override
    public @NotNull JsonType getType() {
        return JsonType.ARRAY;
    }

    @Override
    public void write(@NotNull DataOutputStream out) throws IOException {
        for(JsonObj jsonObj : data){
            out.writeByte(jsonObj.getType().id);
            jsonObj.write(out);
        }
        out.writeByte((byte)0x00);
    }

    public List<JsonObj> getData() {
        return data;
    }
    public int size(){
        return data.size();
    }
    public int length(){
        return data.size();
    }

    @Override
    public @NotNull StringBuilder dump(@NotNull StringBuilder builder, int level, String indent, boolean nextline) {
        if (data.isEmpty()) return builder.append("[]");
        String thisindent = nextline?indent:"";
        String nl = nextline?"\n":"";
        builder.append("[").append((data.get(0) instanceof JsonNumber)?"":nl);
        for (int i = 0; i < data.size(); i++) {
            JsonObj jsonobj = data.get(i);
            if (jsonobj instanceof JsonNumber){
                jsonobj.dump(builder, level + 1, indent, nextline);
                builder.append(i < data.size() - 1 ? ","+((data.get(i+1) instanceof JsonNumber)?"":nl) : "");
            }else {
                builder.append(thisindent.repeat(level + 1));
                jsonobj.dump(builder, level + 1, indent, nextline);
                builder.append(i < data.size() - 1 ? "," : "").append(nl);
            }
        }
        return builder.append((data.get(data.size()-1) instanceof JsonNumber)?"":thisindent.repeat(level)).append("]");
    }

    @Override
    public @Nullable JsonObj read(@NotNull String path) {
        if (path.equals("")) return this;
        if (path.charAt(0)=='['){
            int i = path.indexOf(']',1);
            if (i!=-1){
                try{
                    int idx = Integer.parseInt(path.substring(1,i));
                    if (idx>=0 && idx<length()){
                        return get(idx).read(path.substring(i+1));
                    }
                }catch (NumberFormatException ignored){}
            }
        }
        return null;
    }

    @Override
    public void write(@NotNull String path, JsonObj value) {
        if (path.charAt(0)=='['){
            int i = path.indexOf(']',1);
            if (i!=-1){
                try{
                    int idx = Integer.parseInt(path.substring(1,i));
                    if (idx>=0 && idx<length()){
                        if (i == path.length()-1){
                            put(idx,value);
                        }else{
                            get(idx).write(path.substring(i+1),value);
                        }
                        return;
                    }
                }catch (NumberFormatException ignored){}
            }
        }
        throw new JsonKeyException("unknow path\""+getPath()+path+"\"",this);
    }

    public JsonObj get(int index){
        if (index < 0 || index >= data.size()) throw new JsonKeyException("Index \""+index+"\" out of range",this);
        JsonObj o = data.get(index);
        return o==null?new JsonNull():o;
    }
    private @NotNull JsonObj tryget(int index, JsonType type){
        JsonObj o = get(index);
        if (o.getType()!=type){
            throw new JsonTypeException("Unexpected type in index \""+index+"\": " + o.getType().name()+", type required:"+type.name(),this);
        }
        return o;
    }
    public boolean getBoolean(int index){
        JsonObj o = tryget(index, JsonType.BOOLEAN);
        return ((JsonBoolean)o).getBoolean();
    }

    public int getInt(int index){
        JsonObj o = tryget(index, JsonType.NUMBER);
        return ((JsonNumber)o).getInt();
    }

    public long getLong(int index){
        JsonObj o = tryget(index, JsonType.NUMBER);
        return ((JsonNumber)o).getLong();
    }

    public double getDouble(int index){
        JsonObj o = tryget(index, JsonType.NUMBER);
        return ((JsonNumber)o).getDouble();
    }

    public String getString(int index){
        JsonObj o = tryget(index, JsonType.STRING);
        return ((JsonString)o).getString();
    }

    public JsonArray getArray(int index){
        JsonObj o = tryget(index, JsonType.ARRAY);
        return (JsonArray)o;
    }

    public JsonObject getObject(int index){
        JsonObj o = tryget(index, JsonType.OBJECT);
        return (JsonObject)o;
    }
    public boolean contains(JsonObj obj){
        return data.contains(obj);
    }
    public boolean contains(String value){
        for(JsonObj o : data){
            if (o instanceof JsonString s && s.getData().equals(value)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @NotNull
    @Override
    public Iterator<JsonObj> iterator() {
        return data.iterator();
    }
    public Iterable<JsonObject> objects() {
        return ()->new ObjectIterator(this);
    }
    public Iterable<JsonArray> arrays() {
        return ()->new ArrayIterator(this);
    }
    public Iterable<String> strings() {
        return ()->new StringIterator(this);
    }
    public Iterable<Double> doubles() {
        return ()->new DoubleIterator(this);
    }
    public Iterable<Integer> ints() {
        return ()->new IntIterator(this);
    }
    private static class ObjectIterator implements Iterator<JsonObject> {
        private static final JsonType type = JsonType.OBJECT;
        private final Iterator<JsonObj> it;
        private final JsonArray parent;

        private ObjectIterator(@NotNull JsonArray parent) {
            this.parent = parent;
            it = parent.iterator();
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Contract(pure = true)
        @Override
        public @Nullable JsonObject next() {
            JsonObj o = it.next();
            if (o == null) o = new JsonNull();
            if (o.getType() != type){
                throw new JsonTypeException("Unexpected type in Iterator: " + o.getType().name()+", type required:"+type.name(),parent);
            }
            return (JsonObject) o;
        }
    }
    private static class ArrayIterator implements Iterator<JsonArray> {
        private static final JsonType type = JsonType.ARRAY;
        private final Iterator<JsonObj> it;
        private final JsonArray parent;

        private ArrayIterator(@NotNull JsonArray parent) {
            this.parent = parent;
            it = parent.iterator();
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Contract(pure = true)
        @Override
        public @Nullable JsonArray next() {
            JsonObj o = it.next();
            if (o == null) o = new JsonNull();
            if (o.getType() != type){
                throw new JsonTypeException("Unexpected type in Iterator: " + o.getType().name()+", type required:"+type.name(),parent);
            }
            return (JsonArray) o;
        }
    }
    private static class StringIterator implements Iterator<String> {
        private static final JsonType type = JsonType.STRING;
        private final Iterator<JsonObj> it;
        private final JsonArray parent;

        private StringIterator(@NotNull JsonArray parent) {
            this.parent = parent;
            it = parent.iterator();
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Contract(pure = true)
        @Override
        public @Nullable String next() {
            JsonObj o = it.next();
            if (o == null) o = new JsonNull();
            if (o.getType() != type){
                throw new JsonTypeException("Unexpected type in Iterator: " + o.getType().name()+", type required:"+type.name(),parent);
            }
            return ((JsonString) o).getData();
        }
    }
    private static class DoubleIterator implements Iterator<Double> {
        private static final JsonType type = JsonType.NUMBER;
        private final Iterator<JsonObj> it;
        private final JsonArray parent;

        private DoubleIterator(@NotNull JsonArray parent) {
            this.parent = parent;
            it = parent.iterator();
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Contract(pure = true)
        @Override
        public @Nullable Double next() {
            JsonObj o = it.next();
            if (o == null) o = new JsonNull();
            if (o.getType() != type){
                throw new JsonTypeException("Unexpected type in Iterator: " + o.getType().name()+", type required:"+type.name(),parent);
            }
            return ((JsonNumber) o).getDouble();
        }
    }
    private static class IntIterator implements Iterator<Integer> {
        private static final JsonType type = JsonType.NUMBER;
        private final Iterator<JsonObj> it;
        private final JsonArray parent;

        private IntIterator(@NotNull JsonArray parent) {
            this.parent = parent;
            it = parent.iterator();
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Contract(pure = true)
        @Override
        public @Nullable Integer next() {
            JsonObj o = it.next();
            if (o == null) o = new JsonNull();
            if (o.getType() != type){
                throw new JsonTypeException("Unexpected type in Iterator: " + o.getType().name()+", type required:"+type.name(),parent);
            }
            return ((JsonNumber) o).getInt();
        }
    }
}
