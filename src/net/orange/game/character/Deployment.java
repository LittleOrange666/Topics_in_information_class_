package net.orange.game.character;

import net.orange.game.Main;
import net.orange.game.display.Picture;
import net.orange.game.display.Pos;
import net.orange.game.tools.DrawTool;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.function.Predicate;

public class Deployment implements Comparable<Deployment>{
    public Picture getLabel() {
        return label;
    }

    private final Picture label;
    private final AllyCharacter character;

    public int getCost() {
        return cost;
    }

    private final int cost;

    public Predicate<Plate> getCondition() {
        return condition;
    }

    private final Predicate<Plate> condition;

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    private int cooldown = 0;

    public Deployment(String label, @NotNull AllyCharacter character, int cost, Predicate<Plate> condition) {
        this.label = new Picture(label);
        this.character = character;
        this.cost = cost;
        this.condition = condition;
    }

    public AllyCharacter getCharacter() {
        return character;
    }

    @Override
    public int compareTo(@NotNull Deployment o) {
        return cost-o.cost;
    }

    public void paint(Pos pos, Graphics2D g){
        label.paint(pos, g);
        Pos p = pos.add(Main.deployment_size-30,Main.deployment_size);
        DrawTool.drawString(g, p,String.valueOf(cost),Color.BLACK,Main.numberFont(30));
        p = pos.add((double) Main.deployment_size /2-30, (double) Main.deployment_size /2);
        if (cooldown>0){
            int i = cooldown*10/Main.fps;
            DrawTool.drawString(g, p,(i/10)+"."+(i%10),Color.RED,Main.numberFont(30));
        }
    }
}
