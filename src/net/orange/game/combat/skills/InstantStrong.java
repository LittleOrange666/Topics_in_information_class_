package net.orange.game.combat.skills;

import net.orange.game.character.GameCharacter;
import net.orange.game.combat.*;

public class InstantStrong extends PassiveSkill {
    public static class StrongEffect extends Effect {

        public StrongEffect(int duration, double mul) {
            super(RemoveRule.time, duration);
            addModifier(new Modifier(AttributeType.attack, ModifierType.multiply, mul));
            setPriority(-1000000000000000000L);
        }
    }
    public InstantStrong(GameCharacter parent, int duration, double mul) {
        super(parent, new StrongEffect(duration, mul));
    }
}
