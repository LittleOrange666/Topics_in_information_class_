package net.orange.game.data.nbt;

import net.orange.game.data.json.JsonObj;
import org.jetbrains.annotations.NotNull;

public abstract class NBTObj{
    @NotNull public abstract NBTType getType();
    @NotNull public String dump(int level, String indent, boolean nextline){
        return dump(new StringBuilder(), level, indent, nextline).toString();
    }
    @NotNull public abstract StringBuilder dump(StringBuilder builder, int level, String indent, boolean nextline);

    @NotNull
    public String dump(String indent, boolean nextline) {
        return dump(0,indent,nextline);
    }
    @NotNull
    public String dump(boolean nextline) {
        return dump(0,"    ",nextline);
    }
    @NotNull
    public String dump(int indent, boolean nextline) {
        return dump(0," ".repeat(indent),nextline);
    }
    @NotNull
    public abstract JsonObj toJson();
    public static NBTObj copy(NBTObj obj){
        if (obj instanceof TAG_Byte tag){
            return tag.copy();
        }else if (obj instanceof TAG_Byte_Array tag){
            return tag.copy();
        }else if (obj instanceof TAG_Int tag){
            return tag.copy();
        }else if (obj instanceof TAG_Int_Array tag){
            return tag.copy();
        }else if (obj instanceof TAG_Long tag){
            return tag.copy();
        }else if (obj instanceof TAG_Long_Array tag){
            return tag.copy();
        }else if (obj instanceof TAG_Compound tag){
            return tag.copy();
        }else if (obj instanceof TAG_Double tag){
            return tag.copy();
        }else if (obj instanceof TAG_Float tag){
            return tag.copy();
        }else if (obj instanceof TAG_List tag){
            return tag.copy();
        }else if (obj instanceof TAG_Short tag){
            return tag.copy();
        }else if (obj instanceof TAG_String tag){
            return tag.copy();
        }else {
            throw new IllegalArgumentException("not copyable nbtobj");
        }
    }
}
