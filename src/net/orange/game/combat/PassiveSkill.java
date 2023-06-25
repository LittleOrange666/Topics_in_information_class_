package net.orange.game.combat;

import net.orange.game.character.GameCharacter;

public class PassiveSkill extends Skill{
    public PassiveSkill(GameCharacter parent, Effect effect) {
        super(parent, 1, SpRecoverRule.passive, effect);
    }
}
