package net.orange.game;

import net.orange.game.data.NetworkDataManager;
import net.orange.game.data.json.JsonObject;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class DriveTest {
    public static void main(String[] args) throws IOException, GeneralSecurityException {
        NetworkDataManager data = new NetworkDataManager();
        // data.upload("default.json","resources\\data_test\\user\\default.json");
        //data.list();
        //data.download(data.findID("default.json"),"resources\\data_test\\user\\default_.json");

        //JsonObject object = new JsonObject();
        //object.put("test",77771449);
        //data.upload("test",object);

        //data.list();
        //JsonObject object = data.download("test");
        //System.out.println(object);
    }
}
