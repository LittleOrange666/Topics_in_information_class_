package net.orange.game.game;

import net.orange.game.Main;
import net.orange.game.character.Plate;
import net.orange.game.data.json.JsonArray;
import net.orange.game.display.BlockPos;
import net.orange.game.display.Pos;
import net.orange.game.page.GamePage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class GameLayout implements Iterable<Plate>{
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private final GamePage parent;

    private final int width;
    private final int height;
    private final int blockcount;
    private final Plate[][] data;
    private final double[][] distance;
    private final boolean[][] connected;
    private final boolean[] walkable;
    private final BlockPos[][] target;
    private final BlockIterable blocks;

    public GameLayout(GamePage parent, int width, int height) {
        this.parent = parent;
        this.width = width;
        this.height = height;
        data = new Plate[width][height];
        blockcount = width*height;
        walkable = new boolean[blockcount];
        distance = new double[blockcount][blockcount];
        connected = new boolean[blockcount][blockcount];
        target = new BlockPos[blockcount][blockcount];
        blocks = new BlockIterable(this);
    }
    public Pos initPlates(JsonArray map){
        double dx = (double) (Main.default_width - width * Main.block_size) /2;
        double dy = (double) (Main.default_height - height * Main.block_size) /2;
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++) {
                int dat = map.getArray(i).getInt(j);
                Plate plate = new Plate(new Pos(dx+j*Main.block_size,dy+i*Main.block_size), new BlockPos(j,i));
                switch (dat){
                    case 0->plate.addFlag(Plate.Flag.ground);
                    case 1->plate.addFlag(Plate.Flag.high);
                    case 2->plate.addFlag(Plate.Flag.hole);
                    case 3->{
                        plate.addFlag(Plate.Flag.ground);
                        plate.addFlag(Plate.Flag.unplaceable);
                    }
                    case 4->{
                        plate.addFlag(Plate.Flag.high);
                        plate.addFlag(Plate.Flag.unplaceable);
                    }
                }
                setPlate(j,i,plate);
            }
        }
        return new Pos(dx,dy);
    }
    public void init(){
        for (int i = 0; i < blockcount; i++) {
            Arrays.fill(distance[i], 10000);
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                walkable[x+y*width] = data[x][y].hasFlag(Plate.Flag.ground);
            }
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                target[x][y] = new BlockPos(x,y);
            }
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int x0 = 0; x0 < width; x0++) {
                    for (int y0 = 0; y0 < height; y0++) {
                        boolean sus = true;
                        /*
                        for(int i = Math.min(x,x0);i<=Math.max(x,x0);i++){
                            for (int j = Math.min(y,y0); j<=Math.max(y,y0);j++){
                                if (!walkable[i + j * width]) {
                                    sus = false;
                                    break;
                                }
                            }
                            if (!sus) break;
                        }
                        */
                        Pos p = parent.getBlockCenter(new BlockPos(x,y));
                        Pos p0 = parent.getBlockCenter(new BlockPos(x0,y0));
                        if(Math.abs(x-x0)>Math.abs(y-y0)){
                            for(double i = Math.min(p.x(),p0.x());i<=Math.max(p.x(),p0.x());i++) {
                                double m = (i-Math.min(p.x(),p0.x()))/Math.abs(p.x()-p0.x());
                                Pos cur = p0.sub(p).mul(m).add(p);
                                if (!isWalkable(cur)){
                                    sus = false;
                                    break;
                                }
                            }
                        }else{
                            for (double j = Math.min(p.y(),p0.y()); j<=Math.max(p.y(),p0.y());j++){
                                double m = (j-Math.min(p.y(),p0.y()))/Math.abs(p.y()-p0.y());
                                Pos cur = p0.sub(p).mul(m).add(p);
                                if (!isWalkable(cur)){
                                    sus = false;
                                    break;
                                }
                            }
                        }
                        connected[x+y*width][x0+y0*width] = sus;
                        if (sus){
                            distance[x+y*width][x0+y0*width] = Math.sqrt((x-x0)*(x-x0)+(y-y0)*(y-y0));
                        }
                    }
                }
            }
        }
        for (int k = 0; k < blockcount; k++) {
            for (int a = 0; a < blockcount; a++) {
                for (int b = 0; b < blockcount; b++) {
                    distance[a][b] = Math.min(distance[a][b],distance[a][k]+distance[k][b]);
                }
            }
        }
        for (int a = 0; a < blockcount; a++) {
            for (int b = 0; b < blockcount; b++) {
                for (int c = 0; c < blockcount; c++){
                    if (walkable[c] && c!=a && connected[a][c]){
                        if (distance[a][c]+distance[c][b]<=distance[a][b]) {
                            target[a][b]=new BlockPos(c%width,c/width);
                        }
                    }
                }
            }
        }
    }
    private static final Pos delta = Main.character_size.mul(0.5);
    private static final Pos[] deltas = {delta,delta.rotate(90),delta.rotate(180),delta.rotate(270)};
    public boolean isWalkable(Pos pos){
        BlockPos blockpos = parent.getBlockPos(pos);
        if (blockpos==null) return false;
        if (!getPlate(blockpos).hasFlag(Plate.Flag.ground)) return false;
        for(Pos d : deltas){
            BlockPos block = parent.getBlockPos(pos.add(d));
            if (block!=null && !getPlate(block).hasFlag(Plate.Flag.ground)) return false;
        }
        return true;
    }
    public double getDistance(Pos src, Pos dist){
        BlockPos pos0 = parent.getBlockPos(src);
        BlockPos pos1 = parent.getBlockPos(dist);
        if (pos0 == null || pos1 == null) return 0;
        if (isConnected(pos0,pos1)) return src.distance(dist);
        double distance = 10000;
        for(BlockPos pos : blocks){
            if (isConnected(pos0,pos)){
                Pos cent = parent.getBlockCenter(pos);
                distance = Math.min(distance,src.distance(cent)+getDistance(pos,pos1));
            }
        }
        return distance;
    }
    public Pos findFacing(Pos src, Pos dist){
        BlockPos pos0 = parent.getBlockPos(src);
        BlockPos pos1 = parent.getBlockPos(dist);
        if (pos0 == null || pos1 == null) return Pos.zero;
        if (isConnected(pos0,pos1)) return dist.sub(src);
        else {
            BlockPos target = getTarget(pos0,pos1);
            if (target == null) return Pos.zero;
            return parent.getBlockCenter(target).sub(src);
        }
    }
    public BlockPos getTarget(@NotNull BlockPos pos, @NotNull BlockPos dest){
        if (pos.equals(dest)) return null;
        if (isConnected(pos,dest)) return dest;
        return target[pos.x()+ pos.y()*width][dest.x()+ dest.y()*width];
    }
    public boolean isConnected(@NotNull BlockPos pos, @NotNull BlockPos dest){
        return connected[pos.x()+ pos.y()*width][dest.x()+ dest.y()*width];
    }
    public double getDistance(@NotNull BlockPos pos, @NotNull BlockPos dest){
        return distance[pos.x()+ pos.y()*width][dest.x()+ dest.y()*width] * Main.block_size;
    }
    public Pos getFacing(@NotNull BlockPos pos, @NotNull BlockPos dest){
        return getTarget(pos,dest).sub(pos).toPos().unit();
    }
    public Plate getPlate(@NotNull BlockPos pos){
        if (pos.x() < 0 || pos.x() >= width || pos.y() < 0 || pos.y() >= height) return null;
        return data[pos.x()][pos.y()];
    }
    public Plate getPlate(int x, int y) {
        return data[x][y];
    }
    public void setPlate(int x, int y, Plate plate){
        data[x][y] = plate;
    }

    private static class PlateIterator implements Iterator<Plate>{
        private final GameLayout parent;
        private int i=0;

        private PlateIterator(GameLayout parent) {
            this.parent = parent;
        }

        @Override
        public boolean hasNext() {
            return i < parent.blockcount;
        }

        @Override
        public Plate next() {
            if (i>=parent.blockcount) throw new NoSuchElementException();
            Plate r = parent.getPlate(i%parent.width, i/parent.width);
            i++;
            return r;
        }
    }

    @NotNull
    @Override
    public Iterator<Plate> iterator() {
        return new PlateIterator(this);
    }
    private static class BlockIterator implements Iterator<BlockPos>{
        private final GameLayout parent;
        private int i = 0;

        private BlockIterator(GameLayout parent) {
            this.parent = parent;
        }

        @Override
        public boolean hasNext() {
            return i< parent.blockcount;
        }

        @Override
        public BlockPos next() {
            if (!hasNext()) throw new NoSuchElementException();
            BlockPos r = new BlockPos(i%parent.width, i/parent.width);
            i++;
            return r;
        }
    }

    private record BlockIterable(GameLayout parent) implements Iterable<BlockPos> {

        @Contract(value = " -> new", pure = true)
        @Override
        public @NotNull Iterator<BlockPos> iterator() {
            return new BlockIterator(parent);
        }
    }
    public Iterable<BlockPos> blocks(){
        return blocks;
    }
}
