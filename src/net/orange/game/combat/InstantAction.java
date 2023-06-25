package net.orange.game.combat;

import net.orange.game.character.GameCharacter;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class InstantAction extends Effect{
    private final Consumer<GameCharacter> func;
    public InstantAction(Consumer<GameCharacter> func) {
        super(RemoveRule.tick);
        this.func = func;
    }
    @Override
    public void onTick(@NotNull GameCharacter character, @NotNull AppliedEffect appliedEffect) {
        func.accept(character);
    }
}
