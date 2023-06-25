package net.orange.game.page;

import net.orange.game.Main;
import net.orange.game.character.AllyCharacter;
import net.orange.game.data.object.ChoisePair;
import net.orange.game.data.object.Orb;
import net.orange.game.data.object.Weapon;
import net.orange.game.display.Picture;
import net.orange.game.display.Pos;
import net.orange.game.display.TheButton;
import net.orange.game.tools.DrawTool;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PreparePage extends Page {
    private final GamePage gamePage;
    private ChoisePair main;

    public ArrayList<ChoisePair> getChoisePairs() {
        return choisePairs;
    }

    private final ArrayList<ChoisePair> choisePairs;
    private final Picture start_text;
    private final boolean mutable;

    public PreparePage(String level) {
        gamePage = new GamePage(level);
        mutable = gamePage.getChoices().isEmpty();
        main = new ChoisePair(null,null);
        choisePairs = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            choisePairs.add(null);
        }
        Picture card = Picture.rect(200, 400, Color.white, Color.BLACK, 5);
        start_text = Picture.fromText("START",Main.textFont(60, Font.BOLD),Color.WHITE);
        TheButton start_button = new TheButton(new Pos(1580,560),
                Picture.rect(200,400,Color.GREEN));
        start_button.connect(this::start);
        TheButton back_button = new TheButton(new Pos(10,10),
                Picture.fromText("×",Main.textFont(80, Font.BOLD),Color.RED));
        addPart(start_button,back_button);
        back_button.connect(this::back);
        addPart(Picture.fromText("本體",Main.textFont(40)).toPart(new Pos(140,80)));
        if (mutable) {
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 2; j++) {
                    if (i + j == 7) break;
                    TheButton button = new TheButton(new Pos(140 + 240 * i, 120 + 440 * j), card);
                    addPart(button);
                    int finalI = i;
                    int finalJ = j;
                    button.connect(() -> click(finalI * 2 + finalJ));
                }
            }
            ArrayList<ChoisePair> choises = Main.userData.getFormation();
            if(!choises.isEmpty()) main = choises.get(0);
            for (int i = 1; i < choises.size(); i++) {
                choisePairs.set(i-1,choises.get(i));
            }
        }else {
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 2; j++) {
                    if (i + j == 7) break;
                    addPart(card.toPart(new Pos(140 + 240 * i, 120 + 440 * j)));
                }
            }
            ArrayList<ChoisePair> choises = gamePage.getChoices();
            if(!choises.isEmpty()) main = choises.get(0);
            for (int i = 1; i < choises.size(); i++) {
                choisePairs.set(i-1,choises.get(i));
            }
        }
    }
    public void click(int index){
        ChoisePair pair = null;
        if (index == 0){
            pair = main;
        }else{
            if (index<=choisePairs.size()) pair = choisePairs.get(index-1);
        }
        Main.mainWindow.changePage(new SelectingPage(this,ChoisePair.noNull(pair),index));
    }
    public void select(int index, ChoisePair pair){
        if (index == 0){
            main = pair;
        }else{
            choisePairs.set(index-1,pair);
        }
    }

    @Override
    public void paint(Graphics2D g) {
        super.paint(g);
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 2; j++) {
                if (i+j==7) break;
                Pos pos = new Pos(140+240*i,120+440*j);
                int idx = i*2+j;
                ChoisePair pair = ChoisePair.noNull(idx==0?main:choisePairs.get(idx-1));
                Weapon weapon = pair.weapon();
                if (weapon!=null){
                    weapon.getIcon().paint(pos,g);
                    int level = weapon.getLevel();
                    DrawTool.drawString(g,pos.add(100,200),"Lv."+level,Color.black,Main.textFont(30));
                }
                Orb orb = pair.orb();
                if (orb!=null){
                    orb.getIcon().paint(pos.add(0,220),g);
                }
            }
        }
        start_text.paint(new Pos(1580,720),g);
        if (!mutable){
            DrawTool.drawString(g,new Pos(400,1050),"本次行動編隊不可修改",Color.BLACK,Main.textFont(100));
        }
    }

    @Override
    public void onTick() {
        super.onTick();
    }
    public void start(){
        AllyCharacter maincharacter;
        if (main.weapon()==null){
            maincharacter = new AllyCharacter(gamePage, "main");
        }else{
            maincharacter = new AllyCharacter(gamePage, "(本體)" + main.weapon().getName(), main.getData());
            maincharacter.type = "main";
            maincharacter.cost = 0;
            maincharacter.setAttackarea(null);
        }
        gamePage.addDeployment(maincharacter);
        for(ChoisePair pair : choisePairs){
            if (pair==null) continue;
            Weapon weapon = pair.weapon();
            if (weapon!=null){
                AllyCharacter character = new AllyCharacter(gamePage,weapon.getName(),pair.getData());
                gamePage.addDeployment(character);
            }
        }
        save();
        Main.mainWindow.changePage(gamePage);
    }
    private void save(){
        ArrayList<ChoisePair> formation = new ArrayList<>(List.of(main));
        formation.addAll(choisePairs);
        Main.userData.setFormation(formation);
    }
    public void back(){
        save();
        Main.mainWindow.changePage(new LevelPage());
    }

    public boolean unused(Weapon weapon){
        if (main!=null && main.weapon()!=null && main.weapon().getType()==weapon.getType()){
            return false;
        }
        for(ChoisePair pair : choisePairs){
            if (pair != null && pair.weapon()!=null && pair.weapon().getType()==weapon.getType()){
                return false;
            }
        }
        return true;
    }

    public boolean unused(Orb orb){
        if (main!=null && Objects.equals(main.orb(), orb)){
            return false;
        }
        for(ChoisePair pair : choisePairs){
            if (pair != null && Objects.equals(pair.orb(), orb)){
                return false;
            }
        }
        return true;
    }
}
