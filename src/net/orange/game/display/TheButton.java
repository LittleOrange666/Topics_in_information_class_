package net.orange.game.display;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.function.Predicate;

public class TheButton extends Part{
    private Runnable func = null;

    public Predicate<TheButton> getCondition() {
        return condition;
    }

    public void setCondition(Predicate<TheButton> condition) {
        this.condition = condition;
    }

    private Predicate<TheButton> condition = null;
    public TheButton(Pos pos, @NotNull Picture picture) {
        super(pos);
        setPicture(picture);
    }
    public TheButton connect(Runnable func) {
        this.func = func;
        return this;
    }
    public final void click(){
        if(isDisplayed() && (condition == null || condition.test(this))) {
            if (func != null) {
                func.run();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        click();
    }
}
