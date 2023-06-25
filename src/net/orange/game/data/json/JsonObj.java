package net.orange.game.data.json;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class JsonObj {
    @NotNull public abstract JsonType getType();
    @NotNull public String dump(int level, String indent, boolean nextline){
        return dump(new StringBuilder(), level, indent, nextline).toString();
    }
    public abstract void write(@NotNull DataOutputStream out) throws IOException;
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
    public String dump() {
        return dump(true);
    }
    @NotNull
    public String dump(int indent, boolean nextline) {
        return dump(0," ".repeat(indent),nextline);
    }
    private JsonObj parent = null;
    private String path = "";
    public String getPath() {
        return parent==null?path:parent.getPath()+path;
    }
    public void setPath(String path){
        this.path = path;
    }
    public void setPath(String path, JsonObj parent){
        this.path = path;
        this.parent = parent;
    }
    public JsonObj root(){
        return parent == null ? null : parent.root();
    }

    @Override
    public String toString() {
        return dump(true);
    }
    public abstract @Nullable JsonObj read(@NotNull String path);
    public abstract void write(@NotNull String path, JsonObj value);
}
