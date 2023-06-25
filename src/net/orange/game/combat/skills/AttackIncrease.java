package net.orange.game.combat.skills;

import net.orange.game.character.GameCharacter;
import net.orange.game.combat.*;

public class AttackIncrease extends Skill{
    public static class AttackEffect extends Effect {
        public AttackEffect(double rate, int time) {
            super(RemoveRule.time,time);
            addModifier(new Modifier(AttributeType.attack,ModifierType.multiply,rate));
            setPriority(-1000000000000000000L);
        }
    }
    public AttackIncrease(GameCharacter parent, double spRequired, int time, double rate) {
        super(parent, spRequired, SpRecoverRule.time, new AttackEffect(rate,time));
        setAuto(false);
    }
}
