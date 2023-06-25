package net.orange.game.character;

import net.orange.game.combat.*;
import net.orange.game.display.*;
import net.orange.game.page.GamePage;
import net.orange.game.tools.TimeChecker;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.LinkedList;

public class EnemyCharacter extends GameCharacter{
    private BlockPos cur = null;
    private final LinkedList<EnemyAction> actions = new LinkedList<>();
    private double lasttime;
    private int worth = 1;
    private final TimeChecker attacktimer = new TimeChecker(1);
    private int attacktime = 1000;

    public boolean isOriginal() {
        return original;
    }

    private final boolean original;
    public EnemyCharacter(GamePage parent, Pos pos, String name) {
        this(parent, pos, name,true);
    }
    public EnemyCharacter(GamePage parent, Pos pos, String name, boolean original) {
        super(parent, pos);
        this.original = original;
        setTeam(Team.enemy);
        readData("enemies/"+name);
        setHealthbarcolor(Color.RED);
        lasttime = parent.getPlaytime();
        if (data.has("worth")){
            worth = data.getInt("worth");
        }
        addFlag(Flag.walk);
    }
    public EnemyCharacter(GamePage parent, Pos pos, @NotNull EnemyMessage message){
        this(parent, pos, message,true);
    }
    public EnemyCharacter(GamePage parent, Pos pos, @NotNull EnemyMessage message, boolean original){
        super(parent, pos);
        this.original = original;
        setTeam(Team.enemy);
        readData(message.getData());
        addEffect(message.getEffect());
        setHealthbarcolor(Color.RED);
        lasttime = parent.getPlaytime();
        if (data.has("worth")){
            worth = data.getInt("worth");
        }
        addFlag(Flag.walk);
    }
    public void addAction(EnemyAction action) {
        actions.add(action);
    }

    @Override
    public void paint(Graphics2D g) {
        super.paint(g);
    }
    private void nextAction() {
        actions.poll();
        lasttime = parent.getPlaytime();
        if (actions.isEmpty()){
            parent.damage_tower(worth);
            forcekill();
        }
    }
    @Override
    public void setUnbalanced(boolean unbalanced) {
        super.setUnbalanced(unbalanced);
        if (!unbalanced && cur != null) {
            cur = parent.getBlockPos(center());
        }
    }

    @Override
    public void onTick() {
        super.onTick();
        double curtime = parent.getPlaytime();
        if (attacktimer.check()){
            boolean sus = attack();
            if (sus){
                attacktime = 0;
            }else {
                attacktimer.activate();
            }
        }
        if (!actions.isEmpty()) {
            EnemyAction action = actions.getFirst();
            switch (action.type()) {
                case move -> {
                    BlockPos target = action.pos();
                    if (cur == null) {
                        cur = parent.getBlockPos(center());
                    }
                    if (cur != null) {
                        BlockPos nextblock = parent.getBlockPos(center());
                        if (nextblock != null && nextblock != cur) {
                            Pos nextcenter = parent.getBlockCenter(nextblock);
                            if (nextcenter.sub(center()).length() < 3) cur = nextblock;
                        }
                        BlockPos targetblock = parent.getLayout().getTarget(cur, target);
                        if (targetblock != null) {
                            Pos targetpos = parent.getBlockCenter(targetblock);
                            Pos movefactor = targetpos.sub(center());
                            setSpeed(movefactor);
                        } else {
                            nextAction();
                        }
                    }
                }
                case wait_for_second -> {
                    if (curtime > lasttime + actions.getFirst().time()) {
                        nextAction();
                    }
                }
                case wait_for_playtime -> {
                    if (curtime > actions.getFirst().time()) {
                        nextAction();
                    }
                }
                case wait_for_appeartime -> {
                    if (curtime > getActivateTime() + actions.getFirst().time()) {
                        nextAction();
                    }
                }
                case disappear -> {
                    setDisplayed(false);
                    nextAction();
                }
                case appear_at_pos -> {
                    setDisplayed(true);
                    moveto(parent.getBlockCenter(action.pos()));
                    nextAction();
                }
            }
        }
    }

    @Override
    public long getPriority() {
        long r = super.getPriority();
        double dis = 0;
        Pos last = center();
        for(EnemyAction action : actions){
            if (action.type() == ActionType.move){
                Pos nxt = parent.getBlockCenter(action.pos());
                dis += parent.getLayout().getDistance(last,nxt);
                last = nxt;
            }else if (action.type() == ActionType.appear_at_pos){
                last = parent.getBlockCenter(action.pos());
            }
        }
        //System.out.println(getUuid()+"dis="+dis);
        r -= (long)(dis*1000000);
        return r;
    }
}
