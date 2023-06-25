package net.orange.game.combat;

import net.orange.game.character.GameCharacter;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public enum CharacterState implements Predicate<GameCharacter> {
    friend_target,healer,no_healing,can_attack_high,undead,invincible;
    private final Effect effect;
    {
        effect = new Effect(Effect.RemoveRule.never);
        effect.addState(this);
    }

    @Override
    public boolean test(@NotNull GameCharacter character) {
        return character.hasState(this);
    }
    public Effect toEffect(){
        return effect;
    }
}
