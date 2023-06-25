package net.orange.game.combat.skills;

import net.orange.game.character.GameCharacter;
import net.orange.game.combat.AppliedEffect;
import net.orange.game.combat.Damage;
import net.orange.game.combat.Effect;
import net.orange.game.combat.Skill;
import org.jetbrains.annotations.NotNull;

public class StrongAttack extends Skill {
    public static class AttackEffect extends Effect {
        private final double rate;
        public AttackEffect(double rate) {
            super(RemoveRule.attack);
            this.rate = rate;
            setPriority(-1000000000000000000L);
        }

        @Override
        public void onAttack(@NotNull Damage damage, @NotNull GameCharacter target, AppliedEffect appliedEffect) {
            damage.setValue(damage.getValue()*rate);
        }
    }
    public StrongAttack(GameCharacter parent, double spRequired, double rate) {
        super(parent, spRequired, SpRecoverRule.onattack, new AttackEffect(rate));
    }
}
