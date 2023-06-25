package net.orange.game.page;

import net.orange.game.Main;
import net.orange.game.data.object.UserDataPtr;
import net.orange.game.display.Picture;
import net.orange.game.display.Pos;
import net.orange.game.display.TheButton;
import net.orange.game.tools.DrawTool;
import net.orange.game.tools.Scaler;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class LoginPage extends Page{
    private final TheButton btn1;
    private final TheButton btn2;
    private final TheButton btn3;
    private final TheButton btn4;
    private boolean completed = false;
    public LoginPage(){
        TheButton btn0 = new TheButton(new Pos(400,25),
                Picture.fromText("+",Main.textFont(80),Color.green));
        btn0.connect(this::addlocal);
        btn1 = new TheButton(new Pos(1200,25),
                Picture.fromText("Login",Main.textFont(80),Color.green));
        btn1.connect(Main::trylogin);
        btn2 = new TheButton(new Pos(1200,25),
                Picture.fromText("Logout",Main.textFont(80),Color.red));
        btn2.connect(Main::logout);
        btn3 = new TheButton(new Pos(1500,25),
                Picture.fromText("+",Main.textFont(80),Color.green));
        btn3.connect(this::addonline);
        btn4 = new TheButton(new Pos(1600,800),
                Picture.fromText("Start",Main.textFont(80),Color.green));
        btn4.connect(this::complete);
        addPart(btn0,btn1,btn2,btn3,btn4);
        btn1.setDisplayed(!Main.hasLoggedIn());
        btn2.setDisplayed(Main.hasLoggedIn());
        btn3.setDisplayed(Main.hasLoggedIn());
        btn4.setDisplayed(Main.userDataList.getCurrent() != null);
    }
    public void addlocal(){
        hover(new LoginNameInput(false));
    }
    public void addonline(){
        hover(new LoginNameInput(true));
    }

    @Override
    public void paint(Graphics2D g) {
        super.paint(g);
        DrawTool.drawString(g,new Pos(200,100),"本地存檔：",Color.BLACK, Main.textFont(40));
        UserDataPtr p = Main.userDataList.getCurrent();
        int i = 0;
        for (UserDataPtr o : Main.userDataList.getOfflines()){
            DrawTool.drawString(g,new Pos(250,150+50*i),o.name(),Color.BLACK, Main.textFont(40));
            if (o.equals(p)){
                DrawTool.drawString(g,new Pos(220,150+50*i),"√",Color.green, Main.textFont(40));
            }
            i++;
        }
        DrawTool.drawString(g,new Pos(1000,100),"雲端存檔：",Color.BLACK, Main.textFont(40));
        i = 0;
        for (UserDataPtr o : Main.userDataList.getOnlines()){
            DrawTool.drawString(g,new Pos(1050,150+50*i),o.name(),Color.BLACK, Main.textFont(40));
            if (o.equals(p)){
                DrawTool.drawString(g,new Pos(1020,150+50*i),"√",Color.green, Main.textFont(40));
            }
            i++;
        }
        if (btn1.isDisplayed()){
            if (Main.hasLoggedIn()){
                btn1.setDisplayed(false);
                btn2.setDisplayed(true);
                btn3.setDisplayed(true);
                Main.userDataList.onlogin();
            }
        }else{
            if (!Main.hasLoggedIn()){
                btn1.setDisplayed(true);
                btn2.setDisplayed(false);
                btn3.setDisplayed(false);
                Main.userDataList.onlogout();
            }
        }
    }

    @Override
    public void mouseClicked(@NotNull MouseEvent e) {
        super.mouseClicked(e);
        if (!completed) {
            Pos pos = Scaler.unscale(new Pos(e.getPoint()));
            int i = (int) ((pos.y() - 100) / 50);
            ArrayList<UserDataPtr> l = new ArrayList<>();
            if (pos.x() < 600 && pos.x() > 200) {
                l = Main.userDataList.getOfflines();
            }
            if (pos.x() < 1400 && pos.x() > 1000) {
                l = Main.userDataList.getOnlines();
            }
            if (i >= 0 && i < l.size()) {
                Main.userDataList.select(l.get(i));
            } else {
                Main.userDataList.select(null);
            }
            btn4.setDisplayed(Main.userDataList.getCurrent() != null);
        }
    }
    public void complete(){
        completed = true;
        Main.userDataList.complete();
    }
}
