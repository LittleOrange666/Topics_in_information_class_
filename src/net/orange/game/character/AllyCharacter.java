package net.orange.game.character;

import net.orange.game.Main;
import net.orange.game.combat.AttributeType;
import net.orange.game.data.json.JsonObject;
import net.orange.game.display.Pos;
import net.orange.game.display.RotatedPicture;
import net.orange.game.page.GamePage;
import net.orange.game.tools.TimeChecker;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class AllyCharacter extends GameCharacter {
    private final RotatedPicture sword;
    private final TimeChecker attacktimer = new TimeChecker(1000);
    private int attacktime = 1000;
    public final String labelpath;
    public String type;

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    private boolean isMain = false;
    public int cost;
    public final int redeploy;

    public int getDeploycount() {
        return deploycount;
    }

    public void addDeploycount() {
        this.deploycount++;
    }

    private int deploycount = 0;
    public AllyCharacter(GamePage parent, String name) {
        super(parent, Pos.zero);
        setTeam(Team.ally);
        readData("characters/"+name);
        JsonObject textures = data.getObject("textures");
        sword = new RotatedPicture(textures.getString("weapon"));
        setHealthbarcolor(Color.CYAN);
        labelpath = textures.getString("label");
        cost = data.getInt("cost");
        if (data.has("character_type")){
            type = data.getString("character_type");
        }else {
            type = data.getString("type");
        }
        redeploy = data.getInt("redeploy");
    }
    public AllyCharacter(GamePage parent, String name, JsonObject object) {
        super(parent, Pos.zero);
        setTeam(Team.ally);
        readData(object);
        setName(name);
        JsonObject textures = data.getObject("textures");
        sword = new RotatedPicture(textures.getString("weapon"));
        setHealthbarcolor(Color.CYAN);
        labelpath = textures.getString("label");
        cost = data.getInt("cost");
        if (data.has("character_type")){
            type = data.getString("character_type");
        }else {
            type = data.getString("type");
        }
        redeploy = data.getInt("redeploy");
    }

    @Override
    protected void onAttributeChange(@NotNull AttributeType type) {
        super.onAttributeChange(type);
        if (type == AttributeType.attack_speed || type == AttributeType.attack_interval){
            long time = (long) (100*getAttribute(AttributeType.attack_interval)/getAttribute(AttributeType.attack_speed));
            attacktimer.changeDelay(time);
        }
    }

    @Override
    public void paint(Graphics2D g) {
        super.paint(g);
        if (isActivated()){
            int x = attacktime<15?attacktime:0;
            double theta = Math.abs((x)-7.5)/7.5*90+90;
            sword.rotateTo(theta);
            Pos factor = new Pos(0,50);
            factor = factor.rotate(theta);
            Pos target = center().add(factor);
            sword.paint(target,g);
        }
    }

    @Override
    public void onTick() {
        super.onTick();
        attacktime++;
        Pos movefactor = new Pos();
        if (attacktimer.check()){
            boolean sus = attack();
            if (sus){
                attacktime = 0;
            }else {
                attacktimer.activate();
            }
        }
        if (!isActivated()){
            attacktimer.reset();
        }
        if (hasFlag(Flag.main)){
            if (parent.pressed.contains(KeyEvent.VK_W)) movefactor = movefactor.add(0,-1);
            if (parent.pressed.contains(KeyEvent.VK_A)) movefactor = movefactor.add(-1,0);
            if (parent.pressed.contains(KeyEvent.VK_S)) movefactor = movefactor.add(0,1);
            if (parent.pressed.contains(KeyEvent.VK_D)) movefactor = movefactor.add(1,0);
            movefactor = movefactor.mul(1000);
        }
        if (hasFlag(Flag.walk)){
            List<GameCharacter> targets = parent.queryTarget(1, this::opposite);
            if (!targets.isEmpty()) {
                GameCharacter target = targets.get(0);
                double dis = getAttribute(AttributeType.attack_range)-getAttribute(AttributeType.move_speed)/Main.fps/Main.block_size*5;
                if (distance(target)>dis){
                    movefactor = parent.getLayout().findFacing(center(),target.center());
                }
            }
        }
        setSpeed(movefactor);
    }

    @Override
    public void forcekill() {
        if (isAlive()) {
            super.forcekill();
            parent.redeploy(this);
        }
    }
    public void retreat(){
        if (isAlive()) {
            parent.addcost(cost*(1+Math.min(3,deploycount))/4);
            forcekill();
        }
    }
}
