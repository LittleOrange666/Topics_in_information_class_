package net.orange.game.data.object;

import net.orange.game.Main;
import net.orange.game.data.json.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserData {
    public JsonObject getData() {
        return data;
    }

    private JsonObject data;
    public UserData(){
        data = new JsonObject();
        data.setPath("\"user data\"");
        init();
    }
    public void read(String name){
        if (Main.exists("user/"+name)) {
            data = Main.read("user/"+name);
        }else{
            data = new JsonObject();
            data.setPath("\"user data\"");
        }
        init();
    }
    public void readonline(String name){
        if (Main.dataManager.hasFile(name)) {
            data = Main.dataManager.download(name);
        }else{
            data = new JsonObject();
            data.setPath("\"user data\"");
        }
        init();
    }
    public void write(String name){
        Main.write("user/"+name,data);
    }
    public void writeonline(String name){
        Main.dataManager.upload(name,data);
    }
    public void init(){
        for (String key : new String[]{"weapons","orbs","completed","full_completed","formation"}){
            if (!data.has(key)){
                data.put(key,new JsonArray());
            }
        }
        if (!data.has("accessible")){
            data.put("accessible",new JsonArray());
            JsonArray accessible = data.getArray("accessible");
            accessible.add("0-1");
        }
    }
    public ArrayList<Weapon> getWeapons(){
        ArrayList<Weapon> weapons = new ArrayList<>();
        for(JsonObject o : data.getArray("weapons").objects()){
            weapons.add(new Weapon(o));
        }
        return weapons;
    }
    public void addWeapon(String name){
        data.getArray("weapons").add(new JsonObject(
                new JsonTag("name",name),
                new JsonTag("level",1)
        ));
    }
    public void addOrb(String name){
        JsonObject orb = new JsonObject(
                new JsonTag("name",name)
        );
        data.getArray("orbs").add(orb);
    }
    public ArrayList<Orb> getOrbs(){
        ArrayList<Orb> orbs = new ArrayList<>();
        for(JsonObject o : data.getArray("orbs").objects()){
            orbs.add(new Orb(o));
        }
        return orbs;
    }
    public ArrayList<ChoisePair> getFormation(){
        ArrayList<ChoisePair> formation = new ArrayList<>();
        for(JsonObj o : data.getArray("formation")){
            if (o instanceof JsonObject obj){
                Weapon weapon = null;
                Orb orb = null;
                if (obj.get("weapon") instanceof JsonString s){
                    for(JsonObject w : data.getArray("weapons").objects()){
                        if (w.getString("name").equals(s.getString())){
                            weapon = new Weapon(w);
                            break;
                        }
                    }
                }
                if (obj.get("orb") instanceof JsonString s){
                    for(JsonObject w : data.getArray("orbs").objects()){
                        if (w.getString("name").equals(s.getString())){
                            orb = new Orb(w);
                            break;
                        }
                    }
                }
                formation.add(new ChoisePair(weapon,orb));
            }else{
                formation.add(null);
            }
        }
        return formation;
    }
    public void setFormation(@NotNull ArrayList<ChoisePair> formation){
        JsonArray array = new JsonArray();
        for(ChoisePair o : formation){
            if (o == null){
                array.add(new JsonNull());
            }else{
                array.add(new JsonObject(
                        new JsonTag("weapon", o.weapon()==null?new JsonNull():new JsonString(o.weapon().getId())),
                        new JsonTag("orb", o.orb()==null?new JsonNull():new JsonString(o.orb().getId()))
                ));
            }
        }
        data.put("formation",array);
    }
    public boolean completed(String level){
        return data.getArray("completed").contains(level);
    }
    public boolean accessible(String level){
        return data.getArray("accessible").contains(level);
    }
    public void unlock(String level){
        data.getArray("accessible").add(level);
    }

    public void complete(String level) {
        data.getArray("completed").add(level);
    }
    public boolean full_completed(String level){
        return data.getArray("full_completed").contains(level);
    }

    public void full_complete(String level) {
        data.getArray("full_completed").add(level);
    }
}
