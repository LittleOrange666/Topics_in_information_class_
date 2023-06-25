package net.orange.game.page;

import net.orange.game.Main;
import net.orange.game.display.Picture;
import net.orange.game.display.Pos;
import net.orange.game.display.TheButton;
import net.orange.game.tools.DrawTool;

import java.awt.*;
import java.awt.event.KeyEvent;

public class LoginNameInput extends Page{
    private final boolean isonline;
    private String text = "";
    public LoginNameInput(boolean isonline){
        this.isonline = isonline;
        addPart(Picture.rect(1320,400,Color.gray).toPart(new Pos(300,400)));
        addPart(Picture.rect(1020,200,Color.black).toPart(new Pos(450,500)));
        addPart(new TheButton(new Pos(350,700),
                Picture.fromText("取消", Main.textFont(80),Color.red)).connect(this::cancel));
        addPart(new TheButton(new Pos(1450,700),
                Picture.fromText("確定", Main.textFont(80),Color.green)).connect(this::apply));
        addPart(Picture.rect(1320,200,Color.lightGray).toPart(new Pos(300,200)));
        addPart(Picture.fromText("建立新的"+(isonline?"雲端":"本地")+"存檔",Main.textFont(100),Color.black).toPart(new Pos(570,250)));
    }
    public void apply(){
        if (isonline){
            Main.userDataList.addonline(text);
        }else{
            Main.userDataList.addoffline(text);
        }
        back();
    }
    public void cancel(){
        back();
    }

    @Override
    public void paint(Graphics2D g) {
        super.paint(g);
        DrawTool.drawString(g,new Pos(500,650),text,Color.white,Main.textFont(100));
    }

    @Override
    public void keyTyped(KeyEvent e) {
        super.keyTyped(e);
        char c = e.getKeyChar();
        if (c == '\b'){
            if (text.length()>0){
                text = text.substring(0,text.length()-1);
            }
        }else if (c == '\n'){
            apply();
        }else {
            text += c;
        }
    }
}
