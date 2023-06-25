package net.orange.game.combat;

import net.orange.game.data.json.JsonObject;
import net.orange.game.display.UniqueObj;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Modifier extends UniqueObj {
    private final AttributeType attribute;
    private final ModifierType type;

    public AttributeType getAttribute() {
        return attribute;
    }

    public ModifierType getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    private final double value;

    public Modifier(AttributeType attribute, ModifierType type, double value) {
        this.attribute = attribute;
        this.type = type;
        this.value = value;
    }
    public Modifier multiply(int m){
        return new Modifier(attribute, type, type==ModifierType.final_multiply?Math.pow(value,m):value*m);
    }
    @Contract("_ -> new")
    public static @NotNull Modifier from_json(@NotNull JsonObject obj){
        return new Modifier(AttributeType.valueOf(obj.getString("attribute")),
                ModifierType.valueOf(obj.getString("type")),
                obj.getDouble("value"));
    }
}
