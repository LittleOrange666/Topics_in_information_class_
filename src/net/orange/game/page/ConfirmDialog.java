package net.orange.game.page;

import net.orange.game.Main;
import net.orange.game.display.Picture;
import net.orange.game.display.Pos;
import net.orange.game.display.TheButton;

import java.awt.*;
import java.util.function.Consumer;

public class ConfirmDialog extends Page{
    private final Consumer<Boolean> ret;
    public ConfirmDialog(String text, Consumer<Boolean> ret){
        this.ret = ret;
        addPart(Picture.rect(1320,400, Color.gray).toPart(new Pos(300,400)));
        addPart(new TheButton(new Pos(350,550),
                Picture.fromText("取消", Main.textFont(160),Color.red)).connect(this::cancel));
        addPart(new TheButton(new Pos(1250,550),
                Picture.fromText("確定", Main.textFont(160),Color.green)).connect(this::apply));
        addPart(Picture.rect(1320,200,Color.lightGray).toPart(new Pos(300,200)));
        Picture o = Picture.fromText(text,Main.textFont(100),Color.black);
        addPart(o.toPart(new Pos(300+(1320-o.getSize().x())/2,250)));
    }
    public ConfirmDialog(String text, Runnable ifyes){
        this(text,(r)->{
            if (r) ifyes.run();
        });
    }
    public void apply(){
        back();
        ret.accept(true);
    }
    public void cancel(){
        back();
        ret.accept(false);
    }
}
