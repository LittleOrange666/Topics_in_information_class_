package net.orange.game.page;

import net.orange.game.Main;
import net.orange.game.data.object.ChoisePair;
import net.orange.game.data.object.Orb;
import net.orange.game.data.object.Weapon;
import net.orange.game.display.Picture;
import net.orange.game.display.Pos;
import net.orange.game.display.TheButton;
import net.orange.game.tools.DrawTool;
import net.orange.game.tools.Scaler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SelectingPage extends Page{
    private static final Picture block = Picture.rect(200,200,Color.white);
    private final PreparePage parent;
    private final List<Weapon> weapons;
    private final List<Orb> orbs;
    private List<Orb> displayed_orbs;
    private Weapon selected_weapon;
    private Orb selected_orb;
    private final int index;
    private final Picture check;
    private Pos mousePos = Pos.zero;
    private final ArrayList<TheButton> old_buttons = new ArrayList<>();
    private boolean needsUpdate = false;

    public SelectingPage(@NotNull PreparePage parent, @NotNull ChoisePair pair, int index) {
        this.parent = parent;
        selected_weapon = pair.weapon();
        selected_orb = pair.orb();
        this.weapons = Main.userData.getWeapons().stream().filter((o)->(index>0||o.isMain())&&(selected_weapon!=null&&o.getType().equals(selected_weapon.getType())||parent.unused(o))).toList();
        this.orbs = Main.userData.getOrbs().stream().filter((o)->o.equals(selected_orb)||parent.unused(o)).toList();
        updateDisplayedOrbs();
        this.index = index;
        TheButton button = new TheButton(new Pos(1600,800),
                Picture.fromText("確定", Main.textFont(100, Font.BOLD),Color.GREEN));
        addPart(button);
        button.connect(this::complete);
        TheButton button0 = new TheButton(new Pos(200,800),
                Picture.fromText("清除選擇", Main.textFont(100, Font.BOLD),Color.red));
        addPart(button0);
        button0.connect(this::unSelect);
        check = Picture.fromText("√",Main.textFont(50), Color.GREEN);
        for (int i = 0; i < weapons.size(); i++) {
            Pos pos = pos(i);
            TheButton btn = new TheButton(pos,block);
            int finalI = i;
            btn.connect(()->selectWeapon(finalI));
            addPart(btn);
        }
    }
    private void updateDisplayedOrbs(){
        if (selected_weapon == null){
            displayed_orbs = List.of();
        }else{
            displayed_orbs = orbs.stream().filter(selected_weapon.getType()).toList();
        }
        if (selected_orb!=null && !displayed_orbs.contains(selected_orb)){
            selected_orb = null;
        }
        old_buttons.forEach(this::removePart);
        old_buttons.clear();
        for (int i = 0; i < displayed_orbs.size(); i++) {
            Pos pos = pos(i).add(0,300);
            TheButton btn = new TheButton(pos,block);
            int finalI = i;
            btn.connect(()->selectOrb(finalI));
            addPart(btn);
            old_buttons.add(btn);
        }
    }
    public void unSelect(){
        selected_weapon = null;
        selected_orb = null;
    }
    public void selectWeapon(int i){
        selected_weapon=weapons.get(i);
        needsUpdate = true;
    }
    public void selectOrb(int i){
        selected_orb=displayed_orbs.get(i);
    }
    @Contract("_ -> new")
    private @NotNull Pos pos(int i){
        return new Pos(200+i*240,200);
    }

    @Override
    public void paint(Graphics2D g) {
        super.paint(g);
        for (int i = 0; i < weapons.size(); i++) {
            Weapon weapon = weapons.get(i);
            Pos pos = pos(i);
            weapon.getIcon().paint(pos,g);
            int level = weapon.getLevel();
            DrawTool.drawString(g,pos.add(100,200),"Lv."+level,Color.black,Main.textFont(30));
            if (weapon.equals(selected_weapon)){
                check.paint(pos.add(160,160),g);
            }
        }
        for (int i = 0; i < displayed_orbs.size(); i++) {
            Orb orb = displayed_orbs.get(i);
            Pos pos = pos(i).add(0,300);
            orb.getIcon().paint(pos,g);
            if (orb.equals(selected_orb)){
                check.paint(pos.add(160,160),g);
            }
        }
        if (selected_weapon != null){
            String name = selected_weapon.getName();
            DrawTool.drawString(g,new Pos(0,200),name,Color.BLACK,Main.textFont(50));
        }
        if (selected_orb != null){
            String name = selected_orb.getName();
            DrawTool.drawString(g,new Pos(0,500),name,Color.BLACK,Main.textFont(50));
        }
    }

    @Override
    public void onTick() {
        super.onTick();
        if (needsUpdate){
            updateDisplayedOrbs();
        }
    }

    public void complete(){
        parent.select(index, new ChoisePair(selected_weapon,selected_orb));
        Main.mainWindow.changePage(parent);
    }

    @Override
    public void mouseDragged(@NotNull MouseEvent e) {
        mousePos = Scaler.unscale(new Pos(e.getPoint()));
    }

    @Override
    public void mouseMoved(@NotNull MouseEvent e) {
        mousePos = Scaler.unscale(new Pos(e.getPoint()));
    }
}
