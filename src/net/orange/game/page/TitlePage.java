package net.orange.game.page;

import net.orange.game.Main;
import net.orange.game.data.object.UserDataPtr;
import net.orange.game.display.Picture;
import net.orange.game.display.Pos;
import net.orange.game.display.TheButton;
import net.orange.game.tools.DrawTool;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.MouseEvent;

public class TitlePage extends Page{
    private boolean clicked = false;
    private int time = 0;
    private final boolean selected;
    private boolean locked = false;

    public TitlePage(){
        addPart(new Picture("background.png").toPart(Pos.zero));
        TheButton btn0 = new TheButton(new Pos(1575, 25),
                Picture.fromText("變更登入", Main.textFont(80), Color.black, Color.gray));
        btn0.connect(()->{
            Main.mainWindow.changePage(new LoginPage());
            locked = true;
        });
        TheButton btn1 = new TheButton(new Pos(25, 25),
                Picture.fromText("退出遊戲", Main.textFont(80), Color.black, Color.gray));
        btn1.connect(()->{
            locked = true;
            System.exit(0);
        });
        addPart(btn0,btn1);
        selected = Main.userDataList.getCurrent() != null;
        if (selected) {
            Main.userDataList.read();
            UserDataPtr p = Main.userDataList.getCurrent();
            addPart(Picture.fromText("目前登陸："+p.name()+(p.isonline()?"(雲端存檔)":"(本地存檔)"),Main.textFont(80),Color.white).toPartCenter(new Pos(960,700)));
        }
        addPart(Picture.fromText("Top to Start",Main.textFont(100, Font.BOLD),Color.white).toPartCenter(new Pos(960,800)));
    }

    @Override
    public void onTick() {
        if (!selected) {
            Main.mainWindow.changePage(new LoginPage());
        }
        super.onTick();
        if (clicked && !locked) {
            time++;
            if (time >= 50) Main.mainWindow.changePage(new LevelPage());
            else {
                float alpha = 1.0F - time * 0.02F;
                Main.mainWindow.setAlpha(alpha);
            }
        }
    }
    @Override
    public void mouseClicked(@NotNull MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) return;
        super.mouseClicked(e);
        clicked = true;
    }
}
