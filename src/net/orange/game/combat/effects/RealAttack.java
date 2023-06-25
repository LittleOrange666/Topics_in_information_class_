package net.orange.game.combat.effects;

import net.orange.game.character.GameCharacter;
import net.orange.game.combat.AppliedEffect;
import net.orange.game.combat.Damage;
import net.orange.game.combat.Effect;
import net.orange.game.combat.InnerEffect;
import org.jetbrains.annotations.NotNull;

public class RealAttack extends Effect {
    public static final RealAttack object = new RealAttack();
    public RealAttack() {
        super(Effect.RemoveRule.never);
        addEffect(Inner.instance);
        setPriority(1000000000000000000L);
    }
    public static class Inner extends InnerEffect {
        public static final HealAttack.Inner instance = new HealAttack.Inner();
        @Override
        public void onAttack(@NotNull Damage damage, @NotNull GameCharacter target, AppliedEffect appliedEffect) {
            damage.setType(Damage.Type.real);
        }
    }
}
