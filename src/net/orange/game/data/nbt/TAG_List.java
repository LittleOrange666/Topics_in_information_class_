package net.orange.game.data.nbt;

import net.orange.game.data.json.JsonArray;
import net.orange.game.data.json.JsonObj;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TAG_List extends NBTObj implements Iterable<NBTObj> {
    public TAG_List(){
        this.data = new ArrayList<>();
    }
    public TAG_List(List<NBTObj> data){
        this.data = data;
    }
    public TAG_List(NBTObj... data){
        this.data = Arrays.asList(data);
    }
    public TAG_List(Byte... data){
        this.data = Arrays.stream(data).map((i)->(NBTObj)new TAG_Byte(i)).toList();
    }
    public TAG_List(Short... data){
        this.data = Arrays.stream(data).map((i)->(NBTObj)new TAG_Short(i)).toList();
    }
    public TAG_List(Integer... data){
        this.data = Arrays.stream(data).map((i)->(NBTObj)new TAG_Int(i)).toList();
    }
    public TAG_List(Long... data){
        this.data = Arrays.stream(data).map((i)->(NBTObj)new TAG_Long(i)).toList();
    }
    public TAG_List(Float... data){
        this.data = Arrays.stream(data).map((i)->(NBTObj)new TAG_Float(i)).toList();
    }
    public TAG_List(Double... data){
        this.data = Arrays.stream(data).map((i)->(NBTObj)new TAG_Double(i)).toList();
    }
    public TAG_List(String... data){
        this.data = Arrays.stream(data).map((i)->(NBTObj)new TAG_String(i)).toList();
    }

    public List<NBTObj> getData() {
        return data;
    }

    private List<NBTObj> data;
    public NBTObj get(int i){
        return data.get(i);
    }

    public void setData(List<NBTObj> data) {
        this.data = data;
    }
    public void add(NBTObj jsonobj){
        data.add(jsonobj);
    }
    public int size(){
        return data.size();
    }

    @Override
    public @NotNull NBTType getType() {
        return NBTType.List;
    }

    @Override
    public @NotNull StringBuilder dump(StringBuilder builder, int level, String indent, boolean nextline) {
        nextline = data.size() > 0 && nextline;
        String thisindent = nextline?indent:"";
        String nl = nextline?"\n":"";
        builder.append("[").append(nl);
        for (int i = 0; i < data.size(); i++) {
            builder.append(thisindent.repeat(level+1));
            data.get(i).dump(builder,level+1, indent, nextline);
            builder.append(i<data.size()-1?",":"").append(nl);
        }
        return builder.append(thisindent.repeat(level)).append("]");
    }

    @Override
    public @NotNull JsonObj toJson() {
        return new JsonArray(data.stream().map(NBTObj::toJson).toList());
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @NotNull
    @Override
    public Iterator<NBTObj> iterator() {
        return data.iterator();
    }

    public TAG_List copy(){
        return new TAG_List(data.stream().map(NBTObj::copy).toList());
    }
}
