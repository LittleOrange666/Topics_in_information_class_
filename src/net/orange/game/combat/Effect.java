package net.orange.game.combat;

import net.orange.game.Main;
import net.orange.game.character.GameCharacter;
import net.orange.game.display.UniqueObj;
import net.orange.game.tools.Depended;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Effect extends UniqueObj {
    public static final Effect donothing = new Effect(RemoveRule.never);
    public Effect(RemoveRule removeRule, int duration, int overlaylimit) {
        this.duration = (removeRule == RemoveRule.time?Main.fps:1)*duration;
        this.removeRule = removeRule;
        this.overlaylimit = overlaylimit;
    }
    public Effect(RemoveRule removeRule, int duration){
        this(removeRule, duration, 1);
    }
    public Effect(RemoveRule removeRule){
        this(removeRule, 1, 1);
    }

    public enum RemoveRule{
        never,time,attack,damage,tick
    }

    public int getDuration() {
        return duration;
    }

    public RemoveRule getRemoveRule() {
        return removeRule;
    }

    private final int duration;
    private final RemoveRule removeRule;
    private final ArrayList<InnerEffect> innerEffects = new ArrayList<>();

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    private long priority = 0;

    public int getOverlaylimit() {
        return overlaylimit;
    }

    private final int overlaylimit;

    public Depended getParent() {
        return parent;
    }

    public void setParent(Depended parent) {
        this.parent = parent;
    }

    private Depended parent = null;
    public void addModifier(Modifier modifier) {
        modifiers.add(modifier);
    }

    public List<Modifier> getModifiers() {
        return modifiers;
    }

    private final List<Modifier> modifiers = new ArrayList<>();
    private final Set<CharacterState> states = new HashSet<>();
    public void onTick(@NotNull GameCharacter character, AppliedEffect appliedEffect) {
        for(InnerEffect effect : innerEffects) effect.onTick(character, appliedEffect);
    }
    public void onDamage(@NotNull Damage damage, @NotNull GameCharacter character, AppliedEffect appliedEffect){
        for(InnerEffect effect : innerEffects) effect.onDamage(damage, character, appliedEffect);
    }
    public void onAttack(@NotNull Damage damage, @NotNull GameCharacter target, AppliedEffect appliedEffect){
        for(InnerEffect effect : innerEffects) effect.onAttack(damage, target, appliedEffect);
    }
    public void addEffect(InnerEffect effect){
        innerEffects.add(effect);
    }
    public void addState(CharacterState state){
        states.add(state);
    }
    public boolean hasState(CharacterState state){
        return states.contains(state);
    }
    public PassiveSkill toSkill(GameCharacter character){
        return toSkill(character, false);
    }
    public PassiveSkill toSkill(GameCharacter character, boolean main){
        PassiveSkill r = new PassiveSkill(character,this);
        r.setMain(main);
        return r;
    }
}
