package net.orange.game.combat;

import net.orange.game.character.GameCharacter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AttackEffect extends Effect{
    private static class SuddenEffect extends Effect{
        private final AttackEffect parent;

        public SuddenEffect(AttackEffect parent) {
            super(RemoveRule.damage);
            this.parent = parent;
        }

        @Override
        public void onDamage(@NotNull Damage damage, @NotNull GameCharacter character, AppliedEffect appliedEffect) {
            parent.action(character);
        }

        @Override
        public List<Modifier> getModifiers() {
            return parent.getInnerModifiers();
        }
    }

    public List<Modifier> getInnerModifiers() {
        return inner_modifiers;
    }

    public void setInnerModifiers(List<Modifier> modifiers) {
        this.inner_modifiers = modifiers;
    }

    private List<Modifier> inner_modifiers = new ArrayList<>();
    public AttackEffect() {
        super(RemoveRule.never);
    }

    public AttackEffect(RemoveRule removeRule, int duration, int overlaylimit) {
        super(removeRule, duration, overlaylimit);
    }

    public AttackEffect(RemoveRule removeRule, int duration) {
        super(removeRule, duration);
    }

    public AttackEffect(RemoveRule removeRule) {
        super(removeRule);
    }
    public void action(GameCharacter target){
        if (func!=null){
            func.accept(target);
        }
    }
    private Consumer<GameCharacter> func = null;
    public void connect(Consumer<GameCharacter> func) {
        this.func = func;
    }

    @Override
    public void onAttack(@NotNull Damage damage, @NotNull GameCharacter target, AppliedEffect appliedEffect) {
        target.addEffect(new SuddenEffect(this));
    }
}
