package net.orange.game.combat;

import net.orange.game.Main;
import net.orange.game.character.GameCharacter;
import net.orange.game.display.Picture;
import net.orange.game.display.UniqueObj;
import net.orange.game.tools.Depended;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Skill extends UniqueObj implements Depended {
    private static final Modifier no_sp_recover = new Modifier(AttributeType.sp_recover_ratio,ModifierType.final_multiply,0);

    @Contract("_ -> new")
    public static @NotNull Skill doNothing(GameCharacter character){
        return new Skill(character,1,SpRecoverRule.never,Effect.donothing);
    }

    @Override
    public boolean isAlive() {
        return parent.isAlive();
    }

    public enum SpRecoverRule{
        never, time, onattack, ondamage, passive
    }
    private final GameCharacter parent;

    public boolean isMain() {
        return main;
    }

    public void setMain(boolean main) {
        this.main = main;
    }

    private boolean main = true;

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    private boolean auto = true;

    public double getSp() {
        return sp;
    }

    public double getSpRate(){
        return sp/sp_required;
    }

    public void setSp(double sp) {
        this.sp = sp;
    }

    private double sp;

    public double getSp_required() {
        return sp_required;
    }

    private final double sp_required;
    private final SpRecoverRule spRecoverRule;

    public boolean isSplocked() {
        return splocked;
    }

    public void setSplocked(boolean splocked) {
        this.splocked = splocked;
    }

    private boolean splocked;

    public Effect getEffect() {
        return effect;
    }

    private final Effect effect;
    private boolean applied = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name = "技能";

    public Picture getIcon() {
        return icon;
    }

    public void setIcon(Picture icon) {
        this.icon = icon;
    }

    private Picture icon = Picture.rect(10,10, Color.WHITE);

    public Skill(GameCharacter parent, double spRequired, SpRecoverRule spRecoverRule, Effect effect) {
        this.parent = parent;
        sp_required = spRequired;
        this.spRecoverRule = spRecoverRule;
        this.effect = effect;
        this.effect.setParent(this);
        this.sp = 0;
    }
    public void reset(){
        sp = 0;
        applied = false;
    }
    public void activate(){
        sp = 0;
        applied = true;
        parent.addEffect(effect);
        if (parent.getEffect(effect).isFull()) setSplocked(true);
    }
    public boolean canActivate(){
        return sp >= sp_required;
    }
    public void addsp(double value) {
        if (this.spRecoverRule != SpRecoverRule.passive) {
            sp += value;
            sp = Math.min(sp,sp_required);
            if (auto && canActivate()) {
                activate();
            }
        }
    }
    public void onTick(){
        if (this.spRecoverRule == SpRecoverRule.passive && !applied) activate();
        if(!isSplocked() && spRecoverRule == SpRecoverRule.time) addsp(parent.getAttribute(AttributeType.sp_recover_ratio)/Main.fps);
    }
    public void onDamage(){
        if(!isSplocked() && spRecoverRule == SpRecoverRule.ondamage) addsp(parent.getAttribute(AttributeType.sp_recover_ratio));
    }
    public void onAttack(){
        if(!isSplocked() && spRecoverRule == SpRecoverRule.onattack) addsp(parent.getAttribute(AttributeType.sp_recover_ratio));
    }
    public void tryunlock(){
        if (isSplocked() && !parent.getEffect(effect).isFull()) setSplocked(false);
    }
}
