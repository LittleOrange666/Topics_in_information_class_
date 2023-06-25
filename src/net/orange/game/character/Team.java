package net.orange.game.character;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public enum Team implements Predicate<GameCharacter> {
    ally,enemy,neutral;

    @Override
    public boolean test(@NotNull GameCharacter character) {
        return character.getTeam() == this;
    }
    public boolean opposite(@NotNull GameCharacter character) {
        return opposite(character.getTeam());
    }
    public boolean opposite(@NotNull Team team) {
        switch (this){
            case ally -> {
                return team == enemy;
            }
            case enemy -> {
                return team == ally;
            }
            default -> {
                return false;
            }
        }
    }
}
