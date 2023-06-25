package net.orange.game.combat;

import net.orange.game.character.GameCharacter;
import net.orange.game.tools.Depended;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class AppliedEffect implements Depended, Comparable<AppliedEffect>{
    public Effect getParent() {
        return parent;
    }

    private final Effect parent;

    public int getOverlay_count() {
        return overlay_count;
    }

    private int overlay_count;

    public int getTickcount() {
        return tickcount;
    }

    private int tickcount = 0;

    public int getTime() {
        return time;
    }

    private int time = 0;
    private final LinkedList<Integer> endtimes;
    private final GameCharacter gameCharacter;

    public AppliedEffect(Effect parent, GameCharacter gameCharacter) {
        this.parent = parent;
        this.gameCharacter = gameCharacter;
        this.endtimes = new LinkedList<>();
        overlay_count = 0;
        overlay();
    }
    private void checkAlive(){
        if (parent.getParent() != null && !parent.getParent().isAlive()) {
            overlay_count = 0;
            time = 0;
            tickcount = 0;
        }
    }
    public void onTick(@NotNull GameCharacter character){
        checkAlive();
        if (overlay_count>0) {
            parent.onTick(character,this);
            tickcount++;
            if (parent.getRemoveRule()==Effect.RemoveRule.time ||
                    parent.getRemoveRule()==Effect.RemoveRule.tick) timefly();
        }
    }
    public void onDamage(@NotNull Damage damage, @NotNull GameCharacter character){
        checkAlive();
        if (overlay_count>0) {
            parent.onDamage(damage, character,this);
            if (parent.getRemoveRule()==Effect.RemoveRule.damage) timefly();
        }
    }
    public void onAttack(@NotNull Damage damage, @NotNull GameCharacter target){
        checkAlive();
        if (overlay_count>0) {
            parent.onAttack(damage, target,this);
        }
    }

    public void onOneAttack(){
        if (overlay_count>0 && parent.getRemoveRule() == Effect.RemoveRule.attack) {
            timefly();
        }
    }
    public List<Modifier> getModifiers() {
        return parent.getModifiers();
    }
    public boolean hasState(CharacterState state){
        return parent.hasState(state);
    }
    public void overlay(){
        if (overlay_count< parent.getOverlaylimit()) {
            overlay_count++;
        }else{
            endtimes.poll();
        }
        endtimes.push(time + parent.getDuration());
    }
    public void unoverlay(){
        overlay_count--;
        if (overlay_count == 0) {
            time = 0;
            tickcount = 0;
        }
    }
    public void timefly(){
        time++;
        while(!endtimes.isEmpty() && endtimes.peek()<=time){
            endtimes.poll();
            unoverlay();
        }
    }
    public double getRemainRate(){
        return ((double) (endtimes.getLast() - time)) / parent.getDuration();
    }

    public boolean isFull(){
        return overlay_count>=parent.getOverlaylimit();
    }
    public boolean isActivated(){
        return overlay_count>0;
    }
    public long getPriority(){
        return parent.getPriority();
    }

    @Override
    public boolean isAlive() {
        return gameCharacter.isAlive() && overlay_count>0;
    }

    @Override
    public int compareTo(@NotNull AppliedEffect o) {
        return (int) (o.getPriority()-getPriority());
    }
}
