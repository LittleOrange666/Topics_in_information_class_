package net.orange.game.display;

public class OffsetGroup {
    public Pos getOffset() {
        return offset;
    }

    public void setOffset(Pos offset) {
        this.offset = offset;
    }

    private Pos offset = Pos.zero;
}
