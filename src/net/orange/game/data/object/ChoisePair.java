package net.orange.game.data.object;

import net.orange.game.combat.AttributeType;
import net.orange.game.data.json.JsonArray;
import net.orange.game.data.json.JsonObj;
import net.orange.game.data.json.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ChoisePair(@Nullable Weapon weapon, @Nullable Orb orb) {
    public static final ChoisePair NULL = new ChoisePair(null, null);
    public static @NotNull ChoisePair noNull(@Nullable ChoisePair pair){
        return pair==null ? NULL : pair;
    }
    private static final String[] keys = new String[]{"character_type","cost","redploy","attack_area"};
    public JsonObject getData(){
        if (weapon==null) throw new IllegalArgumentException("you just can get data from a not-null weapon");
        JsonObject r = weapon.getData();
        if (orb != null){
            JsonObject o = orb.get(weapon.getType());
            JsonObject attrs_a = r.getObject("attributes");
            JsonObject attrs_b = o.getObject("attributes");
            if (attrs_a!=null && attrs_b!=null) {
                for (AttributeType type : AttributeType.values()) {
                    String name = type.toString();
                    if (attrs_a.has(name) && attrs_b.has(name)) {
                        attrs_a.put(name, attrs_a.getDouble(name) + attrs_b.getDouble(name));
                    }
                }
            }
            JsonArray skill_a = r.getArray("skills");
            JsonArray skill_b = o.getArray("skills");
            if (skill_a!=null&&skill_b!=null){
                skill_a.getData().addAll(0,skill_b.getData());
            }
            if (o.has("name")){
                r.put("orb_name",o.getString("name"));
            }
            for (String key : keys){
                if (o.has(key)){
                    r.put(key,o.get(key));
                }
            }
        }
        return r;
    }
    @Contract("_ -> new")
    public static @NotNull ChoisePair from_json(@NotNull JsonObject data){
        JsonObj weapon = data.get("weapon");
        JsonObj orb = data.get("orb");
        Weapon w = null;
        Orb o = null;
        if (weapon instanceof JsonObject obj){
            w = new Weapon(obj);
        }
        if (orb instanceof JsonObject obj){
            o = new Orb(obj);
        }
        return new ChoisePair(w,o);
    }
}
