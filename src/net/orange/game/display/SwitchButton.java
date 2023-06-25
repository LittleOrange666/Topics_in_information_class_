package net.orange.game.display;

import org.jetbrains.annotations.NotNull;

public class SwitchButton extends TheButton {
    private final Picture picture0;
    private final Picture picture1;
    private boolean on = false;
    private Runnable turnon = null;
    private Runnable turnoff = null;
    public SwitchButton(Pos pos, @NotNull Picture picture0, @NotNull Picture picture1) {
        super(pos, picture0);
        this.picture0 = picture0;
        this.picture1 = picture1;
        connect(this::onclick);
    }
    public void connecton(Runnable func) {
        this.turnon = func;
    }
    public void connectoff(Runnable func) {
        this.turnoff = func;
    }
    private void onclick(){
        on = !on;
        if (on) {
            setPicture(picture1);
            if (this.turnon != null){
                this.turnon.run();
            }
        }
        else {
            setPicture(picture0);
            if (this.turnoff != null){
                this.turnoff.run();
            }
        }
    }
}
