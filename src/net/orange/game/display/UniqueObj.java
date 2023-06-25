package net.orange.game.display;

import java.util.Objects;
import java.util.UUID;

public class UniqueObj {
    private final UUID uuid;
    public UniqueObj(){
        uuid = UUID.randomUUID();
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UniqueObj uniqueObj)) return false;
        return Objects.equals(uuid, uniqueObj.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
