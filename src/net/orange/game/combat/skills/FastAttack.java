package net.orange.game.combat.skills;

import net.orange.game.character.GameCharacter;
import net.orange.game.combat.*;

public class FastAttack extends Skill {
    public FastAttack(GameCharacter parent, double spRequired, int time, double speed, double attack) {
        super(parent, spRequired, SpRecoverRule.time, new Effect(Effect.RemoveRule.time, time));
        getEffect().addModifier(new Modifier(AttributeType.attack, ModifierType.multiply,attack));
        getEffect().addModifier(new Modifier(AttributeType.attack_speed, ModifierType.add,speed));
        setAuto(false);
    }
}
