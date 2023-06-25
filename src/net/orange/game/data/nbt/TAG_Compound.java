package net.orange.game.data.nbt;

import net.orange.game.data.DataTool;
import net.orange.game.data.json.JsonObject;
import net.orange.game.data.json.JsonObj;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TAG_Compound extends NBTObj{
    public Map<String, NBTObj> getData() {
        return data;
    }

    private Map<String, NBTObj> data;

    public TAG_Compound(Map<String, NBTObj> data) {
        this.data = data;
    }
    public TAG_Compound(List<NBTTag> data) {
        this.data = new HashMap<>();
        for (NBTTag tag : data){
            this.data.put(tag.key(),tag.value());
        }
    }

    public void setData(Map<String, NBTObj> data) {
        this.data = data;
    }
    public TAG_Compound(NBTTag... data) {
        this.data = new HashMap<>();
        for (NBTTag tag : data){
            this.data.put(tag.key(),tag.value());
        }
    }

    public TAG_Compound(){
        this.data = new HashMap<>();
    }
    public int size(){
        return data.size();
    }

    public boolean containsKey(String key){
        return data.containsKey(key);
    }

    public void put(String key, NBTObj value){
        this.data.put(key, value);
    }

    public NBTObj get(String key){
        return this.data.get(key);
    }

    @Override
    public @NotNull NBTType getType() {
        return NBTType.Compound;
    }

    @Override
    public @NotNull StringBuilder dump(StringBuilder builder, int level, String indent, boolean nextline) {
        nextline = data.size() > 0 && nextline;
        String thisindent = nextline?indent:"";
        String nl = nextline?"\n":"";
        String colon = nextline?": ":":";
        builder.append("{").append(nl);
        List<String> keys = data.keySet().stream().sorted().toList();
        for (int i = 0; i < data.size(); i++) {
            String key = keys.get(i);
            String thekey = (key.contains(":") || key.contains("\""))?"\""+ DataTool.addBrackets(key)+"\"":key;
            builder.append(thisindent.repeat(level+1))
                    .append(thekey).append(colon);
            data.get(key).dump(builder,level+1, indent, nextline);
            builder.append(i<data.size()-1?",":"").append(nl);
        }
        return builder.append(thisindent.repeat(level)).append("}");
    }

    @Override
    public @NotNull JsonObj toJson() {
        Map<String, JsonObj> map = new HashMap<>();
        data.forEach((a,b)->map.put(a,b.toJson()));
        return new JsonObject(map);
    }

    @Override
    public String toString(){
        return dump(new StringBuilder(), 0, "", false).toString();
    }
    public TAG_Compound copy(){
        TAG_Compound r = new TAG_Compound();
        for (String key : data.keySet()){
            r.put(key,NBTObj.copy(data.get(key)));
        }
        return r;
    }
}
