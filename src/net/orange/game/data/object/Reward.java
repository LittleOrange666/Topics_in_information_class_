package net.orange.game.data.object;

import net.orange.game.Main;
import net.orange.game.data.json.JsonObject;
import net.orange.game.data.json.JsonTag;
import net.orange.game.display.Picture;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Reward {
    public Reward(@NotNull JsonObject data) {
        this.data = data;
        Picture default_icon = Picture.rect(2,2, Color.WHITE);
        switch (data.getString("type")){
            case "weapon"->{
                String value = data.getString("value");
                JsonObject discription = new JsonObject(new JsonTag("name",value));
                icon = new Weapon(discription).getIcon();
                display = true;
            }
            case "orb"->{
                String value = data.getString("value");
                JsonObject discription = new JsonObject(new JsonTag("name",value));
                icon = new Orb(discription).getIcon();
                display = true;
            }
            case "level"->{
                String value = data.getString("value");
                icon = default_icon;
                display = false;
            }
            default -> {
                icon = default_icon;
                display = false;
            }
        }
    }

    public JsonObject getData() {
        return data;
    }

    private final JsonObject data;

    public boolean isDisplay() {
        return display;
    }

    private final boolean display;

    public Picture getIcon() {
        return icon;
    }

    private final Picture icon;
    public void apply(){
        UserData userData = Main.userData;
        switch (data.getString("type")){
            case "weapon"->{
                String value = data.getString("value");
                userData.addWeapon(value);
            }
            case "orb"->{
                String value = data.getString("value");
                userData.addOrb(value);
            }
            case "level"->{
                String value = data.getString("value");
                userData.unlock(value);
            }
        }
    }
}
