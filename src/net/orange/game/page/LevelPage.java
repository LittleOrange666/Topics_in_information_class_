package net.orange.game.page;

import net.orange.game.Main;
import net.orange.game.data.exception.DataIOException;
import net.orange.game.data.json.*;
import net.orange.game.display.OffsetGroup;
import net.orange.game.display.Picture;
import net.orange.game.display.Pos;
import net.orange.game.display.TheButton;
import net.orange.game.tools.DrawTool;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

public class LevelPage extends Page {
    public static final int level_per_line = 7;
    private JsonObject selected = null;
    private String selectedlevel = null;
    private final TheButton go;
    private int delta = 0;
    private final OffsetGroup offsetGroup = new OffsetGroup();
    private TheButton previewbtn;
    public LevelPage(){
        Font font = Main.textFont(50);
        go = new TheButton(new Pos(1650,900), Picture.fromText("GO!",font,Color.green));
        go.setDisplayed(false);
        go.connect(this::start);
        addPart(go);
        JsonObject object;
        try{
            object = Main.read("levels/list");
        } catch (DataIOException e) {
            e.printStackTrace();
            return;
        }
        JsonArray array = object.getArray("data");
        int i = 0;
        for(JsonObj obj : array){
            if (obj instanceof JsonString level){
                String id = level.getString();
                Color color = Color.GRAY;
                if (Main.userData.full_completed(id)){
                    color = Color.green;
                }else if (Main.userData.completed(id)){
                    color = Color.yellow;
                }else if (Main.userData.accessible(id)){
                    color = Color.cyan;
                }
                Picture picture = Picture.fromText(id,font,Color.black,color);
                int w = i%level_per_line;
                int h = i/level_per_line;
                TheButton button = new TheButton(new Pos(200+200*w,200+200*h), picture);
                addPart(button);
                button.setOffsetGroup(offsetGroup);
                button.connect(() -> selectLevel(id));
                i++;
            }
        }
        TheButton btn0 = new TheButton(new Pos(25, 25),
                Picture.fromText("退出遊戲", Main.textFont(80), Color.black, Color.white));
        btn0.connect(()-> hover(new ConfirmDialog("確定要退出遊戲嗎?",()->System.exit(0))));
        addPart(btn0);
        previewbtn = new TheButton(new Pos(1600,360),Picture.fromText("預覽",Main.textFont(40),Color.gray));
        previewbtn.connect(()-> hover(new LevelPreview(selected)));
        previewbtn.setDisplayed(false);
        addPart(previewbtn);
    }
    public void selectLevel(String level){
        try {
            selected = Main.read("levels/"+level);
            selectedlevel = level;
            go.setDisplayed(Main.userData.accessible(selectedlevel));
            previewbtn.setDisplayed(Main.userData.accessible(selectedlevel));
        } catch (DataIOException e) {
            e.printStackTrace();
        }
    }
    public void start(){
        Main.mainWindow.changePage(new PreparePage(selectedlevel));
    }

    @Override
    public void paint(Graphics2D g) {
        super.paint(g);
        if (selected!=null) {
            JsonObject base = selected.getObject("base");
            String name = base.getString("name");
            DrawTool.drawString(g,new Pos(1600,200),selectedlevel,Color.GRAY,Main.textFont(30));
            DrawTool.drawString(g,new Pos(1600,280),name,Color.BLACK,Main.textFont(50));
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        super.mouseWheelMoved(e);
        delta = Math.max(0,delta+e.getWheelRotation()*30);
        offsetGroup.setOffset(new Pos(0,-delta));
    }
}
