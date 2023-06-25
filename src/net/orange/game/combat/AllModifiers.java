package net.orange.game.combat;

import net.orange.game.character.GameCharacter;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class AllModifiers implements Iterable<Modifier>{
    private final GameCharacter parent;

    public AllModifiers(GameCharacter parent) {
        this.parent = parent;
    }
    private static class It implements Iterator<Modifier>{
        private final Iterator<AppliedEffect> it;
        private Iterator<Modifier> cur = null;
        private int mul;
        private void prepare(){
            if (cur == null || !cur.hasNext()) {
                while (it.hasNext()) {
                    AppliedEffect appliedEffect = it.next();
                    mul = appliedEffect.getOverlay_count();
                    cur = appliedEffect.getModifiers().iterator();
                    if (cur.hasNext()) break;
                }
                if (cur != null && !cur.hasNext()) cur = null;
            }
        }

        private It(@NotNull GameCharacter parent) {
            it = parent.getEffects().iterator();
            prepare();
        }

        @Override
        public boolean hasNext() {
            return cur != null;
        }

        @Override
        public Modifier next() {
            if (!hasNext()) throw new NoSuchElementException();
            Modifier r = cur.next().multiply(mul);
            prepare();
            return r;
        }
    }

    @NotNull
    @Override
    public Iterator<Modifier> iterator() {
        return new It(parent);
    }
}
