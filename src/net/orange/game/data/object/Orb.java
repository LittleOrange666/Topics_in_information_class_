package net.orange.game.data.object;

import net.orange.game.Main;
import net.orange.game.character.WeaponType;
import net.orange.game.data.json.JsonObject;
import net.orange.game.display.Picture;
import net.orange.game.page.Page;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;

public class Orb {
    public JsonObject getData() {
        return data;
    }

    private final JsonObject data;
    private final HashSet<WeaponType> weaponTypes;

    private final String name;

    public String getId() {
        return id;
    }

    private final String id;

    public Orb(JsonObject discription){
        String name = discription.getString("name");
        id = name;
        data = Main.read("orbs/" + name);
        weaponTypes = new HashSet<>();
        for(WeaponType weaponType : WeaponType.values()){
            if (data.has(weaponType.toString())){
                weaponTypes.add(weaponType);
            }
        }
        icon = new Picture(data.getObject("textures").getString("icon"));
        this.name = data.getString("name");
    }
    public boolean support(WeaponType weaponType){
        return weaponTypes.contains(weaponType);
    }
    public JsonObject get(@NotNull WeaponType type){
        return data.getObject(type.toString());
    }

    public Picture getIcon() {
        return icon;
    }

    private final Picture icon;

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Orb orb = (Orb) o;

        return Objects.equals(name, orb.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
