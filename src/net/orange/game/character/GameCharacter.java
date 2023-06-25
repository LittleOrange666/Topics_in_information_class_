package net.orange.game.character;

import net.orange.game.Main;
import net.orange.game.data.exception.DataException;
import net.orange.game.data.exception.DataIOException;
import net.orange.game.data.exception.DataVerifyException;
import net.orange.game.data.json.JsonArray;
import net.orange.game.data.json.JsonObject;
import net.orange.game.tools.*;
import net.orange.game.combat.*;
import net.orange.game.display.*;
import net.orange.game.page.GamePage;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GameCharacter extends Moveable implements Paintable, Depended {
    public final GamePage parent;

    public double getActivateTime() {
        return activateTime;
    }

    private double activateTime = 0;
    private static final Effect appeareffect;
    static {
        appeareffect = new Effect(Effect.RemoveRule.tick,2);
        appeareffect.addState(CharacterState.invincible);
    }

    public boolean isActivated() {
        return activated;
    }

    public void activate() {
        this.activated = true;
        activateId = parent.getActivateId();
        activateTime = parent.getPlaytime();
        addEffect(appeareffect);
    }

    private boolean activated = false;

    public boolean isDisplayed() {
        return displayed;
    }

    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    private boolean displayed = false;
    public double getHealthRate() {
        return healthRate;
    }
    public double getHealth(){
        return healthRate*getAttribute(AttributeType.max_health);
    }

    public boolean isAlive() {
        return alive;
    }

    private boolean alive = true;

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
    public boolean opposite(GameCharacter character) {
        return this.team.opposite(character);
    }
    public boolean opposite(Team team) {
        return this.team.opposite(team);
    }

    private Team team = Team.neutral;

    public void setHealthRate(double healthRate) {
        this.healthRate = Math.max(0,Math.min(healthRate,1));
        if (healthRate <=0) kill();
    }

    public void decreaseHealth(double value) {
        setHealthRate(healthRate - (value / getAttribute(AttributeType.max_health)));
    }

    private double healthRate = 1;
    private final AllModifiers modifiers;

    public Stream<AppliedEffect> getEffects() {
        return effects.values().stream().filter(AppliedEffect::isActivated).sorted();
    }
    public AppliedEffect getEffect(@NotNull Effect effect){
        return effects.get(effect.getUuid());
    }

    private final HashMap<UUID,AppliedEffect> effects;
    protected JsonObject data = null;
    private Picture picture = null;
    private Pos delta = null;

    public Pos getSpeed() {
        return speed;
    }

    public void setSpeed(Pos speed) {
        this.speed = speed;
    }

    public Pos getSlidespeed() {
        return slidespeed;
    }

    public void setSlidespeed(Pos slidespeed) {
        this.slidespeed = slidespeed;
    }
    public void push(Pos power){
        this.slidespeed = this.slidespeed.add(power);
        setUnbalanced(true);
    }

    public boolean isUnbalanced() {
        return unbalanced;
    }

    public void setUnbalanced(boolean unbalanced) {
        this.unbalanced = unbalanced;
        if (unbalanced && blocker != null){
            unblock();
        }
    }

    private boolean unbalanced = false;
    private Pos speed = Pos.zero;
    private Pos slidespeed = Pos.zero;
    private Color healthbarcolor = null;

    public AttackArea getAttackarea() {
        return attackarea;
    }

    public void setAttackarea(AttackArea attackarea) {
        this.attackarea = attackarea;
    }

    private AttackArea attackarea = null;
    public boolean hasMainSkill() {
        return !skills.isEmpty() && skills.get(0).isMain();
    }

    public Skill getMainSkill() {
        if (hasMainSkill()) return skills.get(0);
        return null;
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    private final ArrayList<Skill> skills;

    public long getActivateId() {
        return activateId;
    }
    public long getPriority(){
        return -activateId+(long)getAttribute(AttributeType.priority);
    }

    private long activateId = 0;
    private final HashSet<GameCharacter> blocked;
    private GameCharacter blocker;
    private final Pos randomdelta;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public int getLevel() {
        return level;
    }

    private int level = 1;

    public GameCharacter(GamePage parent, @NotNull Pos pos) {
        super(pos.sub(Main.character_size.mul(0.5)), Main.character_size);
        this.parent = parent;
        modifiers = new AllModifiers(this);
        effects = new HashMap<>();
        blocked = new HashSet<>();
        skills = new ArrayList<>();
        randomdelta = new Pos(Main.rand.nextInt(-3,4),Main.rand.nextInt(-3,4));
    }
    public boolean blockable(){
        return hasFlag(Flag.ground) && blocked.size()<getAttribute(AttributeType.block_count);
    }
    public void block(@NotNull GameCharacter target){
        target.blocker = this;
        blocked.add(target);
        Pos dis = target.center().sub(center());
        double req = ((double) Main.block_size /2)-1;
        double len = Math.max(Math.abs(dis.x()),Math.abs(dis.y()));
        if (len<req){
            double m = req/len;
            Pos mov = dis.mul(m-1);
            parent.slowmove(target,mov.unit(),mov.length(),false);
        }
    }
    public void unblock(){
        if (this.blocker!=null){
            this.blocker.blocked.remove(this);
            this.blocker = null;
        }
    }
    public boolean blocked(){
        return blocker != null;
    }
    public void addEffect(@NotNull Effect effect) {
        if (effects.containsKey(effect.getUuid())) effects.get(effect.getUuid()).overlay();
        else{
            effects.put(effect.getUuid(),new AppliedEffect(effect, this));
        }
        for(Modifier modifier : effect.getModifiers()) {
            onAttributeChange(modifier.getAttribute());
        }
    }
    public boolean hasEffect(@NotNull Effect effect){
        return effects.containsKey(effect.getUuid()) && effects.get(effect.getUuid()).isActivated();
    }

    public double getAttribute(@NotNull AttributeType type){
        double value = type.default_value;
        double a = 0,b = 0,c = 1;
        for(Modifier modifier : modifiers){
            if (modifier.getAttribute() == type){
                switch (modifier.getType()){
                    case add -> value+=modifier.getValue();
                    case multiply -> a+=modifier.getValue();
                    case final_add -> b+=modifier.getValue();
                    case final_multiply -> c*=modifier.getValue();
                }
            }
        }
        double r = (value * (1 + a) + b) * c;
        if (type.minimum!=null) r = Math.max(type.minimum,r);
        if (type.maximum!=null) r = Math.min(type.maximum,r);
        return r;
    }
    protected void onAttributeChange(@NotNull AttributeType type){
    }

    public void damage(@NotNull Damage damage) {
        if (hasState(CharacterState.invincible)) return;
        for (Effect effect : damage.getEffects()){
            addEffect(effect);
        }
        getEffects().forEach(effect -> effect.onDamage(damage,this));
        double value = 0;
        switch(damage.getType()){
            case physics -> value = Math.max(damage.getValue()-getAttribute(AttributeType.defense),damage.getValue()*0.05);
            case magic -> value = Math.max(5,100-getAttribute(AttributeType.resistance))*0.01*damage.getValue();
            case real -> value = damage.getValue();
            case heal -> value = -damage.getValue();
        }
        for(Skill skill : skills) skill.onDamage();
        for(Skill skill : skills) skill.tryunlock();
        decreaseHealth(value);
    }

    @Override
    public void paint(Graphics2D g) {
        if (picture != null){
            picture.paint(getPos().sub(delta).add(randomdelta),g);
        }
    }
    public void paintHealthBar(Graphics2D g) {
        if (isActivated()) {
            BlockPos pos = Scaler.scale(getPos().add(randomdelta)).toBlockPos();
            BlockPos size = displaySize().toBlockPos();
            if (healthbarcolor != null && (team!=Team.enemy || getHealthRate() < 1)){
                g.setColor(healthbarcolor);
                g.fillRect(pos.x(), pos.y() + size.y(), Math.max(1, (int) (displaySize().x() * getHealthRate())), 3);
            }
            if (hasMainSkill()) {
                if (hasEffect(getMainSkill().getEffect())){
                    g.setColor(Color.ORANGE);
                    g.fillRect(pos.x(), pos.y() + size.y()+3, (int) (displaySize().x() * effects.get(getMainSkill().getEffect().getUuid()).getRemainRate()), 3);
                }else{
                    g.setColor(Color.GREEN);
                    g.fillRect(pos.x(), pos.y() + size.y()+3, (int) (displaySize().x() * getMainSkill().getSpRate()), 3);
                }
            }
        }
    }
    public void move(Pos pos) {
        setPos(getPos().add(pos));
    }
    public void moveto(@NotNull Pos pos) {
        setPos(pos.sub(getSize().mul(0.5)));
    }
    public void readData(String path){
        try {
            readData(Main.read(path));
        } catch (DataIOException e) {
            e.printStackTrace();
        }
    }

    public void readData(JsonObject object){
        data = object;
        JsonObject attributes = data.getObject("attributes");
        Effect effect = new Effect(Effect.RemoveRule.never);
        for(AttributeType type : AttributeType.values()){
            if (attributes.has(type.toString())){
                effect.addModifier(new Modifier(type,ModifierType.add,attributes.getDouble(type.toString())-type.default_value));
            }
        }
        addEffect(effect);
        if (data.has("textures")){
            JsonObject textures = data.getObject("textures");
            if (textures.has("character")){
                setPicture(new Picture(textures.getString("character")));
            }
        }
        if (data.has("skills")){
            JsonArray skillarray = data.getArray("skills");
            for (JsonObject obj : skillarray.objects()){
                addSkill(SkillFactory.create(this,obj));
            }
        }
        if (data.has("attack_area")){
            setAttackarea(new AttackArea(data.getArray("attack_area")));
        }
        if (data.has("name")){
            name = data.getString("name");
        }
        if (data.has("level")){
            level = data.getInt("level");
        }
    }
    public double distance(@NotNull GameCharacter character){
        return center().distance(character.center())/Main.block_size;
    }
    public void onTick() {
        Plate plate = parent.getPlate(center());
        if (plate == null || plate.hasFlag(Plate.Flag.hole)) forcekill();
        setSpeed(Pos.zero);

        getEffects().forEach(effect -> effect.onTick(this));
        blocked.removeIf((e)->!e.alive);
        if (blocker!=null && !blocker.alive) blocker = null;
        for(Skill skill : skills) skill.onTick();
        for(Skill skill : skills) skill.tryunlock();
    }
    public void doMove(){
        if (isUnbalanced()){
            Pos facing = slidespeed.unit();
            double m = slidespeed.length();
            parent.slowmove(this, facing,m,false);
            if (m<=Main.hindrance) {
                slidespeed = Pos.zero;
                setUnbalanced(false);
            }else{
                slidespeed = facing.mul(m - Main.hindrance);
            }
        }else{
            Pos facing = speed.unit();
            double m = Math.min(getAttribute(AttributeType.move_speed) / Main.fps,speed.length());
            parent.slowmove(this, facing,m,true);
        }
    }
    public BlockPos getBlockPos(){
        return parent.getBlockPos(center());
    }
    public Plate getPlate(){
        return parent.getPlate(center());
    }
    protected void setPicture(@NotNull Picture picture){
        this.picture = picture;
        delta = picture.getSize().sub(getSize());
        delta = new Pos(delta.x()/2,delta.y());
    }
    protected void setHealthbarcolor(Color color){
        healthbarcolor = color;
    }
    public void forcekill(){
        if (alive) {
            alive = false;
            displayed = false;
            activated = false;
            if (this instanceof AllyCharacter character) {
                if (Objects.equals(character.type, "any")) {
                    flags.clear();
                }
            }
            if (data.has("attack_area")) {
                setAttackarea(new AttackArea(data.getArray("attack_area")));
            }
        }
    }
    public void kill(){
        if (!hasState(CharacterState.undead)) forcekill();
    }

    protected void attack(@NotNull GameCharacter target){
        Damage damage = new Damage(getAttribute(AttributeType.attack), Damage.Type.physics, this);
        getEffects().forEach(effect -> effect.onAttack(damage,target));
        target.damage(damage);
    }
    public boolean attackCondition(GameCharacter o){
        if (blocked.contains(o)) return true;
        if (attackarea == null){
            return distance(o)<=getAttribute(AttributeType.attack_range);
        }else{
            return attackarea.contains(o.getBlockPos().sub(getBlockPos()));
        }
    }
    public boolean attack() {
        Predicate<GameCharacter> condition = this::opposite;
        Sorter<GameCharacter> sorter = o-> 0L;
        if (hasState(CharacterState.friend_target)){
            condition = team;
        }else if (!hasFlag(Flag.high)&&!hasState(CharacterState.can_attack_high)){
            condition = condition.and(Flag.high.negate());
        }
        if (hasState(CharacterState.healer)){
            condition = condition.and(o->o.healthRate<1&&!o.hasState(CharacterState.no_healing));
            sorter = o-> (long) (-10000000000L*o.healthRate);
        }
        condition = condition.and(this::attackCondition);
        sorter = sorter.add((o)->(o.blocker==this||this.blocker==o?1000000000000000000L:0L));
        List<GameCharacter> targets = parent.queryTarget((int)getAttribute(AttributeType.attack_count),condition,sorter);
        if (!targets.isEmpty()){
            for (GameCharacter target : targets){
                attack(target);
            }
            getEffects().forEach(AppliedEffect::onOneAttack);
            for(Skill skill : skills) skill.onAttack();
            for(Skill skill : skills) skill.tryunlock();
        }
        return !targets.isEmpty();
    }
    public void reset(){
        healthRate = 1;
        for(Skill skill : skills) skill.reset();
        alive = true;
    }

    public HashSet<Flag> getFlags() {
        return flags;
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

    public enum Flag implements Predicate<GameCharacter> {
        high,ground,walk,main;

        @Override
        public boolean test(@NotNull GameCharacter character) {
            return character.hasFlag(this);
        }
    }
    public boolean hasState(CharacterState state) {
        for(AppliedEffect effect : effects.values()){
            if (effect.isActivated() && effect.hasState(state)) return true;
        }
        return false;
    }
    public static void verify(JsonObject data) throws DataVerifyException{
        GameCharacter character = new GameCharacter(null,Pos.zero);
        try {
            character.readData(data);
        }catch (DataException e){
            throw new DataVerifyException("failed verify character data",e);
        }
    }
}
