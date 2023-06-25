package net.orange.game.combat.skills;

import net.orange.game.Main;
import net.orange.game.character.GameCharacter;
import net.orange.game.combat.*;
import net.orange.game.display.Pos;
import org.jetbrains.annotations.NotNull;

public class SmallPush extends Skill {
    public static class PushEffect extends Effect {
        private final double power;

        public PushEffect(double power) {
            super(RemoveRule.attack);
            this.power = power;
            setPriority(-1000000000000000000L);
            addModifier(new Modifier(AttributeType.attack_count,ModifierType.final_add,10000));
        }

        @Override
        public void onAttack(@NotNull Damage damage, @NotNull GameCharacter target, AppliedEffect appliedEffect) {
            Main.log("pushing");
            Pos facing = target.center().sub(damage.getSource().center()).unit();
            target.push(facing.mul(power));
        }
    }
    public SmallPush(GameCharacter parent, double spRequired, double power) {
        super(parent, spRequired, SpRecoverRule.time, new PushEffect(power));
    }
}
