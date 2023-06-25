package net.orange.game.combat.effects;

import net.orange.game.character.GameCharacter;
import net.orange.game.combat.*;
import org.jetbrains.annotations.NotNull;

public class HealAttack extends Effect {
    public static final HealAttack object = new HealAttack();
    public HealAttack() {
        super(RemoveRule.never);
        addEffect(Inner.instance);
        addState(CharacterState.friend_target);
        addState(CharacterState.healer);
        setPriority(1000000000000000000L);
    }
    public static class Inner extends InnerEffect {
        public static final Inner instance = new Inner();
        @Override
        public void onAttack(@NotNull Damage damage, @NotNull GameCharacter target, AppliedEffect appliedEffect) {
            damage.setType(Damage.Type.heal);
        }
    }
}
