package net.orange.game.character;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public enum Element {
    metal(1, "金", new Color(255,204, 51)),
    water(2, "水", new Color(51,204,255)),
    wood(3, "木", new Color(0 ,255, 51)),
    fire(4, "火", new Color(255,0,0)),
    earth(5, "土", new Color(153,102, 0)),
    none(0, "無", new Color(255,255,255));
    private final int order;

    public String getName() {
        return name;
    }

    private final String name;

    public Color getColor() {
        return color;
    }

    private final Color color;

    Element(int order, String name, Color color) {
        this.order = order;
        this.name = name;
        this.color = color;
    }
    @Contract(pure = true)
    public Relationship getRelationship(@NotNull Element other){
        if (this==none||other==none) return Relationship.none;
        int i = (5+other.order-order)%5;
        if (i == 0) return Relationship.equal;
        else if ((i&1)==1) return Relationship.restrained;
        else return Relationship.restraint;
    }
    public enum Relationship{
        equal,restraint,restrained,none
    }
}
