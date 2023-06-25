package net.orange.game.data.object;

import net.orange.game.Main;
import net.orange.game.data.json.*;
import net.orange.game.page.TitlePage;

import java.util.ArrayList;
import java.util.UUID;

public class UserDataList {
    private static final String path = "userlist";
    private final JsonObject data;
    private JsonArray onlines = new JsonArray();
    public UserDataList(){
        if (Main.exists(path)){
            data = Main.read(path);
        }else{
            data = new JsonObject();
            data.setPath("\"userlist\"");
        }
        if (!data.has("offlines")){
            data.put("offlines", new JsonArray());
        }
        if (!data.has("current")){
            data.put("current", new JsonNull());
        }
        UserDataPtr o = getCurrent();
        if (o!=null && o.isonline()) {
            //select(null);
        }
    }
    public void addoffline(String name){
        UUID uuid = UUID.randomUUID();
        data.getArray("offlines").add(new JsonObject(
                new JsonTag("uuid", uuid.toString()),
                new JsonTag("name", name)
        ));
    }
    public void addonline(String name){
        UUID uuid = UUID.randomUUID();
        onlines.add(new JsonObject(
                new JsonTag("uuid",uuid.toString()),
                new JsonTag("name", name)
        ));
        Main.dataManager.upload("userlist",new JsonObject(
                new JsonTag("data", onlines)
        ));
    }
    public ArrayList<UserDataPtr> getOfflines(){
        ArrayList<UserDataPtr> r = new ArrayList<>();
        for(JsonObj o : data.getArray("offlines")){
            if (o instanceof JsonObject obj){
                r.add(new UserDataPtr(false,obj.getString("uuid"),obj.getString("name")));
            }
        }
        return r;
    }
    public ArrayList<UserDataPtr> getOnlines(){
        ArrayList<UserDataPtr> r = new ArrayList<>();
        for(JsonObj o : onlines){
            if (o instanceof JsonObject obj){
                r.add(new UserDataPtr(true,obj.getString("uuid"),obj.getString("name")));
            }
        }
        return r;
    }
    public void complete(){
        if (getCurrent() != null){
            Main.mainWindow.changePage(new TitlePage());
        }
    }
    public UserDataPtr getCurrent(){
        JsonObj o = data.get("current");
        if (o instanceof JsonObject obj){
            return UserDataPtr.from_json(obj);
        }else{
            return null;
        }
    }
    public void select(UserDataPtr o){
        if (o == null){
            data.put("current", new JsonNull());
        }else{
            data.put("current",o.to_json());
        }
    }
    public void read(){
        UserDataPtr o = getCurrent();
        if (o.isonline()){
            Main.userData.readonline(o.id());
        }else{
            Main.userData.read(o.id());
        }
    }
    public void save(){
        Main.write(path, data);
        UserDataPtr o = getCurrent();
        if (o!=null){
            if (o.isonline()){
                Main.userData.writeonline(o.id());
            }else{
                Main.userData.write(o.id());
            }
        }
    }
    public void onlogin(){
        if (Main.dataManager.hasFile("userlist")){
            onlines = Main.dataManager.download("userlist").getArray("data");
        }
    }
    public void onlogout(){
        UserDataPtr o = getCurrent();
        if (o!=null && o.isonline()){
            select(null);
        }
    }
}
