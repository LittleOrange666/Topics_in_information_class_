package net.orange.game.character;

import net.orange.game.data.object.Orb;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public enum WeaponType implements Predicate<Orb> {
    rapier("單手劍"),
    bigsword("雙手劍"),
    shield("盾"),
    ax("斧"),
    hammer("錘"),
    bow("弓"),
    crossbow("弩"),
    pistol("銃"),
    staff("杖"),
    whip("鞭"),
    gloves("拳套"),
    dagger("短劍"),
    book("書");

    public String getText() {
        return text;
    }

    private final String text;

    WeaponType(String text) {
        this.text = text;
    }

    @Override
    public boolean test(@NotNull Orb orb) {
        return orb.support(this);
    }
}
