package net.orange.game.page;

import javafx.util.Pair;
import net.orange.game.Main;
import net.orange.game.character.*;
import net.orange.game.combat.*;
import net.orange.game.data.exception.DataIOException;
import net.orange.game.data.json.JsonArray;
import net.orange.game.data.json.JsonObj;
import net.orange.game.data.json.JsonObject;
import net.orange.game.data.json.JsonTag;
import net.orange.game.data.object.ChoisePair;
import net.orange.game.display.*;
import net.orange.game.game.GameResult;
import net.orange.game.tools.*;
import net.orange.game.game.GameAction;
import net.orange.game.game.GameLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GamePage extends Page {
    public HashSet<Integer> pressed = new HashSet<>();
    private boolean starting = true;
    private boolean running = false;
    private final String name;

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        if (isRunning()) {
            this.paused = paused;
            if (paused) {
                middletext = Picture.fromText("PAUSED", Main.textFont(100, Font.BOLD));
            } else {
                middletext = null;
            }
            exitbtn.setDisplayed(this.paused);
        }
    }

    private boolean paused = false;
    public ArrayList<GameCharacter> characters = new ArrayList<>();
    private LinkedList<Pair<Double, GameAction>> actionlist;
    int time = 0;
    private Pos mapdelta = new Pos();
    private final ArrayList<Deployment> deployments = new ArrayList<>();
    private Deployment selected_deployment = null;
    private GameCharacter selected_character = null;
    private Deployment waiting_deployment = null;
    private AttackArea current_attack_area = null;

    public GameLayout getLayout() {
        return layout;
    }

    private GameLayout layout;
    private int tickcount = 0;
    private int slowdowncounter = 0;
    private long activateIterator = 0;
    private int cost = 0;
    private TimeChecker costcounter;
    private int tower_health = 3;
    private int max_tower_health = 3;
    private final SwitchButton pausebtn;
    private final SwitchButton fastbtn;
    private final TheButton activebtn;
    private final TheButton removebtn;
    private final TheButton exitbtn;
    private int max_cost = 0;

    public Picture getTutorialtext() {
        return tutorialtext;
    }

    public void setTutorialtext(Picture tutorialtext) {
        this.tutorialtext = tutorialtext;
    }

    private Picture tutorialtext = null;
    private Picture middletext = null;
    private final ArrayList<CharacterBlock> characterBlocks = new ArrayList<>();

    public Random getRandom() {
        return random;
    }

    private final Random random;

    public ArrayList<ChoisePair> getChoices() {
        return choices;
    }

    private final ArrayList<ChoisePair> choices = new ArrayList<>();
    private int total_enemy_count = 0;
    private int spawn_enemy_count = 0;
    public ArrayList<ArrayList<EnemyAction>> traces = new ArrayList<>();
    public HashMap<String,EnemyMessage> messages = new HashMap<>();
    public GamePage(String level) {
        /*
        addDeployment("0");
        addDeployment("2");
        addDeployment("2");
        addDeployment("1");
        addDeployment("1");
        addDeployment("5");
        addDeployment("5");
        addDeployment("5");
        addDeployment("5");
        addDeployment("6");
         */
        random = new Random();
        this.name = level;
        loadLevel("levels/"+level);
        pausebtn = new SwitchButton(new Pos(1850, 10), new Picture("icon/1.png"), new Picture("icon/0.png"));
        pausebtn.connecton(()->setPaused(true));
        pausebtn.connectoff(()->setPaused(false));
        pausebtn.setCondition((o)->isRunning());
        fastbtn = new SwitchButton(new Pos(1780, 10), new Picture("icon/2.png"), new Picture("icon/3.png"));
        fastbtn.connecton(()->Main.mainWindow.setSpeedrate(2));
        fastbtn.connectoff(()->Main.mainWindow.setSpeedrate(1));
        fastbtn.setCondition((o)->isRunning());
        activebtn = new TheButton(new Pos(30, 800), Picture.fromText("⚡",Main.emojiFont(60,Font.BOLD),Color.GREEN));
        activebtn.connect(()->{
            if (selected_character != null && selected_character.hasMainSkill() && selected_character instanceof AllyCharacter){
                Skill skill = selected_character.getMainSkill();
                if (!skill.isAuto() && skill.canActivate()){
                    skill.activate();
                }
            }
        });
        activebtn.setDisplayed(false);
        removebtn = new TheButton(new Pos(100, 800), Picture.fromText("X",Main.numberFont(60,Font.BOLD),Color.RED));
        removebtn.connect(()->{
            if (selected_character != null && selected_character instanceof AllyCharacter character){
                character.retreat();
                selected_character = null;
            }
        });
        removebtn.setDisplayed(false);
        exitbtn = new TheButton(new Pos(100,100),Picture.fromText("撤退",Main.textFont(60),Color.BLACK));
        exitbtn.setDisplayed(false);
        exitbtn.connect(()->hover(new ConfirmDialog("確定要撤退嗎?",this::mission_faild)));
        addPart(pausebtn,fastbtn,activebtn,removebtn,exitbtn);
    }
    public void loadLevel(String path){
        try {
            JsonObject object = Main.read(path);
            JsonObject base = object.getObject("base");
            int width = base.getInt("width");
            int height = base.getInt("height");
            layout = new GameLayout(this, width, height);
            JsonArray map = base.getArray("map");
            mapdelta = layout.initPlates(map);
            JsonArray reddoors = base.getArray("red_doors");
            for(JsonArray o : reddoors.arrays()){
                layout.getPlate(BlockPos.from_json(o)).addFlag(Plate.Flag.red);
            }
            JsonArray bluedoors = base.getArray("blue_doors");
            for(JsonArray o : bluedoors.arrays()){
                layout.getPlate(BlockPos.from_json(o)).addFlag(Plate.Flag.blue);
            }
            layout.init();
            if (base.has("tower_health")){
                max_tower_health = tower_health = base.getInt("tower_health");
            }
            if (base.has("default_cost")){
                addcost(base.getInt("default_cost"));
            }
            max_cost = 0;
            if (base.has("max_cost")){
                max_cost = base.getInt("max_cost");
            }
            JsonObject enemies = object.getObject("enemies");
            for(String name : enemies.keys()){
                messages.put(name,new EnemyMessage(name,enemies.getObject(name)));
            }
            actionlist = new LinkedList<>();
            for(JsonArray obj : object.getArray("traces").arrays()){
                ArrayList<EnemyAction> l = new ArrayList<>();
                for(JsonObject o : obj.objects()){
                    l.add(EnemyAction.from_json(o));
                }
                traces.add(l);
            }
            for(JsonObject obj : object.getArray("actionlist").objects()){
                GameAction action = GameAction.analyse(obj,this);
                double time = obj.getDouble("time");
                String type = obj.getString("type");
                if (Objects.equals(type, "multiple")){
                    double delay = obj.getDouble("delay");
                    int count = obj.getInt("count");
                    for (int j = 0; j < count; j++) {
                        actionlist.add(new Pair<>(time+j*delay,action));
                    }
                }else{
                    actionlist.add(new Pair<>(time,action));
                }
            }
            actionlist.sort((a,b) -> Main.sign(a.getKey() - b.getKey()));
            for(Pair<Double, GameAction> o : actionlist){
                if (o.getValue() instanceof GameAction.Spawn){
                    total_enemy_count++;
                }
            }
            if (object.has("formation")){
                JsonArray form = object.getArray("formation");
                for(JsonObj o : form){
                    choices.add(ChoisePair.from_json((JsonObject) o));
                }
            }
            tickcount = 0;
        } catch (DataIOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void paint(Graphics2D g) {
        //long t = System.currentTimeMillis();
        super.paint(g);
        if (starting) g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, time*0.02F));
        if (selected_character != null || current_attack_area != null) {
            for (Plate plate : layout) {
                if (plate.hasFlag(Plate.Flag.showarea))plate.paint(g);
            }
            int size = (int)(Scaler.scale*Main.block_size);
            BufferedImage buffer = new BufferedImage(size*layout.getWidth(), size*layout.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = buffer.createGraphics();
            g2.setColor(Color.ORANGE);
            g2.setStroke(new BasicStroke((float) Main.block_size /8));
            long delta = (long)(getPlaytime()*120)%120;
            for (int i = -1;i<=(Main.default_width+Main.default_height)/Main.block_size*2;i++){
                Pos pos0 = new Pos((double) (i * Main.block_size) /2-delta,0);
                Pos pos1 = new Pos((double) (i * Main.block_size) /2-delta-Main.default_height,Main.default_height);
                BlockPos p0 = pos0.toBlockPos();
                BlockPos p1 = pos1.toBlockPos();
                g2.drawLine(p0.x(),p0.y(),p1.x(),p1.y());
            }
            g2.dispose();
            double dx = (double) (Main.default_width - layout.getWidth() * Main.block_size) /2;
            double dy = (double) (Main.default_height - layout.getHeight() * Main.block_size) /2;
            BlockPos corner = Scaler.scale(new Pos(dx,dy)).toBlockPos();
            g.drawImage(buffer,corner.x(),corner.y(),null);
            for (Plate plate : layout) {
                if (!plate.hasFlag(Plate.Flag.showarea))plate.paint(g);
            }
        }else{
            for (Plate plate : layout) plate.paint(g);
        }
        for(GameCharacter character : characters){
            if (character.isDisplayed()) {
                character.paint(g);
            }
        }
        for(GameCharacter character : characters){
            if (character.isDisplayed()) {
                character.paintHealthBar(g);
            }
        }
        Pos startpos = Main.default_size.sub(deployments.size()*Main.deployment_size, Main.deployment_size);
        for (int i = 0; i < deployments.size(); i++) {
            Pos pos = startpos.add(Main.deployment_size*i,0);
            deployments.get(i).paint(pos,g);
        }
        if (selected_deployment != null){
            selected_deployment.getCharacter().paint(g);
        }
        if (waiting_deployment != null){
            waiting_deployment.getCharacter().paint(g);
        }
        /*
        for (CharacterBlock block : characterBlocks) {
            block.paint(g);
        }
         */
        if (getSelectedCharacter() != null){
            GameCharacter character = getSelectedCharacter();
            Font font = Main.textFont(40);
            Font font0 = Main.textFont(30);
            Font font1 = Main.numberFont(30);
            Pos pos = new Pos(0,100);
            Pos delta = new Pos(60,0);
            DrawTool.drawString(g,pos,character.getName(),Color.BLACK,font);
            pos = pos.add(0,30);
            DrawTool.drawString(g,pos,"Lv."+character.getLevel(),Color.BLACK,font0);
            pos = pos.add(0,50);
            DrawTool.drawString(g,pos,"攻擊",Color.BLACK,font0);
            DrawTool.drawString(g,pos.add(delta),String.valueOf((int)character.getAttribute(AttributeType.attack)),Color.BLACK,font1);
            pos = pos.add(0,30);
            DrawTool.drawString(g,pos,"防禦",Color.BLACK,font0);
            DrawTool.drawString(g,pos.add(delta),String.valueOf((int)character.getAttribute(AttributeType.defense)),Color.BLACK,font1);
            pos = pos.add(0,30);
            DrawTool.drawString(g,pos,"法抗",Color.BLACK,font0);
            DrawTool.drawString(g,pos.add(delta),String.valueOf((int)character.getAttribute(AttributeType.resistance)),Color.BLACK,font1);
            pos = pos.add(0,30);
            DrawTool.drawString(g,pos,"阻擋",Color.BLACK,font0);
            DrawTool.drawString(g,pos.add(delta),String.valueOf((int)character.getAttribute(AttributeType.block_count)),Color.BLACK,font1);
            pos = pos.add(0,30);
            DrawTool.drawString(g,pos,"生命",Color.BLACK,font0);
            DrawTool.drawString(g,pos.add(delta),(int)character.getHealth()+"/"+(int)character.getAttribute(AttributeType.max_health),Color.BLACK,font1);
            Skill skill = character.getMainSkill();
            if (skill != null) {
                pos = pos.add(0,30);
                DrawTool.drawString(g, pos, "SP", Color.BLACK, font0);
                DrawTool.drawString(g, pos.add(delta), (int)skill.getSp() + "/" + (int)skill.getSp_required(), Color.BLACK, font1);
            }
        }
        if (tutorialtext != null){
            tutorialtext.paint(new Pos(Main.default_width*0.5,20).sub(tutorialtext.getSize().mul(0.5)),g);
            DrawTool.drawString(g,new Pos(Main.default_width*0.5,20).add(tutorialtext.getSize().mul(0.5)),
                    "點任意處繼續",Color.gray,Main.textFont(20));
        }
        if (middletext != null){
            middletext.paint(Main.default_size.sub(middletext.getSize()).mul(0.5),g);
        }
        Pos textpos = Main.default_size.sub(Main.deployment_size,Main.deployment_size);
        DrawTool.drawString(g, textpos,String.valueOf(cost),Color.BLACK,Main.numberFont(30));
        Pos healthpos = new Pos(Main.default_width-80,100);
        DrawTool.drawString(g, healthpos,String.valueOf(tower_health),Color.BLACK,Main.numberFont(30));
        //System.out.println("paint "+(System.currentTimeMillis()-t));
        long enemy_count = characters.stream().filter((o)->o instanceof EnemyCharacter e && e.isOriginal()).count();
        DrawTool.drawString(g, new Pos(300,50),(spawn_enemy_count-enemy_count)+"/"+total_enemy_count,Color.BLACK,Main.numberFont(30));
    }
    public boolean isRunning(){
        return running && tutorialtext == null;
    }

    @Override
    public void onTick() {
        //long t = System.currentTimeMillis();
        super.onTick();
        if (starting){
            time += 1;
            if (time>=50){
                starting = false;
                running = true;
                tickcount = 0;
                costcounter = new TimeChecker(1);
            }
        }else if (isRunning() && !paused) {
            slowdowncounter++;
            boolean slowdown = selected_deployment!=null || waiting_deployment != null || selected_character != null;
            int count_required = slowdown?Main.mainWindow.getSpeedrate()*Main.slowdownrate:1;
            if (slowdowncounter>=count_required) {
                slowdowncounter = 0;
                if (costcounter.check()) addcost(1);
                List<GameCharacter> blockers = characters.stream().filter(GameCharacter::blockable).toList();
                List<GameCharacter> blockeds = characters.stream().filter(GameCharacter.Flag.walk.and((e) -> !e.blocked() && !e.isUnbalanced())).toList();
                for (GameCharacter character : blockeds) {
                    for (GameCharacter block : blockers) {
                        if (character.opposite(block) && character.collide(block)) {
                            block.block(character);
                        }
                    }
                }
                for (GameCharacter character : characters) {
                    if (character.isActivated()) {
                        character.onTick();
                        character.doMove();
                    }
                }
                if (!actionlist.isEmpty()) {
                    if (actionlist.getFirst().getKey() < getPlaytime()) {
                        GameAction action = Objects.requireNonNull(actionlist.poll()).getValue();
                        action.apply(this);
                        if (action instanceof GameAction.Spawn){
                            spawn_enemy_count++;
                        }
                    }
                } else {
                    if (characters.stream().noneMatch(Team.enemy)) {
                        this.mission_accomplished();
                    }
                }
                characters.removeIf((ch) -> !ch.isAlive());
                for (Deployment deployment : deployments) {
                    if (deployment.getCooldown() > 0) deployment.setCooldown(deployment.getCooldown() - 1);
                }
                tickcount++;
            }
        }
        for (Plate plate : layout) {
            if (plate.getStand() != null && !plate.getStand().isAlive()) plate.setStand(null);
        }
        activebtn.setDisplayed(false);
        removebtn.setDisplayed(false);
        if (selected_character != null && selected_character.isAlive() && selected_character.isActivated() && selected_character instanceof AllyCharacter) {
            Skill skill = selected_character.getMainSkill();
            if (skill != null && !skill.isAuto() && skill.canActivate()) {
                activebtn.setDisplayed(true);
            }
            removebtn.setDisplayed(true);
        }
        if (selected_character != null && !selected_character.isAlive()){
            selected_character = null;
        }
        //System.out.println("tick "+(System.currentTimeMillis()-t));
    }
    public void moveto(@NotNull GameCharacter character, @NotNull Pos target, boolean safe){
        if (character.hasFlag(GameCharacter.Flag.high)){
            character.setPos(target);
            return;
        }
        if (safe && character.blocked()) return;
        Pos[] targets = {target,new Pos(character.getPos().x(),target.y()),new Pos(target.x(),character.getPos().y())};
        Moveable gamearea = new Moveable(mapdelta,new Pos(layout.getWidth()*Main.block_size,layout.getHeight()*Main.block_size));
        for(Pos pos : targets){
            Moveable me = new Moveable(pos,character.getSize());
            if (!gamearea.include(me)) continue;
            boolean ok = true;
            Plate stand_plate = getPlate(pos.add(character.getSize().mul(0.5)));
            if (safe && stand_plate!=null && stand_plate.hasFlag(Plate.Flag.hole)) continue;
            for(Plate plate : layout){
                if (plate.hasFlag(Plate.Flag.high)){
                    if (me.collide(plate)) ok = false;
                }
                if (!ok) break;
            }
            if (ok){
                character.setPos(pos);
                return;
            }
        }
    }
    public void move(GameCharacter character, Pos delta, boolean safe) {
        moveto(character, character.getPos().add(delta), safe);
    }
    public void slowmove(GameCharacter character, Pos unit, double m, boolean safe){
        while(m>=1){
            m-=1;
            move(character,unit, safe);
        }
        move(character,unit.mul(m), safe);
    }
    public @Nullable BlockPos getBlockPos(@NotNull Pos pos){
        BlockPos r = pos.sub(mapdelta).div(Main.block_size).toBlockPos();
        if (r.x()<0||r.x()>=layout.getWidth()||r.y()<0||r.y()>= layout.getHeight()) return null;
        return r;
    }
    public @Nullable Plate getPlate(@Nullable BlockPos pos){
        if (pos==null) return null;
        return layout.getPlate(pos);
    }
    public @Nullable Plate getPlate(@NotNull Pos pos){
        return getPlate(getBlockPos(pos));
    }
    public Pos getBlockCenter(@NotNull BlockPos block){
        return block.toPos().add(0.5,0.5).mul(Main.block_size).add(mapdelta);
    }
    public double getPlaytime(){
        return ((double) tickcount) /Main.fps;
    }
    public long getActivateId(){
        return activateIterator++;
    }
    public void addDeployment(String name){
        AllyCharacter character = new AllyCharacter(this, name);
        addDeployment(character);
    }

    public void addDeployment(AllyCharacter character){
        CharacterBlock block = new CharacterBlock(character,new Pos(Main.default_width-1.5*Main.deployment_size, (1.5+characterBlocks.size())*Main.deployment_size));
        block.connect(()->{
            selected_character = character;
        });
        addPart(block,block.getActivePart(),block.getRemovePart());
        characterBlocks.add(block);
        Predicate<Plate> condition = plate -> false;
        Predicate<Plate> nodoor = Plate.Flag.blue.or(Plate.Flag.red).negate();
        switch (character.type){
            case "high" -> condition = Plate.Flag.high.and(nodoor);
            case "ground" -> condition = Plate.Flag.ground.and(nodoor);
            case "any" -> condition = Plate.Flag.ground.or(Plate.Flag.high).and(nodoor);
            case "walk", "main" -> condition = Plate.Flag.blue;
        }
        switch (character.type){
            case "high" -> character.addFlag(GameCharacter.Flag.high);
            case "ground" -> character.addFlag(GameCharacter.Flag.ground);
            case "walk" -> character.addFlag(GameCharacter.Flag.walk);
            case "main" -> character.addFlag(GameCharacter.Flag.main);
        }
        deployments.add(new Deployment(character.labelpath, character, character.cost, condition));
        Collections.sort(deployments);
    }
    public void redeploy(@NotNull AllyCharacter character){
        Predicate<Plate> condition = plate -> false;
        switch (character.type){
            case "high" -> condition = Plate.Flag.high;
            case "ground" -> condition = Plate.Flag.ground;
            case "any" -> condition = Plate.Flag.ground.or(Plate.Flag.high);
            case "walk", "main" -> condition = Plate.Flag.blue;
        }
        int cost = (int)Math.ceil(character.cost*(1+0.5*Math.min(2,character.getDeploycount())));
        Deployment deployment = new Deployment(character.labelpath, character, cost, condition);
        deployment.setCooldown(character.redeploy*Main.fps);
        deployments.add(deployment);
        Collections.sort(deployments);
    }
    public List<GameCharacter> queryTarget(int count, Predicate<GameCharacter> condition, Sorter<GameCharacter> sorter){
        sorter = sorter.add(GameCharacter::getPriority);
        condition = condition.and(CharacterState.invincible.negate());
        List<GameCharacter> r = characters.stream().filter(condition).sorted(sorter::cmp).toList();
        if (r.size()>count) r = r.subList(0,count);
        return r;
    }
    public List<GameCharacter> queryTarget(int count, Predicate<GameCharacter> condition) {
        return queryTarget(count, condition,o-> 0L);
    }
    public void addcost(int value){
        cost += value;
        cost = Math.max(0, cost);
        if (max_cost != 0) cost = Math.min(max_cost, cost);
    }
    public void damage_tower(int value){
        tower_health -= value;
        if (tower_health<=0){
            this.mission_faild();
        }
    }

    private void mission_faild() {
        running = false;
        middletext = Picture.fromText("mission faild", Main.textFont(100,Font.BOLD));
        ending(GameResult.faild);
    }

    private void mission_accomplished() {
        running = false;
        middletext = Picture.fromText("mission accomplished", Main.textFont(100,Font.BOLD));
        ending(tower_health==max_tower_health?GameResult.fullsuccess:GameResult.success);
    }
    private void ending(GameResult result) {
        Main.mainWindow.changePage(new SettlePage(name, result));
    }
    public @Nullable GameCharacter getSelectedCharacter() {
        if (selected_character!=null) return selected_character;
        else if (selected_deployment != null) return selected_deployment.getCharacter();
        else if (waiting_deployment != null) return waiting_deployment.getCharacter();
        else return null;
    }
    private void checkDeployability(){
        for (Plate plate : layout) {
            if (plate.getStand() != null && !plate.getStand().isAlive()) plate.setStand(null);
            if (!plate.hasFlag(Plate.Flag.unplaceable) && selected_deployment!=null && selected_deployment.getCondition().test(plate) && plate.getStand() == null) {
                plate.addFlag(Plate.Flag.canplace);
            }else{
                plate.removeFlag(Plate.Flag.canplace);
            }
        }
    }

    @Override
    public void keyPressed(@NotNull KeyEvent e) {
        pressed.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(@NotNull KeyEvent e) {
        pressed.remove(e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            pausebtn.click();
        }
        if (e.getKeyCode() == KeyEvent.VK_Q){
            removebtn.click();
        }
        if (e.getKeyCode() == KeyEvent.VK_E){
            activebtn.click();
        }
    }

    @Override
    public void mouseClicked(@NotNull MouseEvent e) {
        if(e.getButton() != MouseEvent.BUTTON1) return;
        selected_character = null;
        super.mouseClicked(e);
        if (running) {
            tutorialtext = null;
            Pos pos = Scaler.unscale(new Pos(e.getPoint()));
            BlockPos blockpos = getBlockPos(pos);
            Plate plate = getPlate(blockpos);
            if (blockpos != null && plate != null && plate.getStand()!=null){
                selected_character = plate.getStand();
            }
            /*
            for(CharacterBlock block : characterBlocks){
                if (block.include(pos) && block.getCharacter().isAlive() && block.getCharacter().isActivated()){
                    selected_character = block.getCharacter();
                }
            }
             */
            for(Plate obj : layout) obj.removeFlag(Plate.Flag.showarea);
            if (selected_character != null && selected_character.getAttackarea() != null){
                AttackArea attackArea = selected_character.getAttackarea();
                BlockPos block0 = selected_character.getBlockPos();
                for(BlockPos delta : attackArea){
                    BlockPos block = delta.add(block0);
                    Plate target = getPlate(block);
                    if (target != null){
                        target.addFlag(Plate.Flag.showarea);
                    }
                }
            }
            /*
            Pos d = new Pos(Main.default_width-1.5*Main.deployment_size, Main.deployment_size);
            Pos p = pos.sub(d);
            if (p.x()>=Main.deployment_size&&p.x()<Main.deployment_size*1.5){
                int h = (int)(p.y()/Main.deployment_size*2);
                if (h>=0&&h<2*characterBlocks.size()){
                    int i = h/2;
                    AllyCharacter character = characterBlocks.get(i).getCharacter();
                    if (h%2==0){
                        Skill skill = character.getMainSkill();
                        if (skill != null && !skill.isAuto() && skill.canActivate()){
                            skill.activate();
                        }
                    }else{
                        if (character.isActivated() && character.isAlive()){
                            character.retreat();
                        }
                    }
                }
            }
             */
        }
    }

    @Override
    public void mousePressed(@NotNull MouseEvent e) {
        if(e.getButton() != MouseEvent.BUTTON1) return;
        if (running) {
            Pos pos = Scaler.unscale(new Pos(e.getPoint()));
            if (waiting_deployment == null) {
                if (pos.y() > Main.default_height - Main.deployment_size && pos.y() < Main.default_height) {
                    int id = (int) ((Main.default_width - pos.x()) / Main.deployment_size);
                    if (id >= 0 && id < deployments.size()) {
                        id = deployments.size() - id - 1;
                        if (deployments.get(id).getCooldown() <= 0 && deployments.get(id).getCost() <= cost) {
                            selected_deployment = deployments.get(id);
                            GameCharacter character = selected_deployment.getCharacter();
                            character.setPos(pos.sub(character.getSize().mul(0.5)));
                            checkDeployability();
                        }
                    }
                }
            }else{
                BlockPos blockPos = waiting_deployment.getCharacter().getBlockPos();
                BlockPos block = getBlockPos(pos);
                if (!Objects.equals(block, blockPos)){
                    waiting_deployment = null;
                    current_attack_area = null;
                    for(Plate plate : layout)plate.removeFlag(Plate.Flag.showarea);
                }
            }
        }
    }

    @Override
    public void mouseReleased(@NotNull MouseEvent e) {
        if(e.getButton() != MouseEvent.BUTTON1) return;
        if (running) {
            Pos pos = Scaler.unscale(new Pos(e.getPoint()));
            BlockPos block = getBlockPos(pos);
            if (selected_deployment != null) {
                if (block != null && selected_deployment.getCost()<=cost) {
                    if (layout.getPlate(block).hasFlag(Plate.Flag.canplace)) {
                        AllyCharacter character = selected_deployment.getCharacter();
                        if (character.getAttackarea()!=null){
                            waiting_deployment = selected_deployment;
                        }else {
                            character.reset();
                            character.moveto(getBlockCenter(block));
                            character.setDisplayed(true);
                            character.activate();
                            characters.add(character);
                            deployments.remove(selected_deployment);
                            addcost(-selected_deployment.getCost());
                            Plate plate = layout.getPlate(block);
                            if (Objects.equals(character.type, "any")) {
                                if (plate.hasFlag(Plate.Flag.ground)) character.addFlag(GameCharacter.Flag.ground);
                                if (plate.hasFlag(Plate.Flag.high)) character.addFlag(GameCharacter.Flag.high);
                            }
                            if (character.hasFlag(GameCharacter.Flag.high) || character.hasFlag(GameCharacter.Flag.ground)) {
                                plate.setStand(character);
                            }
                            character.addDeploycount();
                        }
                    }
                }
                selected_deployment = null;
                for (Plate plate0 : layout) {
                    plate0.removeFlag(Plate.Flag.canplace);
                }
            }
            if(waiting_deployment != null) {
                AllyCharacter character = waiting_deployment.getCharacter();
                BlockPos blockPos = character.getBlockPos();
                if (!Objects.equals(block, blockPos)){
                    character.setAttackarea(current_attack_area);
                    current_attack_area = null;
                    character.reset();
                    character.moveto(getBlockCenter(blockPos));
                    character.setDisplayed(true);
                    character.activate();
                    characters.add(character);
                    deployments.remove(waiting_deployment);
                    addcost(-waiting_deployment.getCost());
                    Plate plate = layout.getPlate(blockPos);
                    if (Objects.equals(character.type, "any")) {
                        if (plate.hasFlag(Plate.Flag.ground)) character.addFlag(GameCharacter.Flag.ground);
                        if (plate.hasFlag(Plate.Flag.high)) character.addFlag(GameCharacter.Flag.high);
                    }
                    if (character.hasFlag(GameCharacter.Flag.high) || character.hasFlag(GameCharacter.Flag.ground)) {
                        plate.setStand(character);
                    }
                    character.addDeploycount();
                    waiting_deployment = null;
                    current_attack_area = null;
                    for(Plate plate0 : layout)plate0.removeFlag(Plate.Flag.showarea);
                }
            }
        }
    }

    @Override
    public void mouseDragged(@NotNull MouseEvent e) {
        if (running) {
            Pos pos = Scaler.unscale(new Pos(e.getPoint()));
            BlockPos block = getBlockPos(pos);
            if (selected_deployment != null) {
                if (block != null) {
                    if (layout.getPlate(block).hasFlag(Plate.Flag.canplace)) {
                        pos = getBlockCenter(block);
                    }
                }
                GameCharacter character = selected_deployment.getCharacter();
                character.moveto(pos);
                checkDeployability();
            }
            if (waiting_deployment != null){
                BlockPos blockPos = waiting_deployment.getCharacter().getBlockPos();
                if (!Objects.equals(block,blockPos)){
                    Pos facing = pos.sub(getBlockCenter(blockPos));
                    int rotate;
                    if (Math.abs(facing.x())>Math.abs(facing.y())){
                        if (facing.x()>0){
                            rotate = 0;
                        }else{
                            rotate = 2;
                        }
                    }else{
                        if(facing.y()>0){
                            rotate = 1;
                        }else{
                            rotate = 3;
                        }
                    }
                    AttackArea area = waiting_deployment.getCharacter().getAttackarea();
                    for (int i = 0;i<rotate;i++) {
                        area = area.rotate();
                    }
                    current_attack_area = area;
                    for(Plate plate : layout)plate.removeFlag(Plate.Flag.showarea);
                    BlockPos blockPos0 = waiting_deployment.getCharacter().getBlockPos();
                    for(BlockPos delta : current_attack_area){
                        BlockPos block0 = delta.add(blockPos0);
                        Plate target = getPlate(block0);
                        if (target != null){
                            target.addFlag(Plate.Flag.showarea);
                        }
                    }
                }else {
                    current_attack_area = null;
                    for(Plate plate0 : layout)plate0.removeFlag(Plate.Flag.showarea);
                }
            }
        }
    }
}
