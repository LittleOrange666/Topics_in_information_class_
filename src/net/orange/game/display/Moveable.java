package net.orange.game.display;

import net.orange.game.tools.Scaler;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Moveable extends UniqueObj {
    public Pos getPos() {
        return offsetGroup==null?pos:pos.add(offsetGroup.getOffset());
    }

    public void setPos(Pos pos) {
        this.pos = pos;
    }

    public Pos getSize() {
        return size;
    }

    public void setSize(Pos size) {
        this.size = size;
    }

    private Pos pos;
    private Pos size;

    public void setOffsetGroup(OffsetGroup offsetGroup) {
        this.offsetGroup = offsetGroup;
    }

    private OffsetGroup offsetGroup = null;
    public Moveable(Pos pos, Pos size){
        super();
        this.pos = pos;
        this.size = size;
    }
    public Moveable(Pos pos){
        super();
        this.pos = pos;
        this.size = new Pos();
    }
    public Pos displayPos(){
        return Scaler.scale(getPos());
    }
    public Pos displaySize(){
        return Scaler.scaleSize(getSize());
    }
    public Pos center(){
        return getPos().add(getSize().mul(0.5));
    }
    public boolean collide(@NotNull Moveable other){
        return getPos().x() + getSize().x() >= other.getPos().x() && getPos().y() + getSize().y() >= other.getPos().y() &&
                getPos().x() <= other.getPos().x()+other.getSize().x() && getPos().y() <= other.getPos().y()+other.getSize().y();
    }
    public boolean include(@NotNull Moveable other){
        return getPos().x() <= other.getPos().x() && getPos().x() + getSize().x() >= other.getPos().x() + other.getSize().x() &&
                getPos().y() <= other.getPos().y() && getPos().y() + getSize().y() >= other.getPos().y() + other.getSize().y();
    }
    public boolean include(@NotNull Pos other){
        return getPos().x() <= other.x() && getPos().x() + getSize().x() >= other.x() &&
                getPos().y() <= other.y() && getPos().y() + getSize().y() >= other.y();
    }
    public void fillRect(@NotNull Graphics2D g){
        BlockPos pos = displayPos().toBlockPos();
        BlockPos size = displaySize().toBlockPos();
        g.fillRect(pos.x(),pos.y(),size.x(),size.y());
    }
}
