package net.orange.game.character;

import net.orange.game.Main;
import net.orange.game.combat.Effect;
import net.orange.game.combat.Modifier;
import net.orange.game.data.json.JsonObject;
import org.jetbrains.annotations.NotNull;

public class EnemyMessage {
    public JsonObject getData() {
        return data;
    }

    private final JsonObject data;

    public Effect getEffect() {
        return effect;
    }

    private final Effect effect = new Effect(Effect.RemoveRule.never);
    public EnemyMessage(String name, @NotNull JsonObject obj){
        data = Main.read("enemies/"+name);
        if (obj.has("modifiers")){
            for (JsonObject o : obj.getArray("modifiers").objects()){
                effect.addModifier(Modifier.from_json(o));
            }
        }
    }
}
