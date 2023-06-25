package net.orange.game.character;

import net.orange.game.Main;
import net.orange.game.display.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashSet;
import java.util.function.Predicate;

public class Plate extends Moveable implements Paintable {
    private static final Picture low_picture = new Picture("surface/grassplate0.png");
    private static final Picture high_picture = new Picture("surface/concreteplate0.png");
    private static final Picture cross_picture = Picture.fromText("×",Main.numberFont(40),Color.red);
    private static final Picture canplace = new Picture("surface/canplace.png");
    private static final Pos SIZE = new Pos(Main.block_size, Main.block_size);

    public BlockPos getBlockPos() {
        return blockPos;
    }

    private final BlockPos blockPos;

    public GameCharacter getStand() {
        return stand;
    }

    public void setStand(GameCharacter stand) {
        this.stand = stand;
    }

    private GameCharacter stand = null;
    public Plate(Pos pos, BlockPos blockPos) {
        super(pos,SIZE);
        this.blockPos = blockPos;
    }

    @Override
    public void paint(Graphics2D g) {
        if (hasFlag(Flag.high)){
            high_picture.paint(getPos(),g);
        }else if(hasFlag(Flag.ground)){
            low_picture.paint(getPos(),g);
        }else if(hasFlag(Flag.hole)){
            g.setColor(Color.BLACK);
            fillRect(g);
        }
        if (hasFlag(Flag.unplaceable)){
            cross_picture.paintCenter(center(),g);
        }
        g.setStroke(new BasicStroke(3));
        Pos pos = displayPos().add(1,1);
        Pos size = displaySize().sub(4,4);
        if (flags.contains(Flag.red)){
            g.setColor(Color.RED);
            g.drawRect((int) pos.x(), (int) pos.y(), (int) size.x(), (int) size.y());
        }
        if (flags.contains(Flag.blue)){
            g.setColor(Color.CYAN);
            g.drawRect((int) pos.x(), (int) pos.y(), (int) size.x(), (int) size.y());
        }
        if (flags.contains(Flag.canplace)){
            canplace.paint(getPos(),g);
        }
    }
    private final HashSet<Flag> flags = new HashSet<>();
    public boolean hasFlag(Flag flag){
        return flags.contains(flag);
    }
    public void addFlag(Flag flag){
        flags.add(flag);
    }
    public void removeFlag(Flag flag){
        flags.remove(flag);
    }

    public enum Flag implements Predicate<Plate> {
        red,blue,canplace,hole,high,ground,showarea, unplaceable;

        @Override
        public boolean test(@NotNull Plate plate) {
            return plate.hasFlag(this);
        }
    }
}
