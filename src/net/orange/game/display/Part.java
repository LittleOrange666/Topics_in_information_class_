package net.orange.game.display;

import net.orange.game.tools.Scaler;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class Part extends Moveable implements Paintable{
    public Part(Pos pos) {
        super(pos);
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    @Override
    public Pos getSize() {
        if (bindpicture && picture != null){
            return picture.getSize();
        }else{
            return super.getSize();
        }
    }

    private Picture picture = null;

    public boolean isBindpicture() {
        return bindpicture;
    }

    public void setBindpicture(boolean bindpicture) {
        this.bindpicture = bindpicture;
    }

    private boolean bindpicture = true;
    private boolean displayed = true;

    public boolean isDisplayed() {
        return displayed;
    }

    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    @Override
    public void paint(Graphics2D g) {
        if (isDisplayed() && picture != null) {
            picture.paint(getPos(),g);
        }
    }
    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }

    public void mouseDragged(MouseEvent e) {

    }

    public void mouseMoved(MouseEvent e) {

    }

    public void mouseWheelMoved(MouseWheelEvent e) {

    }
}
