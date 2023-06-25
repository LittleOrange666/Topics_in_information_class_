package net.orange.game.combat;

import net.orange.game.character.Element;
import net.orange.game.character.GameCharacter;

import java.util.ArrayList;
import java.util.List;

public class Damage {
    public Damage(double value, Type type, GameCharacter source) {
        this.value = value;
        this.type = type;
        this.source = source;
        this.effects = new ArrayList<>();
    }
    public void addEffect(Effect effect) {
        effects.add(effect);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    private double value;
    private Type type;

    private GameCharacter source;

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    private Element element = Element.none;

    public List<Effect> getEffects() {
        return effects;
    }

    private final List<Effect> effects;

    public GameCharacter getSource() {
        return source;
    }

    public void setSource(GameCharacter source) {
        this.source = source;
    }

    public enum Type {
        physics, magic, real, heal
    }
}
