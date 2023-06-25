package net.orange.game.data.object;

import net.orange.game.Main;
import net.orange.game.character.WeaponType;
import net.orange.game.combat.*;
import net.orange.game.data.json.JsonArray;
import net.orange.game.data.json.JsonObject;
import net.orange.game.display.Picture;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class Weapon {
    public JsonObject getData() {
        return data;
    }

    private final JsonObject data;

    public WeaponType getType() {
        return type;
    }

    private final WeaponType type;

    public Picture getIcon() {
        return icon;
    }

    private final Picture icon;

    public String getName() {
        return name;
    }

    private final String name;

    public int getLevel() {
        return level;
    }

    private final int level;

    public boolean isMain() {
        return main;
    }

    private final boolean main;

    public String getId() {
        return id;
    }

    private final String id;
    public Weapon(@NotNull JsonObject discription){
        String name = discription.getString("name");
        id = name;
        if (discription.has("level")) {
            level = discription.getInt("level");
        }else{
            level = 1;
        }
        data = Main.read("weapons/"+name);
        data.put("level", level);
        main = data.getBoolean("main");
        if (data.has("level_attributes")){
            int mul = level-1;
            JsonObject levelAttr = data.getObject("level_attributes");
            JsonObject attrs = data.getObject("attributes");
            for(AttributeType attr : AttributeType.values()){
                if (levelAttr.has(attr.name())&&attrs.has(attr.name())){
                    attrs.put(attr.name(),attrs.getDouble(attr.name())+mul*levelAttr.getDouble(attr.name()));
                }
            }
        }
        type = WeaponType.valueOf(data.getString("type"));
        icon = new Picture(data.getObject("textures").getString("icon"));
        this.name = data.getString("name");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Weapon weapon = (Weapon) o;

        return Objects.equals(name, weapon.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
